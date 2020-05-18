#!/usr/bin/env bash

set -e

DECL_DIR=${PWD}/istio-common/src/main/resources
CRD_FILE=${DECL_DIR}/istio-crd.properties
APIS_TMP=${DECL_DIR}/apis_dir.tmp
ADAPTERS_TMP=${DECL_DIR}/adapters_dir.tmp
TEMPLATES_TMP=${DECL_DIR}/templates_dir.tmp
PACKAGES_CSV=${DECL_DIR}/packages.csv

# Remove previously generated lines
sed -e '/##/q' ${PACKAGES_CSV} >${PACKAGES_CSV}.new
rm ${PACKAGES_CSV}
mv ${PACKAGES_CSV}.new ${PACKAGES_CSV}

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

echo "Using Istio version ${ISTIO_VERSION}"
ISTIO_DIR="istio-$ISTIO_VERSION"
if [ ! -d "$ISTIO_DIR" ]; then
  # if istio version is not already downloaded, download it
  curl -L https://github.com/istio/istio/archive/"${ISTIO_VERSION}".zip --output "${ISTIO_VERSION}".zip
  unzip "${ISTIO_VERSION}".zip
  rm -f "${ISTIO_VERSION}".zip
else
  echo "Istio version $ISTIO_VERSION is already present locally, using it"
fi

go get istio.io/istio@"${ISTIO_VERSION}"
go get istio.io/api@"${ISTIO_VERSION}"

# Generate CRD information
cat "$ISTIO_DIR"/install/kubernetes/helm/istio-init/files/crd*.yaml |
  yq -r '.spec as $s | .metadata as $m | $s.versions[] | "\(.name).\($s.names.kind)=\($m.name) | istio=\($m.labels.istio // "")"' |
  grep istio.io |
  sort -f >"${CRD_FILE}"

if [ ! -d "$ISTIO_DIR/api" ]; then
  pushd "${ISTIO_DIR}" || exit
  git clone --depth 1 https://github.com/istio/api.git --branch "${ISTIO_VERSION}" --single-branch 2>/dev/null
  popd || exit
fi

if [ ! -d "$ISTIO_DIR/istio" ]; then
  pushd "${ISTIO_DIR}" || exit
  git clone --depth 1 https://github.com/istio/istio.git --branch "${ISTIO_VERSION}" --single-branch 2>/dev/null
  popd || exit
fi

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
