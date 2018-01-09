/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mixer.config.client;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum NetworkFailPolicy {
    /**
     * If network fails, request is passed to the backend.
     */
    FAIL_OPEN,
    /**
     * If network fails, request is rejected.
     */
    FAIL_CLOSE
}
