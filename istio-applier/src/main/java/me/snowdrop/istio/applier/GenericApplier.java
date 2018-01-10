/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.applier;

import io.fabric8.kubernetes.api.model.Doneable;
import me.snowdrop.istio.api.model.IstioResource;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class GenericApplier<T extends IstioResource> implements Applier<T> {
    private final String kind;
    private final Class<T> resourceClass;
    private final Class<? extends Doneable<T>> doneableClass;
    private final String crdName;

    public GenericApplier(String kind, String crdName, Class<T> resourceClass, Class<? extends Doneable<T>> doneableClass) {
        this.kind = kind;
        this.crdName = crdName;
        this.resourceClass = resourceClass;
        this.doneableClass = doneableClass;
    }

    @Override
    public String getCustomResourceDefinitionName() {
        return crdName;
    }

    @Override
    public String getKind() {
        return kind;
    }

    @Override
    public Class<T> getResourceClass() {
        return resourceClass;
    }

    @Override
    public Class<? extends Doneable<T>> getDoneableClass() {
        return doneableClass;
    }
}
