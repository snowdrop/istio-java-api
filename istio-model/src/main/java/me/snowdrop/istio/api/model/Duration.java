/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
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
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
@JsonDeserialize(using = Duration.Deserializer.class)
@JsonSerialize(using = Duration.Serializer.class)
@ToString
@EqualsAndHashCode
public class Duration implements Serializable {
    private Map<String, Object> additionalProperties = new HashMap<>();
    private Integer nanos;
    private Long seconds;
    final static PeriodFormatter FORMATTER = new PeriodFormatterBuilder()
            .printZeroNever()
            .appendHours()
            .appendSuffix("h")
            .appendMinutes()
            .appendSuffix("m")
            .appendSeconds()
            .appendSuffix("s")
            .appendMillis()
            .appendSuffix("ms")
            .toFormatter();

    /**
     * No args constructor for serialization
     */
    public Duration() {
    }

    @Buildable(generateBuilderPackage = true, builderPackage = "io.fabric8.kubernetes.api.builder", editableEnabled = false, validationEnabled = true)
    public Duration(Integer nanos, Long seconds) {
        this.nanos = nanos;
        this.seconds = seconds;
    }

    public Integer getNanos() {
        return nanos;
    }

    public Long getSeconds() {
        return seconds;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public static class Deserializer extends JsonDeserializer<Duration> {

        @Override
        public Duration deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            ObjectCodec oc = parser.getCodec();
            JsonNode node = oc.readTree(parser);
            final Period period = FORMATTER.parsePeriod(node.asText());
            return new Duration(0, (long) period.toStandardSeconds().getSeconds());
        }
    }

    public static class Serializer extends JsonSerializer<Duration> {
        @Override
        public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(FORMATTER.print(Seconds.seconds(value.seconds.intValue())));
        }
    }
}
