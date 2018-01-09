/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mixer;

/**
 * How an attribute's value was matched.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum Condition {
    CONDITION_UNSPECIFIED,
    ABSENCE,
    EXACT,
    REGEX
}
