package me.snowdrop.istio.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author shalk
 * @create 2020-11-30
 */
public class DurationTest {
    @Test
    public void testDeserializer() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Duration du = new Duration(100_000_000, 1L);
        final String s1 = om.writeValueAsString(du);
        assertThat(s1).isEqualTo("\"1s100ms\"");
    }

    @Test
    public void testSerializer() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        String s = "\"2s100ms\"";
        final Duration o = om.readerFor(Duration.class).readValue(s);
        assertThat(o.getNanos()).isEqualTo(100_000_000);
        assertThat(o.getSeconds()).isEqualTo(2L);
    }
}
