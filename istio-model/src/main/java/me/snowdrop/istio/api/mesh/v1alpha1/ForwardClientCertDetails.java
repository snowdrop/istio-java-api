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
package me.snowdrop.istio.api.mesh.v1alpha1;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum ForwardClientCertDetails {
	/**
	 * Field is not set
	 */
	UNDEFINED(0),
	/**
	 * Do not send the XFCC header to the next hop. This is the default value.
	 */
	SANITIZE(1),
	/**
	 * When the client connection is mTLS (Mutual TLS), forward the XFCC header in the request.
	 */
	FORWARD_ONLY(2),
	/**
	 * When the client connection is mTLS, append the client certificate information to the request’s XFCC header and
	 * forward it.
	 */
	APPEND_FORWARD(3),
	/**
	 * When the client connection is mTLS, reset the XFCC header with the client certificate information and send it to
	 * the next hop.
	 */
	SANITIZE_SET(4),
	/**
	 * Always forward the XFCC header in the request, regardless of whether the client connection is mTLS.
	 */
	ALWAYS_FORWARD_ONLY(5);

	private final int intValue;

	ForwardClientCertDetails(int intValue) {
		this.intValue = intValue;
	}

	public int value() {
		return intValue;
	}
}
