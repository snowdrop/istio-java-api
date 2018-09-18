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
package me.snowdrop.istio.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import me.snowdrop.istio.api.IstioResource;
import me.snowdrop.istio.api.IstioSpec;

import static me.snowdrop.istio.api.internal.IstioSpecRegistry.getCRDNameFor;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class YAML {
    private final static Pattern DOCUMENT_DELIMITER = Pattern.compile("---");

    private final static YAMLMapper objectMapper = new YAMLMapper();

    private static final String KIND = "kind";

    public static <T> T loadIstioResource(final String specFileAsString, Class<T> clazz) {
        return loadIstioResources(specFileAsString, clazz).get(0);
    }

    public static <T> List<T> loadIstioResources(final String specFileAsString, Class<T> clazz) {
        List<T> results = new ArrayList<>();
        String[] documents = DOCUMENT_DELIMITER.split(specFileAsString);

        final boolean wantSpec = IstioSpec.class.isAssignableFrom(clazz);
        if (!wantSpec && !IstioResource.class.equals(clazz)) {
            throw new IllegalArgumentException("Can only load either IstioSpec implementations or IstioResources. Asked to " +
                    "load: " + clazz.getName());
        }

        for (String document : documents) {
            try {
                document = document.trim();
                if (!document.isEmpty()) {
                    final Map<String, Object> resourceYaml = objectMapper.readValue(document, Map.class);

                    if (resourceYaml.containsKey(KIND)) {
                        final String kind = (String) resourceYaml.get(KIND);
                        getCRDNameFor(kind).orElseThrow(() -> new IllegalArgumentException(String.format("%s is not a known Istio resource.", kind)));
                        final IstioResource resource = objectMapper.convertValue(resourceYaml, IstioResource.class);
                        if (wantSpec) {
                            results.add(clazz.cast(resource.getSpec()));
                        } else {
                            results.add(clazz.cast(resource));
                        }
                    } else {
                        throw new IllegalArgumentException(String.format("%s is not specified in provided resource.", KIND));
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        return results;
    }

    public static <T> List<T> loadIstioResources(final InputStream resource, Class<T> clazz) {
        return loadIstioResources(Utils.writeStreamToString(resource), clazz);
    }

    public static <T> T loadIstioResource(final InputStream resource, Class<T> clazz) {
        return loadIstioResources(resource, clazz).get(0);
    }

}
