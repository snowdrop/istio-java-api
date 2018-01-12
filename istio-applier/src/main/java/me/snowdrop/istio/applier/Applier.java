package me.snowdrop.istio.applier;

import java.util.Map;
import me.snowdrop.istio.api.model.IstioResource;

public interface Applier {

    boolean canApply(String kind);
    IstioResource apply(Map<String, Object> resource);

}
