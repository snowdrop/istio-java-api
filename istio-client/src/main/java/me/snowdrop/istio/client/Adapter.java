package me.snowdrop.istio.client;

import java.util.List;

import io.fabric8.kubernetes.client.KubernetesClient;
import me.snowdrop.istio.api.model.IstioResource;

public interface Adapter {
    List<IstioResource> createCustomResources(IstioResource... resources);

    KubernetesClient getKubernetesClient();
}
