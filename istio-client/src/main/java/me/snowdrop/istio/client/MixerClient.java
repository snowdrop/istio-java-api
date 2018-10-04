package me.snowdrop.istio.client;

import io.fabric8.kubernetes.client.BaseClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import me.snowdrop.istio.client.internal.operation.ApiKeyOperationImpl;
import me.snowdrop.istio.client.internal.operation.AuthorizationOperationImpl;
import me.snowdrop.istio.client.internal.operation.CheckNothingOperationImpl;
import me.snowdrop.istio.client.internal.operation.EdgeOperationImpl;
import me.snowdrop.istio.client.internal.operation.ListEntryOperationImpl;
import me.snowdrop.istio.client.internal.operation.LogEntryOperationImpl;
import me.snowdrop.istio.client.internal.operation.MetricOperationImpl;
import me.snowdrop.istio.client.internal.operation.QuotaOperationImpl;
import me.snowdrop.istio.client.internal.operation.ReportNothingOperationImpl;
import me.snowdrop.istio.client.internal.operation.TraceSpanOperationImpl;
import me.snowdrop.istio.mixer.template.apikey.ApiKey;
import me.snowdrop.istio.mixer.template.apikey.ApiKeyList;
import me.snowdrop.istio.mixer.template.apikey.DoneableApiKey;
import me.snowdrop.istio.mixer.template.authorization.Authorization;
import me.snowdrop.istio.mixer.template.authorization.AuthorizationList;
import me.snowdrop.istio.mixer.template.authorization.DoneableAuthorization;
import me.snowdrop.istio.mixer.template.checknothing.CheckNothing;
import me.snowdrop.istio.mixer.template.checknothing.CheckNothingList;
import me.snowdrop.istio.mixer.template.checknothing.DoneableCheckNothing;
import me.snowdrop.istio.mixer.template.edge.DoneableEdge;
import me.snowdrop.istio.mixer.template.edge.Edge;
import me.snowdrop.istio.mixer.template.edge.EdgeList;
import me.snowdrop.istio.mixer.template.listentry.DoneableListEntry;
import me.snowdrop.istio.mixer.template.listentry.ListEntry;
import me.snowdrop.istio.mixer.template.listentry.ListEntryList;
import me.snowdrop.istio.mixer.template.logentry.DoneableLogEntry;
import me.snowdrop.istio.mixer.template.logentry.LogEntry;
import me.snowdrop.istio.mixer.template.logentry.LogEntryList;
import me.snowdrop.istio.mixer.template.metric.DoneableMetric;
import me.snowdrop.istio.mixer.template.metric.Metric;
import me.snowdrop.istio.mixer.template.metric.MetricList;
import me.snowdrop.istio.mixer.template.quota.DoneableQuota;
import me.snowdrop.istio.mixer.template.quota.Quota;
import me.snowdrop.istio.mixer.template.quota.QuotaList;
import me.snowdrop.istio.mixer.template.reportnothing.DoneableReportNothing;
import me.snowdrop.istio.mixer.template.reportnothing.ReportNothing;
import me.snowdrop.istio.mixer.template.reportnothing.ReportNothingList;
import me.snowdrop.istio.mixer.template.tracespan.DoneableTraceSpan;
import me.snowdrop.istio.mixer.template.tracespan.TraceSpan;
import me.snowdrop.istio.mixer.template.tracespan.TraceSpanList;
import okhttp3.OkHttpClient;

public class MixerClient extends BaseClient implements MixerDsl {

    public MixerClient() throws KubernetesClientException {
    }

    public MixerClient(OkHttpClient httpClient, Config config) throws KubernetesClientException {
        super(httpClient, config);
    }

    @Override
    public MixedOperation<ApiKey, ApiKeyList, DoneableApiKey, Resource<ApiKey, DoneableApiKey>> apiKey() {
        return new ApiKeyOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Authorization, AuthorizationList, DoneableAuthorization, Resource<Authorization, DoneableAuthorization>> authorization() {
        return new AuthorizationOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<CheckNothing, CheckNothingList, DoneableCheckNothing, Resource<CheckNothing, DoneableCheckNothing>> checkNothing() {
        return new CheckNothingOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Edge, EdgeList, DoneableEdge, Resource<Edge, DoneableEdge>> edge() {
        return new EdgeOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<ListEntry, ListEntryList, DoneableListEntry, Resource<ListEntry, DoneableListEntry>> listEntry() {
        return new ListEntryOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<LogEntry, LogEntryList, DoneableLogEntry, Resource<LogEntry, DoneableLogEntry>> logEntry() {
        return new LogEntryOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Metric, MetricList, DoneableMetric, Resource<Metric, DoneableMetric>> metric() {
        return new MetricOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Quota, QuotaList, DoneableQuota, Resource<Quota, DoneableQuota>> quota() {
        return new QuotaOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<ReportNothing, ReportNothingList, DoneableReportNothing, Resource<ReportNothing, DoneableReportNothing>> reportNothing() {
        return new ReportNothingOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<TraceSpan, TraceSpanList, DoneableTraceSpan, Resource<TraceSpan, DoneableTraceSpan>> traceSpan() {
        return new TraceSpanOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }
}
