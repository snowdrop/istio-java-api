package me.snowdrop.istio.api.networking.v1alpha3;

public enum Operation {

    INVALID(0),

    /**
     * Merge the provided config with the generated config using
     * json merge semantics.
     */
    MERGE(1),

    /**
     * Add the provided config to an existing list (of listeners,
     * clusters, virtual hosts, network filters, or http
     * filters). This operation will be ignored when applyTo is set
     * to ROUTE_CONFIGURATION, or HTTP_ROUTE.
     */
    ADD(2),

    /**
     * Remove the selected object from the list (of listeners,
     * clusters, virtual hosts, network filters, or http
     * filters). Does not require a value to be specified. This
     * operation will be ignored when applyTo is set to
     * ROUTE_CONFIGURATION, or HTTP_ROUTE.
     */
    REMOVE(3),

    /**
     * Insert operation on an array of named objects. This operation
     * is typically useful only in the context of filters, where the
     * order of filters matter. For clusters and virtual hosts,
     * order of the element in the array does not matter. Insert
     * before the selected filter or sub filter. If no filter is
     * selected, the specified filter will be inserted at the front
     * of the list.
     */
    INSERT_BEFORE(4),

    /**
     * Insert operation on an array of named objects. This operation
     * is typically useful only in the context of filters, where the
     * order of filters matter. For clusters and virtual hosts,
     * order of the element in the array does not matter. Insert
     * after the selected filter or sub filter. If no filter is
     * selected, the specified filter will be inserted at the end
     * of the list.
     */
    INSERT_AFTER(5),

    /**
     * Insert operation on an array of named objects. This operation
     * is typically useful only in the context of filters, where the
     * order of filters matter. For clusters and virtual hosts,
     * order of the element in the array does not matter. Insert
     * first in the list based on the presence of selected filter or not.
     * This is specifically useful when you want your filter first in the
     * list based on a match condition specified in Match clause.
     */
    INSERT_FIRST(6);

    private final int intValue;

    Operation(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
