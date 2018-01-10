package me.snowdrop.istio.applier;

import java.util.Map;

import io.fabric8.kubernetes.api.model.Doneable;
import me.snowdrop.istio.api.model.IstioResource;

public interface Applier<T extends IstioResource> {
    String getKind();

    String getCustomResourceDefinitionName();

    Class<T> getResourceClass();

    Class<? extends Doneable<T>> getDoneableClass();
}
