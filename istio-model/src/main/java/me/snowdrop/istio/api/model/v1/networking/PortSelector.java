package me.snowdrop.istio.api.model.v1.networking;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.snowdrop.istio.api.internal.PortSelectorDeserializer;

@JsonDeserialize(using = PortSelectorDeserializer.class)
public interface PortSelector extends Serializable {
}
