/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package me.snowdrop.istio.api.internal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class MixerSupportRegistry {
	private Map<String, Class<?>> registry;

	public Map<String, Class<?>> loadFromProperties(String propertiesFileName) {
		if (registry == null) {
			registry = new HashMap<>(17);
			Properties props = new Properties();

			try (final InputStream inputStream =
						  Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFileName +
								  ".properties")) {
				props.load(inputStream);
			} catch (Exception e) {
				throw new RuntimeException("Couldn't load " + propertiesFileName + ".properties from classpath", e);
			}

			registry = props.entrySet().parallelStream().collect(Collectors.toMap(k -> k.getKey().toString(), v -> {
				final String className = v.getValue().toString();
				try {
					return Thread.currentThread().getContextClassLoader().loadClass(className);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(className + " doesn't appear to be a known Istio " + propertiesFileName + " class", e);
				}
			}));
		}
		return registry;
	}

	public Class<?> getImplementationClass(String id) {
		final Class<?> targetClass = registry.get(id);
		if (targetClass == null) {
			throw new RuntimeException("No implementation class associated with " + id);
		}
		return targetClass;
	}
}
