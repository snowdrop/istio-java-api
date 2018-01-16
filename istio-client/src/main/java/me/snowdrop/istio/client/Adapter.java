package me.snowdrop.istio.client;

import java.util.List;

import me.snowdrop.istio.api.model.IstioResource;

public interface Adapter {
    List<IstioResource> createCustomResources(IstioResource... resources);
}
