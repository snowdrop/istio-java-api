package me.snowdrop.istio.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import me.snowdrop.istio.api.internal.IstioSpecRegistry;
import me.snowdrop.istio.api.model.DoneableIstioResource;
import me.snowdrop.istio.api.model.IstioResource;

public class KubernetesAdapter implements Adapter {

    private KubernetesClient client;

    public KubernetesAdapter(KubernetesClient kubernetesClient) {
        this.client = kubernetesClient;
    }

    public List<IstioResource> createCustomResources(IstioResource... resources) {
        if(resources != null) {
            List<IstioResource> results = new ArrayList<>(resources.length);

            for (IstioResource resource : resources) {
                final String crdName = IstioSpecRegistry.getCRDNameFor(resource.getKind());
                final CustomResourceDefinition customResourceDefinition = client.customResourceDefinitions().withName(crdName).get();
                if (customResourceDefinition == null) {
                    throw new IllegalArgumentException(String.format("Custom Resource Definition %s is not found in cluster %s",
                            crdName, client.getMasterUrl()));
                }

                final IstioResource result = client.customResources(customResourceDefinition, IstioResource.class, KubernetesResourceList.class, DoneableIstioResource.class)
                        .inNamespace(client.getNamespace())
                        .create(resource);
                results.add(result);
            }

            return results;
        }

        return Collections.emptyList();
    }
}
