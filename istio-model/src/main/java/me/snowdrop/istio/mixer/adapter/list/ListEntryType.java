/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.mixer.adapter.list;

/**
 * Determines the type of list that the adapter is consulting.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum ListEntryType {
    /**
     * List entries are treated as plain strings.
     */
    STRINGS(0),
    /**
     * List entries are treated as case-insensitive strings.
     */
    CASE_INSENSITIVE_STRINGS(1),
    /**
     * List entries are treated as IP addresses and ranges.
     */
    IP_ADDRESSES(2),
    /**
     * List entries are treated as re2 regexp. See [here](https://github.com/google/re2/wiki/Syntax) for the supported syntax.
     */
    REGEX(3);

    private final int intValue;

    ListEntryType(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
