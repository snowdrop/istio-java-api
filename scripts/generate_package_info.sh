#!/usr/bin/env bash

DECL_DIR=${PWD}/istio-common/src/main/resources
ADAPTER_CRDS=${DECL_DIR}/adapter_crds.properties
TEMPLATE_CRDS=${DECL_DIR}/template_crds.properties
OTHER_CRDS=${DECL_DIR}/other_crds.properties
CRD_FILE=${DECL_DIR}/crd_list.tmp
APIS_TMP=${DECL_DIR}/apis_dir.tmp
ADAPTERS_TMP=${DECL_DIR}/adapters_dir.tmp
TEMPLATES_TMP=${DECL_DIR}/templates_dir.tmp
PACKAGES_CSV=${DECL_DIR}/packages.csv

# Remove previously generated lines
sed -e '/##/q' ${PACKAGES_CSV} > ${PACKAGES_CSV}.new
rm ${PACKAGES_CSV}
mv ${PACKAGES_CSV}.new ${PACKAGES_CSV}

if [[ -z "$GOPATH" ]]; then
    echo "You must set and export your GOPATH environment variable to use this script!"
    exit 1
fi

pushd ${GOPATH}/src > /dev/null
ls -d istio.io/api/*/v* > ${APIS_TMP}
ls -d istio.io/istio/mixer/adapter/*/config > ${ADAPTERS_TMP}
ls -d istio.io/istio/mixer/template/*/ >${TEMPLATES_TMP}
popd > /dev/null

cat ${APIS_TMP} | go run cmd/packageGen/packageGen.go api >> ${PACKAGES_CSV}
cat ${ADAPTERS_TMP} | go run cmd/packageGen/packageGen.go adapter >> ${PACKAGES_CSV}
cat ${TEMPLATES_TMP} | go run cmd/packageGen/packageGen.go template >> ${PACKAGES_CSV}

#rm -f ${DECL_DIR}/*.tmp
rm -f ${PACKAGES_CSV}.bak