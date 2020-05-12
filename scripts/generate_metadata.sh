#!/usr/bin/env bash

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
ISTIO_VERSION=$(grep istio.io/istio go.mod | cut -d'/' -f4 | cut -d' ' -f3 | tr -d '[:space:]')
ISTIO_DIR="istio-$ISTIO_VERSION"
if [ ! -d "$ISTIO_DIR" ]; then
  # if istio version is not already downloaded, download it
  curl -L https://git.io/getLatestIstio | ISTIO_VERSION="${ISTIO_VERSION}" sh -
else
  echo "Istio version $ISTIO_VERSION is already present locally, using it"
fi

# Generate CRD information
more "$ISTIO_DIR"/install/kubernetes/helm/istio-init/files/crd*.yaml |
  #  yq '"\(.spec.names.kind)=\(.metadata.name) | istio=\(.metadata.labels.istio // "") | version=\(.spec.versions[0].name)"' | # later CRD defs use versions instead of version
  yq -r '"\(.spec.names.kind)=\(.metadata.name)| istio=\(.metadata.labels.istio // "")| version=\(.spec.version)"' |
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
