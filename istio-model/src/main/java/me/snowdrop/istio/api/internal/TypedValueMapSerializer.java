/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.internal;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.snowdrop.istio.api.model.v1.cexl.TypedValue;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class TypedValueMapSerializer extends JsonSerializer<Map<String, TypedValue>> {
    @Override
    public void serialize(Map<String, TypedValue> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        value.forEach((s, typedValue) -> {
            try {
                gen.writeStringField(s, typedValue.getExpression());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
