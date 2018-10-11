package me.snowdrop.istio.client;

import io.fabric8.kubernetes.client.BaseClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import me.snowdrop.istio.adapter.bypass.Bypass;
import me.snowdrop.istio.adapter.bypass.BypassList;
import me.snowdrop.istio.adapter.bypass.DoneableBypass;
import me.snowdrop.istio.adapter.denier.Denier;
import me.snowdrop.istio.adapter.denier.DenierList;
import me.snowdrop.istio.adapter.denier.DoneableDenier;
import me.snowdrop.istio.adapter.fluentd.DoneableFluentd;
import me.snowdrop.istio.adapter.fluentd.Fluentd;
import me.snowdrop.istio.adapter.fluentd.FluentdList;
import me.snowdrop.istio.adapter.kubernetesenv.DoneableKubernetesenv;
import me.snowdrop.istio.adapter.kubernetesenv.Kubernetesenv;
import me.snowdrop.istio.adapter.kubernetesenv.KubernetesenvList;
import me.snowdrop.istio.adapter.memquota.DoneableMemquota;
import me.snowdrop.istio.adapter.memquota.Memquota;
import me.snowdrop.istio.adapter.memquota.MemquotaList;
import me.snowdrop.istio.adapter.opa.DoneableOpa;
import me.snowdrop.istio.adapter.opa.Opa;
import me.snowdrop.istio.adapter.opa.OpaList;
import me.snowdrop.istio.adapter.prometheus.DoneablePrometheus;
import me.snowdrop.istio.adapter.prometheus.Prometheus;
import me.snowdrop.istio.adapter.prometheus.PrometheusList;
import me.snowdrop.istio.adapter.servicecontrol.DoneableServicecontrol;
import me.snowdrop.istio.adapter.servicecontrol.Servicecontrol;
import me.snowdrop.istio.adapter.servicecontrol.ServicecontrolList;
import me.snowdrop.istio.adapter.solarwinds.DoneableSolarwinds;
import me.snowdrop.istio.adapter.solarwinds.Solarwinds;
import me.snowdrop.istio.adapter.solarwinds.SolarwindsList;
import me.snowdrop.istio.client.internal.operation.BypassOperationImpl;
import me.snowdrop.istio.client.internal.operation.CirconusOperationImpl;
import me.snowdrop.istio.client.internal.operation.DenierOperationImpl;
import me.snowdrop.istio.client.internal.operation.FluentdOperationImpl;
import me.snowdrop.istio.client.internal.operation.KubernetesenvOperationImpl;
import me.snowdrop.istio.client.internal.operation.MemquotaOperationImpl;
import me.snowdrop.istio.client.internal.operation.OpaOperationImpl;
import me.snowdrop.istio.client.internal.operation.PrometheusOperationImpl;
import me.snowdrop.istio.client.internal.operation.ServicecontrolOperationImpl;
import me.snowdrop.istio.client.internal.operation.SolarwindsOperationImpl;
import me.snowdrop.istio.client.internal.operation.StackdriverOperationImpl;
import me.snowdrop.istio.client.internal.operation.StatsdOperationImpl;
import me.snowdrop.istio.client.internal.operation.StdioOperationImpl;
import me.snowdrop.istio.mixer.adapter.circonus.Circonus;
import me.snowdrop.istio.mixer.adapter.circonus.CirconusList;
import me.snowdrop.istio.mixer.adapter.circonus.DoneableCirconus;
import me.snowdrop.istio.mixer.adapter.stackdriver.DoneableStackdriver;
import me.snowdrop.istio.mixer.adapter.stackdriver.Stackdriver;
import me.snowdrop.istio.mixer.adapter.stackdriver.StackdriverList;
import me.snowdrop.istio.mixer.adapter.statsd.DoneableStatsd;
import me.snowdrop.istio.mixer.adapter.statsd.Statsd;
import me.snowdrop.istio.mixer.adapter.statsd.StatsdList;
import me.snowdrop.istio.mixer.adapter.stdio.DoneableStdio;
import me.snowdrop.istio.mixer.adapter.stdio.Stdio;
import me.snowdrop.istio.mixer.adapter.stdio.StdioList;
import okhttp3.OkHttpClient;

@Deprecated
public class AdapterClient extends BaseClient implements AdapterDsl {

    public AdapterClient() throws KubernetesClientException {
    }

    public AdapterClient(OkHttpClient httpClient, Config config) throws KubernetesClientException {
        super(httpClient, config);
    }

    @Override
    public MixedOperation<Bypass, BypassList, DoneableBypass, Resource<Bypass, DoneableBypass>> bypass() {
        return new BypassOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Circonus, CirconusList, DoneableCirconus, Resource<Circonus, DoneableCirconus>> circonus() {
        return new CirconusOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Denier, DenierList, DoneableDenier, Resource<Denier, DoneableDenier>> denier() {
        return new DenierOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Fluentd, FluentdList, DoneableFluentd, Resource<Fluentd, DoneableFluentd>> fluentd() {
        return new FluentdOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Kubernetesenv, KubernetesenvList, DoneableKubernetesenv, Resource<Kubernetesenv, DoneableKubernetesenv>> kubernetesenv() {
        return new KubernetesenvOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Memquota, MemquotaList, DoneableMemquota, Resource<Memquota, DoneableMemquota>> memquota() {
        return new MemquotaOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Opa, OpaList, DoneableOpa, Resource<Opa, DoneableOpa>> opa() {
        return new OpaOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Prometheus, PrometheusList, DoneablePrometheus, Resource<Prometheus, DoneablePrometheus>> prometheus() {
        return new PrometheusOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Servicecontrol, ServicecontrolList, DoneableServicecontrol, Resource<Servicecontrol, DoneableServicecontrol>> servicecontrol() {
        return new ServicecontrolOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Solarwinds, SolarwindsList, DoneableSolarwinds, Resource<Solarwinds, DoneableSolarwinds>> solarwinds() {
        return new SolarwindsOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Stackdriver, StackdriverList, DoneableStackdriver, Resource<Stackdriver, DoneableStackdriver>> stackdriver() {
        return new StackdriverOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Statsd, StatsdList, DoneableStatsd, Resource<Statsd, DoneableStatsd>> statsd() {
        return new StatsdOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Stdio, StdioList, DoneableStdio, Resource<Stdio, DoneableStdio>> stdio() {
        return new StdioOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }
}
