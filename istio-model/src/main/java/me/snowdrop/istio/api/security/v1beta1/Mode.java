/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package me.snowdrop.istio.api.security.v1beta1;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum Mode {
	/**
	 * Inherit from parent, if has one. Otherwise treated as PERMISSIVE.
	 */
	UNSET(0),
	/**
	 * Connection is not tunneled.
	 */
	DISABLE(1),
	/**
	 * Connection can be either plaintext or mTLS tunnel.
	 */
	PERMISSIVE(2),
	/**
	 * Connection is an mTLS tunnel (TLS with client cert must be presented).
	 */
	STRICT(3);

	private final int intValue;

	Mode(int intValue) {
		this.intValue = intValue;
	}

	public int value() {
		return intValue;
	}
}
