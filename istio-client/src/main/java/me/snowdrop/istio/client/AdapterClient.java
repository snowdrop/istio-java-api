package me.snowdrop.istio.client;

import io.fabric8.kubernetes.client.BaseClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import me.snowdrop.istio.client.internal.operation.BypassOperationImpl;
import me.snowdrop.istio.client.internal.operation.CirconusOperationImpl;
import me.snowdrop.istio.client.internal.operation.DenierOperationImpl;
import me.snowdrop.istio.client.internal.operation.FluentdOperationImpl;
import me.snowdrop.istio.client.internal.operation.KubernetesenvOperationImpl;
import me.snowdrop.istio.client.internal.operation.MemquotaOperationImpl;
import me.snowdrop.istio.client.internal.operation.OpaOperationImpl;
import me.snowdrop.istio.client.internal.operation.PrometheusOperationImpl;
import me.snowdrop.istio.client.internal.operation.RedisquotaOperationImpl;
import me.snowdrop.istio.client.internal.operation.SolarwindsOperationImpl;
import me.snowdrop.istio.client.internal.operation.StackdriverOperationImpl;
import me.snowdrop.istio.client.internal.operation.StatsdOperationImpl;
import me.snowdrop.istio.client.internal.operation.StdioOperationImpl;
import me.snowdrop.istio.mixer.adapter.bypass.Bypass;
import me.snowdrop.istio.mixer.adapter.bypass.BypassList;
import me.snowdrop.istio.mixer.adapter.bypass.DoneableBypass;
import me.snowdrop.istio.mixer.adapter.circonus.Circonus;
import me.snowdrop.istio.mixer.adapter.circonus.CirconusList;
import me.snowdrop.istio.mixer.adapter.circonus.DoneableCirconus;
import me.snowdrop.istio.mixer.adapter.denier.Denier;
import me.snowdrop.istio.mixer.adapter.denier.DenierList;
import me.snowdrop.istio.mixer.adapter.denier.DoneableDenier;
import me.snowdrop.istio.mixer.adapter.fluentd.DoneableFluentd;
import me.snowdrop.istio.mixer.adapter.fluentd.Fluentd;
import me.snowdrop.istio.mixer.adapter.fluentd.FluentdList;
import me.snowdrop.istio.mixer.adapter.kubernetesenv.DoneableKubernetesenv;
import me.snowdrop.istio.mixer.adapter.kubernetesenv.Kubernetesenv;
import me.snowdrop.istio.mixer.adapter.kubernetesenv.KubernetesenvList;
import me.snowdrop.istio.mixer.adapter.memquota.DoneableMemquota;
import me.snowdrop.istio.mixer.adapter.memquota.Memquota;
import me.snowdrop.istio.mixer.adapter.memquota.MemquotaList;
import me.snowdrop.istio.mixer.adapter.opa.DoneableOpa;
import me.snowdrop.istio.mixer.adapter.opa.Opa;
import me.snowdrop.istio.mixer.adapter.opa.OpaList;
import me.snowdrop.istio.mixer.adapter.prometheus.DoneablePrometheus;
import me.snowdrop.istio.mixer.adapter.prometheus.Prometheus;
import me.snowdrop.istio.mixer.adapter.prometheus.PrometheusList;
import me.snowdrop.istio.mixer.adapter.redisquota.DoneableRedisquota;
import me.snowdrop.istio.mixer.adapter.redisquota.Redisquota;
import me.snowdrop.istio.mixer.adapter.redisquota.RedisquotaList;
import me.snowdrop.istio.mixer.adapter.solarwinds.DoneableSolarwinds;
import me.snowdrop.istio.mixer.adapter.solarwinds.Solarwinds;
import me.snowdrop.istio.mixer.adapter.solarwinds.SolarwindsList;
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
    public MixedOperation<Redisquota, RedisquotaList, DoneableRedisquota, Resource<Redisquota, DoneableRedisquota>> redisquota() {
        return new RedisquotaOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
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
