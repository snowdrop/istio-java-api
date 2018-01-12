package me.snowdrop.istio.applier;

import me.snowdrop.istio.api.model.IstioBaseResource;

public interface Adapter {
    IstioBaseResource createCustomResource(String crdName, IstioBaseResource resource);
}
