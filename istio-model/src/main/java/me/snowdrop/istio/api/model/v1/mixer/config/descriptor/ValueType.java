/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mixer.config.descriptor;

/**
 * ValueType describes the types that values in the Istio system can take. These
 * are used to describe the type of Attributes at run time, describe the type of
 * the result of evaluating an expression, and to describe the runtime type of
 * fields of other descriptors.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum ValueType {

    /**
     * Invalid, default value.
     */
    VALUE_TYPE_UNSPECIFIED,
    /**
     * An undiscriminated variable-length string.
     */
    STRING,
    /**
     * An undiscriminated 64-bit signed integer.
     */
    INT64,
    /**
     * An undiscriminated 64-bit floating-point value.
     */
    DOUBLE,
    /**
     * An undiscriminated boolean value.
     */
    BOOL,
    /**
     * A point in time.
     */
    TIMESTAMP,
    /**
     * An IP address.
     */
    IP_ADDRESS,
    /**
     * An email address.
     */
    EMAIL_ADDRESS,
    /**
     * A URI.
     */
    URI,
    /**
     * A DNS name.
     */
    DNS_NAME,
    /**
     * A span between two points in time.
     */
    DURATION,
    /**
     * A map string - string, typically used by headers.
     */
    STRING_MAP
}
