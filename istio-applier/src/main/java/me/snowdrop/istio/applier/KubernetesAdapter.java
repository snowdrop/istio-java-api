package me.snowdrop.istio.applier;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import me.snowdrop.istio.api.model.IstioResource;

public class KubernetesAdapter implements Adapter {

    private KubernetesClient client;

    public KubernetesAdapter(KubernetesClient kubernetesClient) {
        this.client = kubernetesClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IstioResource> T createCustomResource(T resource, Applier<T> applier) {
        final String crdName = applier.getCustomResourceDefinitionName();

        final CustomResourceDefinition customResourceDefinition = client.customResourceDefinitions().withName(crdName).get();
        if (customResourceDefinition == null) {
            throw new IllegalArgumentException(String.format("Custom Resource Definition %s is not found in cluster %s",
                    crdName, client.getMasterUrl()));
        }

        return client.customResources(customResourceDefinition, applier.getResourceClass(), KubernetesResourceList.class, applier.getDoneableClass()).create(resource);
    }
}
