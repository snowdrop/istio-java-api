package me.snowdrop.istio.applier;

import me.snowdrop.istio.api.model.IstioResource;

public interface Adapter {
    <T extends IstioResource> T createCustomResource(T resource, Applier<T> applier);
}
