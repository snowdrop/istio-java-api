/*
 * *
 *  * Copyright (C) 2018 Red Hat, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */
package me.snowdrop.istio.api;

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
    private final static DateTimeFormatter FORMATTER = ISODateTimeFormat.dateTime();

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
