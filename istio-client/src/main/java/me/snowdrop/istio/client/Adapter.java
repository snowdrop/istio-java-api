package me.snowdrop.istio.client;

import me.snowdrop.istio.api.model.IstioResource;

public interface Adapter {
    IstioResource createCustomResource(String crdName, IstioResource resource);
}
