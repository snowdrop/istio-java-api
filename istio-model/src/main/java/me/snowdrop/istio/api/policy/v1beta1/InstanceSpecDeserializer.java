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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import me.snowdrop.istio.api.internal.MixerResourceDeserializer;
import me.snowdrop.istio.api.internal.MixerSupportRegistry;

import java.io.IOException;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class InstanceSpecDeserializer extends JsonDeserializer<InstanceSpec> implements MixerResourceDeserializer<InstanceSpec, InstanceParams> {
	private static final MixerSupportRegistry registry = new MixerSupportRegistry();

	static {
		registry.loadFromProperties("templates");
	}

	public InstanceSpecDeserializer() {
	}

	@Override
	public InstanceSpec deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		return (InstanceSpec) deserialize(jsonParser, MixerResourceDeserializer.INSTANCE_TYPE_FIELD);
	}


	@Override
	public InstanceSpec newInstance() {
		return new InstanceSpec();
	}

	public Class<? extends InstanceParams> getImplementationClass(String compiledTemplate) {
		return registry.getImplementationClass(compiledTemplate).asSubclass(InstanceParams.class);
	}
}
