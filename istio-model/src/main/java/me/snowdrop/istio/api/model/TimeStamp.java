/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.sundr.builder.annotations.Buildable;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
@JsonDeserialize(using = TimeStamp.Deserializer.class)
@JsonSerialize(using = TimeStamp.Serializer.class)
@ToString
@EqualsAndHashCode
public class TimeStamp implements Serializable {
    private Integer nanos;
    private Long seconds;
    final static DateTimeFormatter FORMATTER = ISODateTimeFormat.dateTime();

    public TimeStamp() {
    }

    @Buildable(generateBuilderPackage = true, builderPackage = "io.fabric8.kubernetes.api.builder", editableEnabled = false, validationEnabled = true)
    public TimeStamp(Integer nanos, Long seconds) {
        this.nanos = nanos;
        this.seconds = seconds;
    }

    public Integer getNanos() {
        return nanos;
    }

    public Long getSeconds() {
        return seconds;
    }

    public static class Deserializer extends JsonDeserializer<TimeStamp> {

        @Override
        public TimeStamp deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            ObjectCodec oc = parser.getCodec();
            JsonNode node = oc.readTree(parser);

            final DateTime dateTime = FORMATTER.parseDateTime(node.asText());
            return new TimeStamp(0, dateTime.getMillis() / 1000);
        }
    }

    public static class Serializer extends JsonSerializer<TimeStamp> {
        @Override
        public void serialize(TimeStamp value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(FORMATTER.print(new DateTime(value.getSeconds() * 1000)));
        }
    }
}
