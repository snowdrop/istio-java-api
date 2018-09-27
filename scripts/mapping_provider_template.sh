#!/bin/sh


LINES=""

input=$(cat)

IMPORTS_FILE=$(mktemp /tmp/imports.XXXXXXXXXX)
MAPPING_FILE=$(mktemp /tmp/mappings.XXXXXXXXXX)

for c in $input; do
pkg=`echo $c  | awk -F "#" '{print $1}'`
class=`echo $c | awk -F "#" '{print $2}'`
crd=`cat istio-common/src/main/resources/istio-crd.properties | grep -i "$class="`
prefix=`echo $crd | awk -F "=" '{print $2}' | awk -F "|" '{print $1}' | awk -F "." '{print $2"."$3"."$4}'`
version=`echo $crd | awk -F "|" '{print $3}' | awk -F "=" '{print $2}'`
echo "import $pkg.$class;" >> $IMPORTS_FILE
echo "        mappings.put(\"$prefix/$version#$class\", ${class}.class);" >> $MAPPING_FILE
done

#define the template.
cat  << EOF
package me.snowdrop.istio;

import io.fabric8.kubernetes.api.KubernetesResourceMappingProvider;
import io.fabric8.kubernetes.api.model.KubernetesResource;

import java.util.HashMap;
import java.util.Map;

$(cat $IMPORTS_FILE)

public class IstioResourceMappingProvider implements KubernetesResourceMappingProvider {

    public final Map<String, Class<? extends KubernetesResource>> mappings = new HashMap<>();

    public IstioResourceMappingProvider () {
    }

    public Map<String, Class<? extends KubernetesResource>> getMappings() {
$(cat $MAPPING_FILE)
        return mappings;
    }
}
EOF