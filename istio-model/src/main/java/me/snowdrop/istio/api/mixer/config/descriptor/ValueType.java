/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.mixer.config.descriptor;

import java.net.InetAddress;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import me.snowdrop.istio.api.Duration;

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
    VALUE_TYPE_UNSPECIFIED(null),
    /**
     * An undiscriminated variable-length string.
     */
    STRING(String.class),
    /**
     * An undiscriminated 64-bit signed integer.
     */
    INT64(Integer.class),
    /**
     * An undiscriminated 64-bit floating-point value.
     */
    DOUBLE(Double.class),
    /**
     * An undiscriminated boolean value.
     */
    BOOL(Boolean.class),
    /**
     * A point in time.
     */
    TIMESTAMP(Date.class),
    /**
     * An IP address.
     */
    IP_ADDRESS(InetAddress.class),
    /**
     * An email address.
     */
    EMAIL_ADDRESS(String.class), // todo: create Email class
    /**
     * A URI.
     */
    URI(URI.class),
    /**
     * A DNS name.
     */
    DNS_NAME(String.class), // todo: not sure what this represents exactly or how to validate
    /**
     * A span between two points in time.
     */
    DURATION(Duration.class),
    /**
     * A map string - string, typically used by headers.
     */
    STRING_MAP(Map.class);

    ValueType(Class associatedType) {
        this.associatedType = associatedType;
    }

    private final Class associatedType;
}
