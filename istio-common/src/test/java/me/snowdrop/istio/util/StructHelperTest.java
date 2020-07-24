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

import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class StructHelperTest {
	private static Map<String, Object> map;

	@BeforeClass
	public static void loadMap() {
		Yaml parser = new Yaml();
		map = parser.loadAs(Thread.currentThread().getContextClassLoader().getResourceAsStream("struct.yml"), Map.class);
	}

	@Test
	public void emptyPathShouldReturnCurrent() {
		assertEquals(map, StructHelper.get(map, "", Map.class));
		assertEquals("INSERT_BEFORE", StructHelper.get(map, "patch.operation.", String.class));
	}

	@Test
	public void gettingTerminalShouldWork() {
		assertEquals("INSERT_BEFORE", StructHelper.get(map, "patch.operation", String.class));
	}

	@Test
	public void gettingTerminalWithImproperExpectedClassShouldFail() {
		try {
			assertEquals(Collections.emptyMap(), StructHelper.get(map, "patch.operation", Map.class));
			fail("Should have thrown a ClassCastException");
		} catch (ClassCastException e) {
			// expected
		}
	}

	@Test
	public void pathInterruptedByTerminalShouldFail() {
		final String path = "patch.operation.foo.bar";
		try {
			assertEquals(Collections.emptyMap(), StructHelper.get(map, path, Map.class));
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
			final String message = e.getMessage();
			assertEquals("'" + path + "' is interrupted at 'patch.operation' because 'operation' is a terminal element",
					message);
		}
	}

	@Test
	public void gettingNonExistingTerminalShouldFail() {
		final String path = "patch.value.typed_config.value.root_id";
		try {
			assertEquals("stats_outbound", StructHelper.get(map, path, String.class));
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
			final String message = e.getMessage();
			assertEquals("'root_id' is not a child of 'patch.value.typed_config.value'. Known children: " +
					"'config'", message);
		}
	}

	@Test
	public void interruptedPathShouldFail() {
		final String path = "patch.value.value.config";
		try {
			assertEquals(Collections.emptyMap(), StructHelper.get(map, path, Map.class));
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
			final String message = e.getMessage();
			assertEquals("'value' is not a child of 'patch.value.value'. Known children: 'name', " +
					"'typed_config'", message);
		}
	}

	@Test
	public void gettingIntermediateShouldReturnMap() {
		final Map intermediate = StructHelper.get(StructHelperTest.map, "patch.value.typed_config.value.config", Map.class);
		assertEquals("stats_outbound", intermediate.get("root_id"));
	}

	@Test
	public void gettingFromIntermediateShouldBeEquivalentToUsingLongerPath() {
		final Map intermediate = StructHelper.get(StructHelperTest.map, "patch.value.typed_config.value.config",
				Map.class);
		assertEquals(intermediate.get("root_id"),
				StructHelper.get(StructHelperTest.map, "patch.value.typed_config.value.config.root_id", String.class));
	}
}
