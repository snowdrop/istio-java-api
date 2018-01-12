package me.snowdrop.istio.applier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

public class ObjectMapperFactory {

    public static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

}
