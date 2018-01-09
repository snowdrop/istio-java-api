/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mixer.config.descriptor;

/**
 * PayloadFormat details the currently supported logging payload formats.
 * {@link #TEXT} is the default payload format.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum PayloadFormat {
    /**
     * Invalid, default value.
     */
    PAYLOAD_FORMAT_UNSPECIFIED,
    /**
     * Indicates a payload format of raw text.
     */
    TEXT,
    /**
     * Indicates that the payload is a serialized JSON object.
     */
    JSON
}
