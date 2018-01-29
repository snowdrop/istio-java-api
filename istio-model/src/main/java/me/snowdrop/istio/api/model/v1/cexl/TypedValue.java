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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
@JsonDeserialize(using = TypedValue.TypedValueDeserializer.class)
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

    public static TypedValue from(String value, String attributeName) {
        // todo: check that evaluated value is of proper type based on attribute name as defined in attribute vocabulary
        AttributeVocabulary.AttributeInfo info = AttributeVocabulary.getInfoFor(attributeName);
        final TypedValue evaluated = from(value);

        if (!info.type.equals(evaluated.type)) {
            throw new IllegalArgumentException(
                    String.format("'%s' attribute is supposed to be of type '%s' but given value '%s' evaluates as a '%s' type",
                            attributeName, info.type, value, evaluated.type));
        }
        
        return evaluated;
    }

    public static TypedValue from(String value) {
        // todo: parse expression and determine type instead of hardcoding ValueType.STRING
        return new TypedValue(ValueType.STRING, value);
    }

    static class TypedValueSerializer extends JsonSerializer<TypedValue> {

        @Override
        public void serialize(TypedValue value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.expression);
        }
    }

    static class TypedValueDeserializer extends JsonDeserializer<TypedValue> {
        @Override
        public TypedValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return TypedValue.from(p.getText());
        }
    }

}
