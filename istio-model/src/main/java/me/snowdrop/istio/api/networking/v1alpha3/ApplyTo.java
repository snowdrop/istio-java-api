package me.snowdrop.istio.api.networking.v1alpha3;

public enum ApplyTo {

    INVALID(0),

    /**
     * Applies the patch to the listener.
     */
    LISTENER(1),

    /**
     * Applies the patch to the filter chain.
     */
    FILTER_CHAIN(2),

    /**
     * Applies the patch to the network filter chain, to modify an
     * existing filter or add a new filter.
     */
    NETWORK_FILTER(3),

    /**
     * Applies the patch to the HTTP filter chain in the http
     * connection manager, to modify an existing filter or add a new
     * filter.
     */
    HTTP_FILTER(4),

    /**
     * Applies the patch to the Route configuration (rds output)
     * inside a HTTP connection manager. This does not apply to the
     * virtual host. Currently, only MERGE operation is allowed on the
     * route configuration objects.
     */
    ROUTE_CONFIGURATION(5),

    /**
     * Applies the patch to a virtual host inside a route configuration.
     */
    VIRTUAL_HOST(6),

    /**
     * Applies the patch to a route object inside the matched virtual
     * host in a route configuration. Currently, only MERGE operation
     * is allowed on the route objects.
     */
    HTTP_ROUTE(7),

    /**
     * Applies the patch to a cluster in a CDS output. Also used to add new clusters.
     */
    CLUSTER(8);

    private final int intValue;

    ApplyTo(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
