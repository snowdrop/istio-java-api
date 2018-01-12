package me.snowdrop.istio.applier;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import me.snowdrop.istio.api.model.DoneableIstioBaseResource;
import me.snowdrop.istio.api.model.IstioBaseResource;

public class KubernetesAdapter implements Adapter {

    private KubernetesClient client;

    public KubernetesAdapter(KubernetesClient kubernetesClient) {
        this.client = kubernetesClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IstioBaseResource createCustomResource(String crdName, IstioBaseResource resource) {

        final CustomResourceDefinition customResourceDefinition = client.customResourceDefinitions().withName(crdName).get();
        if (customResourceDefinition == null) {
            throw new IllegalArgumentException(String.format("Custom Resource Definition %s is not found in cluster %s",
                    crdName, client.getMasterUrl()));
        }

        return client.customResources(customResourceDefinition, IstioBaseResource.class, KubernetesResourceList.class, DoneableIstioBaseResource.class).
                inNamespace("istio-system")
                .create(resource);
    }
}
