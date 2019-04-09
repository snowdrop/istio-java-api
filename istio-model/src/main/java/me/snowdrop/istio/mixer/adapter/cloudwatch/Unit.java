/*
 *
 *  * Copyright (C) 2019 Red Hat, Inc.
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
package me.snowdrop.istio.mixer.adapter.cloudwatch;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum Unit {
    None(0),

    Seconds(1),

    Microseconds(2),

    Milliseconds(3),

    Count(4),

    Bytes(5),

    Kilobytes(6),

    Megabytes(7),

    Gigabytes(8),

    Terabytes(9),

    Bits(10),

    Kilobits(11),

    Megabits(12),

    Gigabits(13),

    Terabits(14),

    Percent(15),

    Bytes_Second(16),

    Kilobytes_Second(17),

    Megabytes_Second(18),

    Gigabytes_Second(19),

    Terabytes_Second(20),

    Bits_Second(21),

    Kilobits_Second(22),

    Megabits_Second(23),

    Gigabits_Second(24),

    Terabits_Second(25),

    Count_Second(26);

    private final int intValue;

    Unit(int intValue) {
        this.intValue = intValue;
    }

    public int value() {
        return intValue;
    }
}
