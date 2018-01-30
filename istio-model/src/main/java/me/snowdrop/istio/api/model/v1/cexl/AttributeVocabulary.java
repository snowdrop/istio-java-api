/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.cexl;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import me.snowdrop.istio.api.model.v1.mixer.config.descriptor.ValueType;

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


    /**
     * source.ip attribute with expected type: ip_address
     * Client IP address.
     * Example: 10.0.0.117
     */
    public static final String source_ip = "source.ip";

    static {
        ATTRIBUTE_INFO_MAP.put(source_ip, new AttributeInfo(source_ip, "ip_address", "Client IP address.", "10.0.0.117"));
    }

    /**
     * source.service attribute with expected type: string
     * The fully qualified name of the service that the client belongs to.
     * Example: redis-master.my-namespace.svc.cluster.local
     */
    public static final String source_service = "source.service";

    static {
        ATTRIBUTE_INFO_MAP.put(source_service, new AttributeInfo(source_service, "string", "The fully qualified name of the service that the client belongs to.", "redis-master.my-namespace.svc.cluster.local"));
    }

    /**
     * source.name attribute with expected type: string
     * The short name part of the source service.
     * Example: redis-master
     */
    public static final String source_name = "source.name";

    static {
        ATTRIBUTE_INFO_MAP.put(source_name, new AttributeInfo(source_name, "string", "The short name part of the source service.", "redis-master"));
    }

    /**
     * source.namespace attribute with expected type: string
     * The namespace part of the source service.
     * Example: my-namespace
     */
    public static final String source_namespace = "source.namespace";

    static {
        ATTRIBUTE_INFO_MAP.put(source_namespace, new AttributeInfo(source_namespace, "string", "The namespace part of the source service.", "my-namespace"));
    }

    /**
     * source.domain attribute with expected type: string
     * The domain suffix part of the source service, excluding the name and the namespace.
     * Example: svc.cluster.local
     */
    public static final String source_domain = "source.domain";

    static {
        ATTRIBUTE_INFO_MAP.put(source_domain, new AttributeInfo(source_domain, "string", "The domain suffix part of the source service, excluding the name and the namespace.", "svc.cluster.local"));
    }

    /**
     * source.uid attribute with expected type: string
     * Platform-specific unique identifier for the client instance of the source service.
     * Example: kubernetes://redis-master-2353460263-1ecey.my-namespace
     */
    public static final String source_uid = "source.uid";

    static {
        ATTRIBUTE_INFO_MAP.put(source_uid, new AttributeInfo(source_uid, "string", "Platform-specific unique identifier for the client instance of the source service.", "kubernetes://redis-master-2353460263-1ecey.my-namespace"));
    }

    /**
     * source.labels attribute with expected type: map[string, string]
     * A map of key-value pairs attached to the client instance.
     * Example: version => v1
     */
    public static final String source_labels = "source.labels";

    static {
        ATTRIBUTE_INFO_MAP.put(source_labels, new AttributeInfo(source_labels, ValueType.STRING_MAP.name(), "A map of key-value pairs attached to the client instance.", "version => v1"));
    }

    /**
     * source.user attribute with expected type: string
     * The identity of the immediate sender of the request, authenticated by mTLS.
     * Example: service-account-foo
     */
    public static final String source_user = "source.user";

    static {
        ATTRIBUTE_INFO_MAP.put(source_user, new AttributeInfo(source_user, "string", "The identity of the immediate sender of the request, authenticated by mTLS.", "service-account-foo"));
    }

    /**
     * destination.ip attribute with expected type: ip_address
     * Server IP address.
     * Example: 10.0.0.104
     */
    public static final String destination_ip = "destination.ip";

    static {
        ATTRIBUTE_INFO_MAP.put(destination_ip, new AttributeInfo(destination_ip, "ip_address", "Server IP address.", "10.0.0.104"));
    }

    /**
     * destination.port attribute with expected type: int64
     * The recipient port on the server IP address.
     * Example: 8080
     */
    public static final String destination_port = "destination.port";

    static {
        ATTRIBUTE_INFO_MAP.put(destination_port, new AttributeInfo(destination_port, "int64", "The recipient port on the server IP address.", "8080"));
    }

    /**
     * destination.service attribute with expected type: string
     * The fully qualified name of the service that the server belongs to.
     * Example: my-svc.my-namespace.svc.cluster.local
     */
    public static final String destination_service = "destination.service";

    static {
        ATTRIBUTE_INFO_MAP.put(destination_service, new AttributeInfo(destination_service, "string", "The fully qualified name of the service that the server belongs to.", "my-svc.my-namespace.svc.cluster.local"));
    }

    /**
     * destination.name attribute with expected type: string
     * The short name part of the destination service.
     * Example: my-svc
     */
    public static final String destination_name = "destination.name";

    static {
        ATTRIBUTE_INFO_MAP.put(destination_name, new AttributeInfo(destination_name, "string", "The short name part of the destination service.", "my-svc"));
    }

    /**
     * destination.namespace attribute with expected type: string
     * The namespace part of the destination service.
     * Example: my-namespace
     */
    public static final String destination_namespace = "destination.namespace";

    static {
        ATTRIBUTE_INFO_MAP.put(destination_namespace, new AttributeInfo(destination_namespace, "string", "The namespace part of the destination service.", "my-namespace"));
    }

    /**
     * destination.domain attribute with expected type: string
     * The domain suffix part of the destination service, excluding the name and the namespace.
     * Example: svc.cluster.local
     */
    public static final String destination_domain = "destination.domain";

    static {
        ATTRIBUTE_INFO_MAP.put(destination_domain, new AttributeInfo(destination_domain, "string", "The domain suffix part of the destination service, excluding the name and the namespace.", "svc.cluster.local"));
    }

    /**
     * destination.uid attribute with expected type: string
     * Platform-specific unique identifier for the server instance of the destination service.
     * Example: kubernetes://my-svc-234443-5sffe.my-namespace
     */
    public static final String destination_uid = "destination.uid";

    static {
        ATTRIBUTE_INFO_MAP.put(destination_uid, new AttributeInfo(destination_uid, "string", "Platform-specific unique identifier for the server instance of the destination service.", "kubernetes://my-svc-234443-5sffe.my-namespace"));
    }

    /**
     * destination.labels attribute with expected type: map[string, string]
     * A map of key-value pairs attached to the server instance.
     * Example: version => v2
     */
    public static final String destination_labels = "destination.labels";

    static {
        ATTRIBUTE_INFO_MAP.put(destination_labels, new AttributeInfo(destination_labels, ValueType.STRING_MAP.name(), "A map of key-value pairs attached to the server instance.", "version => v2"));
    }

    /**
     * destination.user attribute with expected type: string
     * The user running the destination application.
     * Example: service-account
     */
    public static final String destination_user = "destination.user";

    static {
        ATTRIBUTE_INFO_MAP.put(destination_user, new AttributeInfo(destination_user, "string", "The user running the destination application.", "service-account"));
    }

    /**
     * request.headers attribute with expected type: map[string, string]
     * HTTP request headers. For gRPC, its metadata will be here.
     * Example:
     */
    public static final String request_headers = "request.headers";

    static {
        ATTRIBUTE_INFO_MAP.put(request_headers, new AttributeInfo(request_headers, ValueType.STRING_MAP.name(), "HTTP request headers. For gRPC, its metadata will be here.", ""));
    }

    /**
     * request.id attribute with expected type: string
     * An ID for the request with statistically low probability of collision.
     * Example:
     */
    public static final String request_id = "request.id";

    static {
        ATTRIBUTE_INFO_MAP.put(request_id, new AttributeInfo(request_id, "string", "An ID for the request with statistically low probability of collision.", ""));
    }

    /**
     * request.path attribute with expected type: string
     * The HTTP URL path including query string
     * Example:
     */
    public static final String request_path = "request.path";

    static {
        ATTRIBUTE_INFO_MAP.put(request_path, new AttributeInfo(request_path, "string", "The HTTP URL path including query string", ""));
    }

    /**
     * request.host attribute with expected type: string
     * HTTP/1.x host header or HTTP/2 authority header.
     * Example: redis-master:3337
     */
    public static final String request_host = "request.host";

    static {
        ATTRIBUTE_INFO_MAP.put(request_host, new AttributeInfo(request_host, "string", "HTTP/1.x host header or HTTP/2 authority header.", "redis-master:3337"));
    }

    /**
     * request.method attribute with expected type: string
     * The HTTP method.
     * Example:
     */
    public static final String request_method = "request.method";

    static {
        ATTRIBUTE_INFO_MAP.put(request_method, new AttributeInfo(request_method, "string", "The HTTP method.", ""));
    }

    /**
     * request.reason attribute with expected type: string
     * The request reason used by auditing systems.
     * Example:
     */
    public static final String request_reason = "request.reason";

    static {
        ATTRIBUTE_INFO_MAP.put(request_reason, new AttributeInfo(request_reason, "string", "The request reason used by auditing systems.", ""));
    }

    /**
     * request.referer attribute with expected type: string
     * The HTTP referer header.
     * Example:
     */
    public static final String request_referer = "request.referer";

    static {
        ATTRIBUTE_INFO_MAP.put(request_referer, new AttributeInfo(request_referer, "string", "The HTTP referer header.", ""));
    }

    /**
     * request.scheme attribute with expected type: string
     * URI Scheme of the request
     * Example:
     */
    public static final String request_scheme = "request.scheme";

    static {
        ATTRIBUTE_INFO_MAP.put(request_scheme, new AttributeInfo(request_scheme, "string", "URI Scheme of the request", ""));
    }

    /**
     * request.size attribute with expected type: int64
     * Size of the request in bytes. For HTTP requests this is equivalent to the Content-Length header.
     * Example:
     */
    public static final String request_size = "request.size";

    static {
        ATTRIBUTE_INFO_MAP.put(request_size, new AttributeInfo(request_size, "int64", "Size of the request in bytes. For HTTP requests this is equivalent to the Content-Length header.", ""));
    }

    /**
     * request.time attribute with expected type: timestamp
     * The timestamp when the destination receives the request. This should be equivalent to Firebase "now".
     * Example:
     */
    public static final String request_time = "request.time";

    static {
        ATTRIBUTE_INFO_MAP.put(request_time, new AttributeInfo(request_time, "timestamp", "The timestamp when the destination receives the request. This should be equivalent to Firebase \"now\".", ""));
    }

    /**
     * request.useragent attribute with expected type: string
     * The HTTP User-Agent header.
     * Example:
     */
    public static final String request_useragent = "request.useragent";

    static {
        ATTRIBUTE_INFO_MAP.put(request_useragent, new AttributeInfo(request_useragent, "string", "The HTTP User-Agent header.", ""));
    }

    /**
     * response.headers attribute with expected type: map[string, string]
     * HTTP response headers.
     * Example:
     */
    public static final String response_headers = "response.headers";

    static {
        ATTRIBUTE_INFO_MAP.put(response_headers, new AttributeInfo(response_headers, ValueType.STRING_MAP.name(), "HTTP response headers.", ""));
    }

    /**
     * response.size attribute with expected type: int64
     * Size of the response body in bytes
     * Example:
     */
    public static final String response_size = "response.size";

    static {
        ATTRIBUTE_INFO_MAP.put(response_size, new AttributeInfo(response_size, "int64", "Size of the response body in bytes", ""));
    }

    /**
     * response.time attribute with expected type: timestamp
     * The timestamp when the destination produced the response.
     * Example:
     */
    public static final String response_time = "response.time";

    static {
        ATTRIBUTE_INFO_MAP.put(response_time, new AttributeInfo(response_time, "timestamp", "The timestamp when the destination produced the response.", ""));
    }

    /**
     * response.duration attribute with expected type: duration
     * The amount of time the response took to generate.
     * Example:
     */
    public static final String response_duration = "response.duration";

    static {
        ATTRIBUTE_INFO_MAP.put(response_duration, new AttributeInfo(response_duration, "duration", "The amount of time the response took to generate.", ""));
    }

    /**
     * response.code attribute with expected type: int64
     * The response's HTTP status code.
     * Example:
     */
    public static final String response_code = "response.code";

    static {
        ATTRIBUTE_INFO_MAP.put(response_code, new AttributeInfo(response_code, "int64", "The response's HTTP status code.", ""));
    }

    /**
     * connection.id attribute with expected type: string
     * An ID for a TCP connection with statistically low probability of collision.
     * Example:
     */
    public static final String connection_id = "connection.id";

    static {
        ATTRIBUTE_INFO_MAP.put(connection_id, new AttributeInfo(connection_id, "string", "An ID for a TCP connection with statistically low probability of collision.", ""));
    }

    /**
     * connection.received.bytes attribute with expected type: int64
     * Number of bytes received by a destination service on a connection since the last Report() for a connection.
     * Example:
     */
    public static final String connection_received_bytes = "connection.received.bytes";

    static {
        ATTRIBUTE_INFO_MAP.put(connection_received_bytes, new AttributeInfo(connection_received_bytes, "int64", "Number of bytes received by a destination service on a connection since the last Report() for a connection.", ""));
    }

    /**
     * connection.received.bytes_total attribute with expected type: int64
     * Total number of bytes received by a destination service during the lifetime of a connection.
     * Example:
     */
    public static final String connection_received_bytes_total = "connection.received.bytes_total";

    static {
        ATTRIBUTE_INFO_MAP.put(connection_received_bytes_total, new AttributeInfo(connection_received_bytes_total, "int64", "Total number of bytes received by a destination service during the lifetime of a connection.", ""));
    }

    /**
     * connection.sent.bytes attribute with expected type: int64
     * Number of bytes sent by a destination service on a connection since the last Report() for a connection.
     * Example:
     */
    public static final String connection_sent_bytes = "connection.sent.bytes";

    static {
        ATTRIBUTE_INFO_MAP.put(connection_sent_bytes, new AttributeInfo(connection_sent_bytes, "int64", "Number of bytes sent by a destination service on a connection since the last Report() for a connection.", ""));
    }

    /**
     * connection.sent.bytes_total attribute with expected type: int64
     * Total number of bytes sent by a destination service during the lifetime of a connection.
     * Example:
     */
    public static final String connection_sent_bytes_total = "connection.sent.bytes_total";

    static {
        ATTRIBUTE_INFO_MAP.put(connection_sent_bytes_total, new AttributeInfo(connection_sent_bytes_total, "int64", "Total number of bytes sent by a destination service during the lifetime of a connection.", ""));
    }

    /**
     * connection.duration attribute with expected type: duration
     * The total amount of time a connection has been open.
     * Example:
     */
    public static final String connection_duration = "connection.duration";

    static {
        ATTRIBUTE_INFO_MAP.put(connection_duration, new AttributeInfo(connection_duration, "duration", "The total amount of time a connection has been open.", ""));
    }

    /**
     * context.protocol attribute with expected type: string
     * Protocol of the request or connection being proxied.
     * Example: tcp
     */
    public static final String context_protocol = "context.protocol";

    static {
        ATTRIBUTE_INFO_MAP.put(context_protocol, new AttributeInfo(context_protocol, "string", "Protocol of the request or connection being proxied.", "tcp"));
    }

    /**
     * context.time attribute with expected type: timestamp
     * The timestamp of Mixer operation.
     * Example:
     */
    public static final String context_time = "context.time";

    static {
        ATTRIBUTE_INFO_MAP.put(context_time, new AttributeInfo(context_time, "timestamp", "The timestamp of Mixer operation.", ""));
    }

    /**
     * api.service attribute with expected type: string
     * The public service name. This is different than the in-mesh service identity and reflects the name of the service exposed to the client.
     * Example: my-svc.com
     */
    public static final String api_service = "api.service";

    static {
        ATTRIBUTE_INFO_MAP.put(api_service, new AttributeInfo(api_service, "string", "The public service name. This is different than the in-mesh service identity and reflects the name of the service exposed to the client.", "my-svc.com"));
    }

    /**
     * api.version attribute with expected type: string
     * The API version.
     * Example: v1alpha1
     */
    public static final String api_version = "api.version";

    static {
        ATTRIBUTE_INFO_MAP.put(api_version, new AttributeInfo(api_version, "string", "The API version.", "v1alpha1"));
    }

    /**
     * api.operation attribute with expected type: string
     * Unique string used to identify the operation. The id is unique among all operations described in a specific <service, version>.
     * Example: getPetsById
     */
    public static final String api_operation = "api.operation";

    static {
        ATTRIBUTE_INFO_MAP.put(api_operation, new AttributeInfo(api_operation, "string", "Unique string used to identify the operation. The id is unique among all operations described in a specific <service, version>.", "getPetsById"));
    }

    /**
     * api.protocol attribute with expected type: string
     * The protocol type of the API call. Mainly for monitoring/analytics. Note that this is the frontend protocol exposed to the client, not the protocol implemented by the backend service.
     * Example: "http", “https”, or "grpc"
     */
    public static final String api_protocol = "api.protocol";

    static {
        ATTRIBUTE_INFO_MAP.put(api_protocol, new AttributeInfo(api_protocol, "string", "The protocol type of the API call. Mainly for monitoring/analytics. Note that this is the frontend protocol exposed to the client, not the protocol implemented by the backend service.", "\"http\", \"https\", or \"grpc\""));
    }

    /**
     * request.auth.principal attribute with expected type: string
     * The authenticated principal of the request. This is a string of the issuer (`iss`) and subject (`sub`) claims within a JWT concatenated with “/” with a percent-encoded subject value.
     * Example: accounts.my-svc.com/104958560606
     */
    public static final String request_auth_principal = "request.auth.principal";

    static {
        ATTRIBUTE_INFO_MAP.put(request_auth_principal, new AttributeInfo(request_auth_principal, "string", "The authenticated principal of the request. This is a string of the issuer (`iss`) and subject (`sub`) claims within a JWT concatenated with “/” with a percent-encoded subject value.", "accounts.my-svc.com/104958560606"));
    }

    /**
     * request.auth.audiences attribute with expected type: string
     * The intended audience(s) for this authentication information. This should reflect the audience (`aud`) claim within a JWT.
     * Example: ['my-svc.com', 'scopes/read']
     */
    public static final String request_auth_audiences = "request.auth.audiences";

    static {
        ATTRIBUTE_INFO_MAP.put(request_auth_audiences, new AttributeInfo(request_auth_audiences, "string", "The intended audience(s) for this authentication information. This should reflect the audience (`aud`) claim within a JWT.", "['my-svc.com', 'scopes/read']"));
    }

    /**
     * request.auth.presenter attribute with expected type: string
     * The authorized presenter of the credential. This value should reflect the optional Authorized Presenter (`azp`) claim within a JWT or the OAuth2 client id.
     * Example: 123456789012.my-svc.com
     */
    public static final String request_auth_presenter = "request.auth.presenter";

    static {
        ATTRIBUTE_INFO_MAP.put(request_auth_presenter, new AttributeInfo(request_auth_presenter, "string", "The authorized presenter of the credential. This value should reflect the optional Authorized Presenter (`azp`) claim within a JWT or the OAuth2 client id.", "123456789012.my-svc.com"));
    }

    /**
     * request.api_key attribute with expected type: string
     * The API key used for the request.
     * Example: abcde12345
     */
    public static final String request_api_key = "request.api_key";

    static {
        ATTRIBUTE_INFO_MAP.put(request_api_key, new AttributeInfo(request_api_key, "string", "The API key used for the request.", "abcde12345"));
    }

    /**
     * check.error_code attribute with expected type: int64
     * The error [code](https://github.com/google/protobuf/blob/master/src/google/protobuf/stubs/status.h#L44) for Mixer Check call.
     * Example: 5
     */
    public static final String check_error_code = "check.error_code";

    static {
        ATTRIBUTE_INFO_MAP.put(check_error_code, new AttributeInfo(check_error_code, "int64", "The error [code](https://github.com/google/protobuf/blob/master/src/google/protobuf/stubs/status.h#L44) for Mixer Check call.", "5"));
    }

    /**
     * check.error_message attribute with expected type: string
     * The error message for Mixer Check call.
     * Example: Could not find the resource
     */
    public static final String check_error_message = "check.error_message";

    static {
        ATTRIBUTE_INFO_MAP.put(check_error_message, new AttributeInfo(check_error_message, "string", "The error message for Mixer Check call.", "Could not find the resource"));
    }


    private AttributeVocabulary() {
    }
}
