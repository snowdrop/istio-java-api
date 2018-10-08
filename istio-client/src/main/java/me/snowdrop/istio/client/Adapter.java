package me.snowdrop.istio.client;

import java.util.List;

import io.fabric8.kubernetes.client.KubernetesClient;
import me.snowdrop.istio.api.IstioResource;

@Deprecated
public interface Adapter {
    List<IstioResource> createCustomResources(IstioResource... resources);

    List<IstioResource> createOrReplaceCustomResources(IstioResource... resources);

    Boolean deleteCustomResources(IstioResource resource);

    KubernetesClient getKubernetesClient();
}
