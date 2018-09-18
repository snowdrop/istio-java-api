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
import me.snowdrop.istio.api.mixer.config.descriptor.ValueType;
import me.snowdrop.istio.api.model.v1.cexl.parser.CEXLLexer;
import me.snowdrop.istio.api.model.v1.cexl.parser.CEXLParser;
import me.snowdrop.istio.api.model.v1.cexl.parser.CEXLTypeResolver;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
@Buildable(builderPackage = "io.fabric8.kubernetes.api.builder", generateBuilderPackage = true, editableEnabled = false)
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

    public static TypedValue from(String value) {
        // Get our lexer
        CEXLLexer lexer = new CEXLLexer(CharStreams.fromString(value));

        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Pass the tokens to the parser
        CEXLParser parser = new CEXLParser(tokens);

        // Specify our entry point
        final CEXLParser.ExpressionContext context = parser.expression();

        // Walk it and attach our listener
        ParseTreeWalker walker = new ParseTreeWalker();
        final CEXLTypeResolver resolver = new CEXLTypeResolver();
        walker.walk(resolver, context);

        return new TypedValue(resolver.getExpressionType(), value);
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
