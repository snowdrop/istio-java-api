#!/bin/bash

MODEL_SRC="istio-model/src/main/java/me/snowdrop/istio"
CLIENT_SRC="istio-client/src/main/gen/me/snowdrop/istio/clientv2"
CLIENT_RES="istio-client/src/main/resources"

mkdir -p $CLIENT_SRC

function generate_dsl() {
class_name=$1
filter=$2
dir=$3
out=${dir}/${class_name}.java

echo "package me.snowdrop.istio.clientv2;" > $out
echo "import io.fabric8.kubernetes.client.dsl.MixedOperation;" >> $out
echo "import io.fabric8.kubernetes.client.dsl.Resource;" >> $out

cat istio-model/src/main/resources/schema/istio-schema.json | grep Spec | grep $filter | awk -F "\"" '{print $4}' | sort | uniq | while read s
 do
  r=`echo ${s:0:-4}`
  l=`echo ${r}List`
  c=`echo $r | awk -F "." '{print $NF}'`
  p=`echo $r | awk -F ".$c" '{print $1}'`
  d=`echo Doneable${c}`

  if [ -n "$c" ] && [ ${#c} -gt 2 ];  then
    echo "import $r;" >> $out
    echo "import $l;" >> $out
    echo "import ${p}.${d};" >> $out
  fi
done

echo "public interface $class_name {" >> $out

cat istio-model/src/main/resources/schema/istio-schema.json | grep Spec | grep $filter | awk -F "\"" '{print $4}'  | sort | uniq | while read s
 do
  r=`echo ${s:0:-4}`
  c=`echo $r | awk -F "." '{print $NF}'`
  m=`echo $c | sed 's/.*/\l&/'`
  if [ -n "$c" ] && [ ${#c} -gt 2 ];  then
    echo "  MixedOperation<$c,${c}List, Doneable$c,Resource<$c,Doneable$c>> $m();" >> $out
  fi
done

echo "}" >> $out
}

function generate_handlers() {
filter=$1
dir=$2
res=$3
mkdir -p $dir
cat istio-model/src/main/resources/schema/istio-schema.json | grep Spec | grep $filter | awk -F "\"" '{print $4}' | sort | uniq | while read s
 do
  r=`echo ${s:0:-4}`
  c=`echo $r | awk -F "." '{print $NF}'`
  p=`echo $r | awk -F ".$c" '{print $1}'`

  if [ -n "$c" ] && [ ${#c} -gt 2 ];  then
    out=${dir}/${c}Handler.java
    ./scripts/handler_template.sh $p $filter $c > $out
    echo "me.snowdrop.istio.client.internal.handler.${filter}.$c" >> $res
  fi
done
}

function generate_operations() {
filter=$1
dir=$2
mkdir -p $dir
cat istio-model/src/main/resources/schema/istio-schema.json | grep Spec | grep $filter | awk -F "\"" '{print $4}' | sort | uniq | while read s
 do
  r=`echo ${s:0:-4}`
  c=`echo $r | awk -F "." '{print $NF}'`
  p=`echo $r | awk -F ".$c" '{print $1}'`

  if [ -n "$c" ] && [ ${#c} -gt 2 ];  then
    out=${dir}/${c}OperationImpl.java
    ./scripts/operation_template.sh $p $filter $c > $out
  fi
done
}

if [ ! -d ".git" ]; then
 echo "Please execute from the project root!"
 exit 1
fi

function generate_mappings() {
cat istio-model/src/main/resources/schema/istio-schema.json | grep Spec | awk -F "\"" '{print $4}' | sort | uniq | while read s
 do
  r=`echo ${s:0:-4}`
  c=`echo $r | awk -F "." '{print $NF}'`
  p=`echo $r | awk -F ".$c" '{print $1}'`

  if [ -n "$c" ] && [ ${#c} -gt 2 ];  then
    echo "$p#$c"
  fi
done
}

if [ ! -d ".git" ]; then
 echo "Please execute from the project root!"
 exit 1
fi

echo "Generating DSL interfaces"

generate_dsl "AdapterDsl" "adapter" ${CLIENT_SRC}
generate_dsl "MixerDsl" "mixer.template" ${CLIENT_SRC}
generate_dsl "IstioDsl" "istio.api" ${CLIENT_SRC}

echo "Generating Resource Handlers"
generate_handlers "adapter" ${CLIENT_SRC}/internal/handler/adapter ${CLIENT_RES}/META-INF/services/io.fabric8.kubernetes.client.ResourceHandler
generate_handlers "mixer.template" ${CLIENT_SRC}/internal/handler/mixer ${CLIENT_RES}/META-INF/services/io.fabric8.kubernetes.client.ResourceHandler
generate_handlers "api" ${CLIENT_SRC}/internal/handler/api ${CLIENT_RES}/META-INF/services/io.fabric8.kubernetes.client.ResourceHandler

echo "Generating Operation Implementations"
generate_operations "adapter" ${CLIENT_SRC}/internal/operation/adapter
generate_operations "mixer.template" ${CLIENT_SRC}/internal/operation/mixer
generate_operations "api" ${CLIENT_SRC}/internal/operation/api

echo "Generate Mappings"
generate_mappings | ./scripts/mapping_provider_template.sh> ${MODEL_SRC}/IstioResourceMappingProvider.java


