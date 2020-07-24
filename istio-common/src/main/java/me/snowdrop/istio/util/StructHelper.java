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
package me.snowdrop.istio.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class StructHelper {

	public static <T> T get(Map<String, Object> struct, String dotSeparatedPath, Class<T> expectedType) {
		final String[] parts = dotSeparatedPath.split("\\.");
		return get(struct, parts, expectedType, new LinkedList<>(), dotSeparatedPath);
	}

	private static <T> T get(Map struct, String[] parts, Class<T> expectedType, List<String> processed,
									 String dotSeparatedPath) {
		switch (parts.length) {
			case 0:
				return null;
			case 1:
				return expectedType.cast(handleNullOrEmptyPath(struct, parts, processed));
			default:
				final Object o = handleNullOrEmptyPath(struct, parts, processed);
				if (o instanceof Map) {
					Map map = (Map) o;
					final String[] newParts = new String[parts.length - 1];
					System.arraycopy(parts, 1, newParts, 0, parts.length - 1);
					return get(map, newParts, expectedType, processed, dotSeparatedPath);
				} else {
					throw new IllegalArgumentException("'" + dotSeparatedPath + "' is interrupted at '"
							+ String.join(".", processed) + "' because '" + parts[0] + "' is a terminal element");
				}
		}
	}

	private static Object handleNullOrEmptyPath(Map struct, String[] parts, List<String> processed) {
		final String part = parts[0];
		if (part.isBlank()) {
			return struct;
		}
		// only add part to processed if we're not looking for the last element of our path
		if (parts.length > 1) {
			processed.add(part);
		}
		final Object o = struct.get(part);
		if (o == null) {
			throw new IllegalArgumentException("'" + part + "' is not a child of '"
					+ String.join(".", processed)
					+ "'. Known children: "
					+ struct.keySet().stream()
					.map(k -> "'" + k + "'")
					.collect(Collectors.joining(", ")));
		}
		return o;
	}

}
