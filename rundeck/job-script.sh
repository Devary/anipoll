#!/usr/bin/env bash
set -euo pipefail

echo "WORKSPACE=${RD_OPTION_WORKSPACE:-}"
echo "IMAGE=${RD_OPTION_IMAGE:-}"
echo "TAG=${RD_OPTION_TAG:-}"
echo "NAMESPACE=${RD_OPTION_NAMESPACE:-}"
echo "DEPLOYMENT=${RD_OPTION_DEPLOYMENT:-}"
echo "CONTAINER=${RD_OPTION_CONTAINER:-}"
echo "PORT=${RD_OPTION_PORT:-}"

WORKSPACE="${RD_OPTION_WORKSPACE}"
cd "${WORKSPACE}"

./k8s/deploy.sh \
  "${RD_OPTION_IMAGE:-}" \
  "${RD_OPTION_TAG:-}" \
  "${RD_OPTION_NAMESPACE:-default}" \
  "${RD_OPTION_DEPLOYMENT:-anipoll}" \
  "${RD_OPTION_CONTAINER:-anipoll}" \
  "${RD_OPTION_PORT:-8080}" \
  "${WORKSPACE}"
