package me.snowdrop.istio.clientv2;

import io.fabric8.kubernetes.client.Client;

public interface IstioClient extends Client, IstioDsl {

    AdapterDsl adapter();
    MixerDsl mixer();
}
