/*
 * *
 *  * Copyright (C) 2018 Red Hat, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */
package me.snowdrop.istio.adapter.stdio;

/**
 * Stream is used to select between different log output sinks.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum Stream {
    /**
     * Output to the Mixer process' standard output stream. This is the default value.
     */
    STDOUT(0),

    /**
     * Output to the Mixer process' standard error stream.
     */
    STDERR(1),

    /**
     * Output to a specific file.
     */
    FILE(2),

    /**
     * Output to a specific rotating file, controlled by the various file rotation options.
     */
    ROTATED_FILE(3);

    private final int intValue;

    Stream(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
