package me.snowdrop.istio.applier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.snowdrop.istio.api.model.IstioResource;

public class IstioExecutor {

    private static final String KIND = "kind";
    private List<Applier> istioResources = new ArrayList<>();

    public IstioExecutor(Adapter client) {
        istioResources.addAll(Arrays.asList(
            new RouteRuleApplier(client),
            new DestinationPolicyApplier(client),
            new EgressRuleApplier(client)));
    }

    public Optional<IstioResource> registerCustomResource(final String resource) {
        try {
            final Map<String, Object> resourceYaml = ObjectMapperFactory.objectMapper.readValue(resource, Map.class);
            return apply(resourceYaml);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Optional<IstioResource> registerCustomResource(final InputStream resource) {
        try {
            final Map<String, Object> resourceYaml = ObjectMapperFactory.objectMapper.readValue(resource, Map.class);
            return apply(resourceYaml);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Optional<IstioResource> apply(Map<String, Object> resourceYaml) {
        if (resourceYaml.containsKey(KIND)) {

            final Optional<Applier> applier = istioResources.stream()
                .filter(a -> a.canApply((String) resourceYaml.get(KIND)))
                .findFirst();

           if (applier.isPresent()) {
               return Optional.of(applier.get().apply(resourceYaml));
            }

            return Optional.empty();

        } else {
            throw new IllegalArgumentException(String.format("%s is not specified in provided resource.", KIND));
        }
    }
}
