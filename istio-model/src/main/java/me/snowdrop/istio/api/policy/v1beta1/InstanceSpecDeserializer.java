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
package me.snowdrop.istio.api.policy.v1beta1;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.snowdrop.istio.api.internal.MixerSupportRegistry;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class InstanceSpecDeserializer extends JsonDeserializer<InstanceSpec> {
	private static final MixerSupportRegistry registry = new MixerSupportRegistry();

	static {
		registry.loadFromProperties("templates");
	}

	public InstanceSpecDeserializer() {
	}

	@Override
	public InstanceSpec deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
		ObjectCodec codec = jsonParser.getCodec();
		final ObjectNode node = codec.readTree(jsonParser);

		final InstanceSpec spec = new InstanceSpec();
		final Class<? extends InstanceSpec> specClass = spec.getClass();
		final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
		while (fields.hasNext()) {
			final Map.Entry<String, JsonNode> field = fields.next();
			final String fieldName = field.getKey();

			try {
				final Field targetClassField = specClass.getDeclaredField(fieldName);
				final Class<?> targetClass;
				if (fieldName.equals("params")) {
					final String compiledTemplate = node.findValue("compiledTemplate").textValue();
					targetClass = getImplementationClass(compiledTemplate);
				} else {
					final Type type = targetClassField.getAnnotatedType().getType();
					targetClass = (Class<?>) type;
				}
				targetClassField.setAccessible(true);

				final Object deserialized = codec.treeToValue(field.getValue(), targetClass);
				targetClassField.set(spec, deserialized);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return spec;
	}


	private Class<? extends InstanceParams> getImplementationClass(String compiledTemplate) {
		return registry.getImplementationClass(compiledTemplate).asSubclass(InstanceParams.class);
	}
}
