package me.snowdrop.istio.clientv2;

import io.fabric8.kubernetes.client.BaseClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import me.snowdrop.istio.adapter.bypass.Bypass;
import me.snowdrop.istio.adapter.bypass.BypassList;
import me.snowdrop.istio.adapter.bypass.DoneableBypass;
import me.snowdrop.istio.adapter.circonus.Circonus;
import me.snowdrop.istio.adapter.circonus.CirconusList;
import me.snowdrop.istio.adapter.circonus.DoneableCirconus;
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
import me.snowdrop.istio.adapter.memquota.DoneableQuota;
import me.snowdrop.istio.adapter.memquota.Memquota;
import me.snowdrop.istio.adapter.memquota.MemquotaList;
import me.snowdrop.istio.adapter.opa.DoneableOpa;
import me.snowdrop.istio.adapter.opa.Opa;
import me.snowdrop.istio.adapter.opa.OpaList;
import me.snowdrop.istio.adapter.prometheus.DoneablePrometheus;
import me.snowdrop.istio.adapter.prometheus.Prometheus;
import me.snowdrop.istio.adapter.prometheus.PrometheusList;
import me.snowdrop.istio.adapter.servicecontrol.DoneableServicecontrol;
import me.snowdrop.istio.adapter.servicecontrol.Quota;
import me.snowdrop.istio.adapter.servicecontrol.QuotaList;
import me.snowdrop.istio.adapter.servicecontrol.Servicecontrol;
import me.snowdrop.istio.adapter.servicecontrol.ServicecontrolList;
import me.snowdrop.istio.adapter.solarwinds.DoneableSolarwinds;
import me.snowdrop.istio.adapter.solarwinds.Solarwinds;
import me.snowdrop.istio.adapter.solarwinds.SolarwindsList;
import me.snowdrop.istio.adapter.stackdriver.DoneableStackdriver;
import me.snowdrop.istio.adapter.stackdriver.Stackdriver;
import me.snowdrop.istio.adapter.stackdriver.StackdriverList;
import me.snowdrop.istio.adapter.statsd.DoneableStatsd;
import me.snowdrop.istio.adapter.statsd.Statsd;
import me.snowdrop.istio.adapter.statsd.StatsdList;
import me.snowdrop.istio.adapter.stdio.DoneableStdio;
import me.snowdrop.istio.adapter.stdio.Stdio;
import me.snowdrop.istio.adapter.stdio.StdioList;
import me.snowdrop.istio.client.internal.operation.adapter.BypassOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.CirconusOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.DenierOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.FluentdOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.KubernetesenvOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.MemquotaOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.OpaOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.PrometheusOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.QuotaOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.ServiceControlQuotaOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.ServicecontrolOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.SolarwindsOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.StackdriverOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.StatsdOperationImpl;
import me.snowdrop.istio.client.internal.operation.adapter.StdioOperationImpl;
import okhttp3.OkHttpClient;

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
    public MixedOperation<me.snowdrop.istio.adapter.memquota.Quota, me.snowdrop.istio.adapter.memquota.QuotaList, DoneableQuota, Resource<me.snowdrop.istio.adapter.memquota.Quota, DoneableQuota>> quota() {
        return new QuotaOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
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
    public MixedOperation<Quota, QuotaList, me.snowdrop.istio.adapter.servicecontrol.DoneableQuota, Resource<Quota, me.snowdrop.istio.adapter.servicecontrol.DoneableQuota>> serviceControlQuota() {
        return new ServiceControlQuotaOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
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
