package me.snowdrop.istio.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.snowdrop.istio.api.model.IstioResource;

import static me.snowdrop.istio.api.internal.IstioSpecRegistry.getCRDNameFor;

public class IstioClient {

    private final static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    private static final String KIND = "kind";

    private final Adapter client;

    public IstioClient(Adapter client) {
        this.client = client;
    }

    public Optional<IstioResource> registerCustomResource(final String resource) {
        try {
            final Map<String, Object> resourceYaml = objectMapper.readValue(resource, Map.class);
            return apply(resourceYaml);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Optional<IstioResource> registerCustomResource(final InputStream resource) {
        try {
            final Map<String, Object> resourceYaml = objectMapper.readValue(resource, Map.class);
            return apply(resourceYaml);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Optional<IstioResource> registerCustomResource(final IstioResource resource) {
        final String kind = resource.getKind();
        final String crdName = getCRDNameFor(kind);
        if (crdName != null) {
            return Optional.of(client.createCustomResource(crdName, resource));
        } else {
            throw new IllegalArgumentException(String.format("%s is not a known Istio resource.", kind));
        }
    }

    @SuppressWarnings("unchecked")
    private Optional<IstioResource> apply(Map<String, Object> resourceYaml) {
        if (resourceYaml.containsKey(KIND)) {
            final String kind = (String) resourceYaml.get(KIND);
            final String crdName = getCRDNameFor(kind);
            if (crdName != null) {
                final IstioResource resource = objectMapper.convertValue(resourceYaml, IstioResource.class);

                return Optional.of(client.createCustomResource(crdName, resource));
            } else {
                throw new IllegalArgumentException(String.format("%s is not a known Istio resource.", kind));
            }
        } else {
            throw new IllegalArgumentException(String.format("%s is not specified in provided resource.", KIND));
        }
    }
}
