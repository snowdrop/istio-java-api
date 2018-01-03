/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model;

import java.io.Serializable;

import io.fabric8.kubernetes.api.model.HasMetadata;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public interface IstioResource extends HasMetadata, Serializable {
}
