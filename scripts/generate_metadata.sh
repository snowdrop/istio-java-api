#!/usr/bin/env bash

set -e

DECL_DIR=${PWD}/istio-common/src/main/resources
CRD_FILE=${DECL_DIR}/istio-crd.properties
APIS_TMP=${DECL_DIR}/apis_dir.tmp
ADAPTERS_TMP=${DECL_DIR}/adapters_dir.tmp
TEMPLATES_TMP=${DECL_DIR}/templates_dir.tmp
PACKAGES_CSV=${DECL_DIR}/packages.csv

function istioVersion() {
  istioVersion=$(curl -L -s https://api.github.com/repos/istio/istio/releases |
    grep tag_name | sed "s/ *\"tag_name\": *\"\\(.*\\)\",*/\\1/" |
    grep -v -E "(alpha|beta|rc)\.[0-9]$" | sort -t"." -k 1,1 -k 2,2 -k 3,3 -k 4,4 | tail -n 1)
  echo "${istioVersion}"
}

# Retrieve Istio version if not already done
if [ "$1" == "version" ]; then
  istioVersion
  exit
elif [ -n "$1" ]; then
  ISTIO_VERSION="${1}"
else
  ISTIO_VERSION=$(istioVersion)
fi

# Remove previously generated lines
sed -e '/##/q' ${PACKAGES_CSV} >${PACKAGES_CSV}.new
rm ${PACKAGES_CSV}
mv ${PACKAGES_CSV}.new ${PACKAGES_CSV}

echo "Using Istio version ${ISTIO_VERSION}"
ISTIO_DIR="istio-$ISTIO_VERSION"
if [ ! -d "$ISTIO_DIR" ]; then
  mkdir "${ISTIO_DIR}"
else
  echo "Istio version $ISTIO_VERSION is already present locally, using it"
fi

go get istio.io/istio@"${ISTIO_VERSION}"
go get istio.io/api@"${ISTIO_VERSION}"

if [ ! -d "$ISTIO_DIR/api" ]; then
  pushd "${ISTIO_DIR}" >/dev/null || exit
  git clone --depth 1 https://github.com/istio/api.git --branch "${ISTIO_VERSION}" --single-branch 2>/dev/null
  popd >/dev/null || exit
fi

if [ ! -d "$ISTIO_DIR/istio" ]; then
  pushd "${ISTIO_DIR}" >/dev/null || exit
  git clone --depth 1 https://github.com/istio/istio.git --branch "${ISTIO_VERSION}" --single-branch 2>/dev/null
  popd >/dev/null || exit
fi

# Generate CRD information
cat "$ISTIO_DIR"/api/kubernetes/customresourcedefinitions.gen.yaml |
  yq -r '(.spec // empty) as $s | (.metadata // empty) as $m | $s.versions[] | "\(.name).\($s.names.kind)=\($m.name) | istio=\($m.labels.istio // "")"' |
  grep istio.io |
  sort -f >"${CRD_FILE}"

ls -d "${ISTIO_DIR}"/api/*/v* | sed "s/${ISTIO_DIR}/istio.io/" >"${APIS_TMP}"
ls -d "${ISTIO_DIR}"/istio/mixer/adapter/*/config | sed "s/${ISTIO_DIR}/istio.io/" >"${ADAPTERS_TMP}"
ls -d "${ISTIO_DIR}"/istio/mixer/template/*/ | sed "s/${ISTIO_DIR}/istio.io/" >"${TEMPLATES_TMP}"

{
  go run cmd/packageGen/packageGen.go api <"${APIS_TMP}"
  go run cmd/packageGen/packageGen.go adapter <"${ADAPTERS_TMP}"
  go run cmd/packageGen/packageGen.go template <"${TEMPLATES_TMP}"
} >>"${PACKAGES_CSV}"

rm -f ${DECL_DIR}/*.tmp
rm -f ${PACKAGES_CSV}.bak
