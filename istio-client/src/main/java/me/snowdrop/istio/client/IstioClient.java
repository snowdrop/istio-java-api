package me.snowdrop.istio.client;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import me.snowdrop.istio.api.internal.IstioSpecRegistry;
import me.snowdrop.istio.api.model.DoneableIstioResource;
import me.snowdrop.istio.api.model.IstioResource;
import me.snowdrop.istio.api.model.IstioResourceList;
import me.snowdrop.istio.util.YAML;

public class IstioClient {

    private final Adapter client;

    public IstioClient(Adapter client) {
        this.client = client;
    }

    public List<IstioResource> registerCustomResources(final String specFileAsString) {
        List<IstioResource> results = YAML.loadIstioResources(specFileAsString, IstioResource.class);

        switch (results.size()) {
            case 0:
                return Collections.emptyList();
            case 1:
                return client.createCustomResources(results.get(0));
            default:
                return client.createCustomResources(results.toArray(new IstioResource[results.size()]));
        }
    }

    public List<IstioResource> registerCustomResources(final InputStream resource) {
        return registerCustomResources(YAML.writeStreamToString(resource));
    }

    public List<IstioResource> getResources(final String kind) {
        final String crdName = IstioSpecRegistry.getCRDNameFor(kind)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown kind %s", kind)));

        final KubernetesClient client = getKubernetesClient();
        final CustomResourceDefinition customResourceDefinition = client.customResourceDefinitions().withName(crdName).get();

        if (customResourceDefinition == null) {
            throw new IllegalArgumentException(String.format("Custom Resource Definition %s is not found in cluster %s",
                    crdName, client.getMasterUrl()));
        }

        final KubernetesResourceList list = client.customResources(customResourceDefinition, IstioResource.class, IstioResourceList.class, DoneableIstioResource.class)
                .inNamespace(client.getNamespace())
                .list();
        return list.getItems();
    }

    public List<IstioResource> getResourcesLike(final IstioResource resource) {
        if (resource == null) {
            return Collections.emptyList();
        }
        return getResources(resource.getKind());
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

    public KubernetesClient getKubernetesClient() {
        return client.getKubernetesClient();
    }
}
