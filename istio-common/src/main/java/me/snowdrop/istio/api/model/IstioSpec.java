/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.snowdrop.istio.api.internal.IstioKind;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public interface IstioSpec extends Serializable {
    @JsonIgnore
    default String getKind() {
        final IstioKind kind = getClass().getAnnotation(IstioKind.class);
        if (kind != null) {
            return kind.name();
        }
        throw new IllegalStateException(getClass().getName() + " should have been annotated with @IstioKind!");
    }
}
