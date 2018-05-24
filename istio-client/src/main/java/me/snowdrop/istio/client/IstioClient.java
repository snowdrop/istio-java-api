package me.snowdrop.istio.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.client.KubernetesClient;
import me.snowdrop.istio.api.model.IstioResource;

import static me.snowdrop.istio.api.internal.IstioSpecRegistry.getCRDNameFor;

public class IstioClient {

    private final static Pattern DOCUMENT_DELIMITER = Pattern.compile("---");
    private final static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    private static final String KIND = "kind";

    private final Adapter client;

    public IstioClient(Adapter client) {
        this.client = client;
    }

    public List<IstioResource> registerCustomResources(final String specFileAsString) {
        List<IstioResource> results = new ArrayList<>();
        String[] documents = DOCUMENT_DELIMITER.split(specFileAsString);

        for (String document : documents) {
            try {
                document = document.trim();
                if (!document.isEmpty()) {
                    final Map<String, Object> resourceYaml = objectMapper.readValue(document, Map.class);

                    if (resourceYaml.containsKey(KIND)) {
                        final String kind = (String) resourceYaml.get(KIND);
                        getCRDNameFor(kind).orElseThrow(() -> new IllegalArgumentException(String.format("%s is not a known Istio resource.", kind)));
                        final IstioResource resource = objectMapper.convertValue(resourceYaml, IstioResource.class);
                        results.add(resource);
                    } else {
                        throw new IllegalArgumentException(String.format("%s is not specified in provided resource.", KIND));
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        switch (documents.length) {
            case 0:
                return Collections.emptyList();
            case 1:
                return client.createCustomResources(results.get(0));
            default:
                return client.createCustomResources(results.toArray(new IstioResource[results.size()]));
        }
    }

    public List<IstioResource> registerCustomResources(final InputStream resource) {
        return registerCustomResources(readSpecFileFromInputStream(resource));
    }


    public IstioResource registerCustomResource(final IstioResource resource) {
        return client.createCustomResources(resource).get(0);
    }

    public IstioResource registerOrUpdateCustomResource(final IstioResource resource) {
        return client.createOrReplaceCustomResources(resource).get(0);
    }

    public Boolean unregisterCustomResource(final IstioResource istioResource) {
        return client.deleteCustomResources(istioResource);
    }

    private static String readSpecFileFromInputStream(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return outputStream.toString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read InputStream.", e);
        }
    }

    public KubernetesClient getKubernetesClient() {
        return client.getKubernetesClient();
    }
}
