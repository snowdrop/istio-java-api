package me.snowdrop.istio.applier;

import io.fabric8.kubernetes.api.model.Doneable;
import me.snowdrop.istio.api.model.IstioResource;

public interface Adapter {

    IstioResource createCustomResource(String customResourceDefinition, IstioResource istioResource, Class<? extends Doneable> done);

}
