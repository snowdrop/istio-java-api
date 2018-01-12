/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.cexl;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.sundr.builder.annotations.Buildable;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.snowdrop.istio.api.model.v1.mixer.config.descriptor.ValueType;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
@Buildable(builderPackage = "me.snowdrop.istio.api.builder", generateBuilderPackage = true, editableEnabled = false)
@EqualsAndHashCode
@ToString
@JsonSerialize(using = TypedValue.TypedValueSerializer.class)
public class TypedValue {
    private final ValueType type;
    private String expression;

    public TypedValue(ValueType type, String expression) {
        this.type = type;
        this.expression = expression;
    }

    @JsonIgnore
    public ValueType getType() {
        return type;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    static class TypedValueSerializer extends JsonSerializer<TypedValue> {

        @Override
        public void serialize(TypedValue value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.expression);
        }
    }
}
