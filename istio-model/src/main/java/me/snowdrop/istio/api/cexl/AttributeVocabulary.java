/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.cexl;

import me.snowdrop.istio.api.mixer.config.descriptor.ValueType;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class AttributeVocabulary {
	private static final Map<String, AttributeInfo> ATTRIBUTE_INFO_MAP = new ConcurrentHashMap<>();

	public static Optional<AttributeInfo> getInfoFor(String attributeName) {
		return Optional.ofNullable(ATTRIBUTE_INFO_MAP.get(attributeName));
	}

	public static class AttributeInfo {
		public final String name;
		public final ValueType type;
		public final String description;
		public final String example;

		private AttributeInfo(String name, String istioType, String description, String example) {
			this.name = name;
			this.type = ValueType.valueOf(istioType.toUpperCase());
			this.description = description;
			this.example = example;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			AttributeInfo info = (AttributeInfo) o;

			if (!name.equals(info.name)) return false;
			return type == info.type;
		}

		@Override
		public int hashCode() {
			int result = name.hashCode();
			result = 31 * result + type.hashCode();
			return result;
		}
	}

	public static Set<String> getKnownAttributes() {
		return Collections.unmodifiableSet(ATTRIBUTE_INFO_MAP.keySet());
	}

	// generated from https://github.com/istio/istio.io/blob/master/content/en/docs/reference/config/policy-and-telemetry/attribute-vocabulary/index.md

	static {
		ATTRIBUTE_INFO_MAP.put("source.uid", new AttributeInfo("source.uid", "string", "Platform-specific unique identifier for the source workload instance.", "`kubernetes://redis-master-2353460263-1ecey.my-namespace` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("source.ip", new AttributeInfo("source.ip", "ip_address", "Source workload instance IP address.", "`10.0.0.117` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("source.labels", new AttributeInfo("source.labels", ValueType.STRING_MAP.name()
				, "A map of key-value pairs attached to the source instance.", "version => v1 "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("source.name", new AttributeInfo("source.name", "string", "Source workload instance name.", "`redis-master-2353460263-1ecey` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("source.namespace", new AttributeInfo("source.namespace", "string", "Source workload instance namespace.", "`my-namespace` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("source.principal", new AttributeInfo("source.principal", "string", "Authority under which the source workload instance is running.", "`service-account-foo` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("source.owner", new AttributeInfo("source.owner", "string", "Reference to the workload controlling the source workload instance.", "`kubernetes://apis/apps/v1/namespaces/istio-system/deployments/istio-policy` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("source.workload.uid", new AttributeInfo("source.workload.uid", "string", "Unique identifier of the source workload.", "`istio://istio-system/workloads/istio-policy` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("source.workload.name", new AttributeInfo("source.workload.name", "string", "Source workload name.", "`istio-policy` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("source.workload.namespace", new AttributeInfo("source.workload.namespace", "string", "Source workload namespace. ", "`istio-system` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.uid", new AttributeInfo("destination.uid", "string", "Platform-specific unique identifier for the server instance.", "`kubernetes://my-svc-234443-5sffe.my-namespace` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.ip", new AttributeInfo("destination.ip", "ip_address", "Server IP address.", "`10.0.0.104` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.port", new AttributeInfo("destination.port", "int64", "The recipient port on the server IP address.", "`8080` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.labels", new AttributeInfo("destination.labels", ValueType.STRING_MAP.name()
				, "A map of key-value pairs attached to the server instance.", "version => v2 "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.name", new AttributeInfo("destination.name", "string", "Destination workload instance name.", "`istio-telemetry-2359333` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.namespace", new AttributeInfo("destination.namespace", "string", "Destination workload instance namespace.", "`istio-system` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.principal", new AttributeInfo("destination.principal", "string", "Authority under which the destination workload instance is running.", "`service-account` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.workload.uid", new AttributeInfo("destination.workload.uid", "string", "Unique identifier of the destination workload.", "`istio://istio-system/workloads/istio-telemetry` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.workload.name", new AttributeInfo("destination.workload.name", "string", "Destination workload name.", "`istio-telemetry` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.workload.namespace", new AttributeInfo("destination.workload.namespace", "string", "Destination workload namespace.", "`istio-system` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.container.name", new AttributeInfo("destination.container.name", "string", "Name of the destination workload instance's container.", "`mixer` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.container.image", new AttributeInfo("destination.container.image", "string", "Image of the destination workload instance's container.", "`gcr.io/istio-testing/mixer:0.8.0` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.service.host", new AttributeInfo("destination.service.host", "string", "Destination host address.", "`istio-telemetry.istio-system.svc.cluster.local` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.service.uid", new AttributeInfo("destination.service.uid", "string", "Unique identifier of the destination service.", "`istio://istio-system/services/istio-telemetry` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.service.name", new AttributeInfo("destination.service.name", "string", "Destination service name.", "`istio-telemetry` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.service.namespace", new AttributeInfo("destination.service.namespace", "string", "Destination service namespace.", "`istio-system` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("origin.ip", new AttributeInfo("origin.ip", "ip_address", "IP address of the proxy client, e.g. origin for the ingress proxies.", "`127.0.0.1` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.headers", new AttributeInfo("request.headers", ValueType.STRING_MAP.name()
				, "HTTP request headers with lowercase keys. For gRPC, its metadata will be here.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.id", new AttributeInfo("request.id", "string", "An ID for the request with statistically low probability of collision.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.path", new AttributeInfo("request.path", "string", "The HTTP URL path including query string", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.url_path", new AttributeInfo("request.url_path", "string", "The path part of HTTP URL, with query string being stripped", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.query_params", new AttributeInfo("request.query_params", ValueType.STRING_MAP.name()
				, "A map of query parameters extracted from the HTTP URL.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.host", new AttributeInfo("request.host", "string", "HTTP/1.x host header or HTTP/2 authority header.", "`redis-master:3337` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.method", new AttributeInfo("request.method", "string", "The HTTP method.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.reason", new AttributeInfo("request.reason", "string", "The request reason used by auditing systems.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.referer", new AttributeInfo("request.referer", "string", "The HTTP referer header.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.scheme", new AttributeInfo("request.scheme", "string", "URI Scheme of the request", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.size", new AttributeInfo("request.size", "int64", "Size of the request in bytes. For HTTP requests this is equivalent to the Content-Length header.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.total_size", new AttributeInfo("request.total_size", "int64", "Total size of HTTP request in bytes, including request headers, body and trailers.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.time", new AttributeInfo("request.time", "timestamp", "The timestamp when the " +
				"destination receives the request. This should be equivalent to Firebase \"now\").", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.useragent", new AttributeInfo("request.useragent", "string", "The HTTP User-Agent header.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("response.headers", new AttributeInfo("response.headers", ValueType.STRING_MAP.name()
				, "HTTP response headers with lowercase keys.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("response.size", new AttributeInfo("response.size", "int64", "Size of the response body in bytes", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("response.total_size", new AttributeInfo("response.total_size", "int64", "Total size of HTTP response in bytes, including response headers and body.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("response.time", new AttributeInfo("response.time", "timestamp", "The timestamp when the destination produced the response.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("response.duration", new AttributeInfo("response.duration", "duration", "The amount of time the response took to generate.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("response.code", new AttributeInfo("response.code", "int64", "The response's HTTP status code.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("response.grpc_status", new AttributeInfo("response.grpc_status", "string", "The response's gRPC status.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("response.grpc_message", new AttributeInfo("response.grpc_message", "string", "The response's gRPC status message.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("connection.id", new AttributeInfo("connection.id", "string", "An ID for a TCP connection with statistically low probability of collision.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("connection.event", new AttributeInfo("connection.event", "string", "Status of a TCP connection, its value is one of \"open\", \"continue\" and \"close\".", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("connection.received.bytes", new AttributeInfo("connection.received.bytes", "int64", "Number of bytes received by a destination service on a connection since the last Report() for a connection.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("connection.received.bytes_total", new AttributeInfo("connection.received.bytes_total", "int64", "Total number of bytes received by a destination service during the lifetime of a connection.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("connection.sent.bytes", new AttributeInfo("connection.sent.bytes", "int64", "Number of bytes sent by a destination service on a connection since the last Report() for a connection.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("connection.sent.bytes_total", new AttributeInfo("connection.sent.bytes_total", "int64", "Total number of bytes sent by a destination service during the lifetime of a connection.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("connection.duration", new AttributeInfo("connection.duration", "duration", "The total amount of time a connection has been open.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("connection.mtls", new AttributeInfo("connection.mtls", "bool", "Indicates whether a " +
				"request is received over a mutual TLS enabled downstream connection.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("connection.requested_server_name", new AttributeInfo("connection.requested_server_name", "string", "The requested server name (SNI) of the connection", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("context.protocol", new AttributeInfo("context.protocol", "string", "Protocol of the request or connection being proxied.", "`tcp` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("context.time", new AttributeInfo("context.time", "timestamp", "The timestamp of Mixer operation.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("context.reporter.kind", new AttributeInfo("context.reporter.kind", "string", "Contextualizes the reported attribute set. Set to `inbound` for the server-side calls from sidecars and `outbound` for the client-side calls from sidecars and gateways", "`inbound` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("context.reporter.uid", new AttributeInfo("context.reporter.uid", "string", "Platform-specific identifier of the attribute reporter.", "`kubernetes://my-svc-234443-5sffe.my-namespace` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("context.proxy_error_code", new AttributeInfo("context.proxy_error_code", "string", "Additional details about the response or connection from proxy. In case of Envoy, see `%RESPONSE_FLAGS%` in [Envoy Access Log](https://www.envoyproxy.io/docs/envoy/latest/configuration/observability/access_log/usage#config-access-log-format-response-flags) for more detail", "`UH` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("api.service", new AttributeInfo("api.service", "string", "The public service name. This is different than the in-mesh service identity and reflects the name of the service exposed to the client.", "`my-svc.com` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("api.version", new AttributeInfo("api.version", "string", "The API version.", "`v1alpha1` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("api.operation", new AttributeInfo("api.operation", "string", "Unique string used to identify the operation. The id is unique among all operations described in a specific &lt;service, version&gt;.", "`getPetsById` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("api.protocol", new AttributeInfo("api.protocol", "string", "The protocol type of the API call. Mainly for monitoring/analytics. Note that this is the frontend protocol exposed to the client, not the protocol implemented by the backend service.", "`http`, `https`, or `grpc` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.auth.principal", new AttributeInfo("request.auth.principal", "string", "The " +
				"authenticated principal of the request. This is a string of the issuer (`iss`) and subject (`sub`) " +
				"claims within a JWT concatenated with \"/\" with a percent - encoded subject value.This attribute may " +
				"come from the peer or the origin in the Istio authentication policy, depending on the binding rule defined in the Istio authentication policy.", "`issuer@foo.com/sub @foo.com`"));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.auth.audiences", new AttributeInfo("request.auth.audiences", "string", "The intended audience(s) for this authentication information. This should reflect the audience (`aud`) claim within a JWT.", "`aud1` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.auth.presenter", new AttributeInfo("request.auth.presenter", "string", "The authorized presenter of the credential. This value should reflect the optional Authorized Presenter (`azp`) claim within a JWT or the OAuth2 client id.", "123456789012.my-svc.com "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.auth.claims", new AttributeInfo("request.auth.claims", ValueType.STRING_MAP.name()
				, "all raw string claims from the `origin` JWT", "`iss`: `issuer@foo.com`, `sub`: `sub@foo.com`, `aud`: `aud1` "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("request.api_key", new AttributeInfo("request.api_key", "string", "The API key used for the request.", "abcde12345 "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("check.error_code", new AttributeInfo("check.error_code", "int64", "The error [code](https://github.com/google/protobuf/blob/master/src/google/protobuf/stubs/status.h) for Mixer Check call.", "5 "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("check.error_message", new AttributeInfo("check.error_message", "string", "The error message for Mixer Check call.", "Could not find the resource "));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("check.cache_hit", new AttributeInfo("check.cache_hit", "bool", "Indicates whether Mixer check call hits local cache.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("quota.cache_hit", new AttributeInfo("quota.cache_hit", "bool", "Indicates whether Mixer" +
				" quota call hits local cache.", ""));
	}

	static {
		ATTRIBUTE_INFO_MAP.put("destination.owner", new AttributeInfo("destination.owner", "string", "Reference to the workload controlling the destination workload instance.", "`kubernetes://apis/apps/v1/namespaces/istio-system/deployments/istio-telemetry`"));
	}

	private AttributeVocabulary() {
	}
}
