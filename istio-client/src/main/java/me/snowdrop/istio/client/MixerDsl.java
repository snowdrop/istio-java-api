package me.snowdrop.istio.client;

import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import me.snowdrop.istio.mixer.template.apikey.ApiKey;
import me.snowdrop.istio.mixer.template.apikey.ApiKeyList;
import me.snowdrop.istio.mixer.template.apikey.DoneableApiKey;
import me.snowdrop.istio.mixer.template.authorization.Authorization;
import me.snowdrop.istio.mixer.template.authorization.AuthorizationList;
import me.snowdrop.istio.mixer.template.authorization.DoneableAuthorization;
import me.snowdrop.istio.mixer.template.checknothing.CheckNothing;
import me.snowdrop.istio.mixer.template.checknothing.CheckNothingList;
import me.snowdrop.istio.mixer.template.checknothing.DoneableCheckNothing;
import me.snowdrop.istio.mixer.template.edge.Edge;
import me.snowdrop.istio.mixer.template.edge.EdgeList;
import me.snowdrop.istio.mixer.template.edge.DoneableEdge;
import me.snowdrop.istio.mixer.template.listentry.ListEntry;
import me.snowdrop.istio.mixer.template.listentry.ListEntryList;
import me.snowdrop.istio.mixer.template.listentry.DoneableListEntry;
import me.snowdrop.istio.mixer.template.logentry.LogEntry;
import me.snowdrop.istio.mixer.template.logentry.LogEntryList;
import me.snowdrop.istio.mixer.template.logentry.DoneableLogEntry;
import me.snowdrop.istio.mixer.template.metric.Metric;
import me.snowdrop.istio.mixer.template.metric.MetricList;
import me.snowdrop.istio.mixer.template.metric.DoneableMetric;
import me.snowdrop.istio.mixer.template.quota.Quota;
import me.snowdrop.istio.mixer.template.quota.QuotaList;
import me.snowdrop.istio.mixer.template.quota.DoneableQuota;
import me.snowdrop.istio.mixer.template.reportnothing.ReportNothing;
import me.snowdrop.istio.mixer.template.reportnothing.ReportNothingList;
import me.snowdrop.istio.mixer.template.reportnothing.DoneableReportNothing;
import me.snowdrop.istio.mixer.template.tracespan.TraceSpan;
import me.snowdrop.istio.mixer.template.tracespan.TraceSpanList;
import me.snowdrop.istio.mixer.template.tracespan.DoneableTraceSpan;

public interface MixerDsl {
  MixedOperation<ApiKey,ApiKeyList, DoneableApiKey,Resource<ApiKey,DoneableApiKey>> apiKey();
  MixedOperation<Authorization,AuthorizationList, DoneableAuthorization,Resource<Authorization,DoneableAuthorization>> authorization();
  MixedOperation<CheckNothing,CheckNothingList, DoneableCheckNothing,Resource<CheckNothing,DoneableCheckNothing>> checkNothing();
  MixedOperation<Edge,EdgeList, DoneableEdge,Resource<Edge,DoneableEdge>> edge();
  MixedOperation<ListEntry,ListEntryList, DoneableListEntry,Resource<ListEntry,DoneableListEntry>> listEntry();
  MixedOperation<LogEntry,LogEntryList, DoneableLogEntry,Resource<LogEntry,DoneableLogEntry>> logEntry();
  MixedOperation<Metric,MetricList, DoneableMetric,Resource<Metric,DoneableMetric>> metric();
  MixedOperation<Quota,QuotaList, DoneableQuota,Resource<Quota,DoneableQuota>> quota();
  MixedOperation<ReportNothing,ReportNothingList, DoneableReportNothing,Resource<ReportNothing,DoneableReportNothing>> reportNothing();
  MixedOperation<TraceSpan,TraceSpanList, DoneableTraceSpan,Resource<TraceSpan,DoneableTraceSpan>> traceSpan();
}
