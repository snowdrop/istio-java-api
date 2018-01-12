package me.snowdrop.istio.applier;

import io.fabric8.kubernetes.api.model.Doneable;
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
    public IstioResource createCustomResource(String customResourceDef, IstioResource istioResource,
        Class<? extends Doneable> done) {

        final CustomResourceDefinition customResourceDefinition =
            client.customResourceDefinitions().withName(customResourceDef).get();

        if (customResourceDefinition == null) {
            throw new IllegalArgumentException(String.format("Custom Resource Definition %s is not found in cluster %s",
                customResourceDef, client.getMasterUrl()));
        }

        final IstioResource createdIstioResource =
            (IstioResource) client.customResources(customResourceDefinition, IstioResource.class, KubernetesResourceList.class, done)
                .create(istioResource);

        return createdIstioResource;
    }
}
