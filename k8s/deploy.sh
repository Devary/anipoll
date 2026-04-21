  #!/usr/bin/env bash                                                                            
   set -euo pipefail                                                                              
                                                                                                  
   echo "ARG1_IMAGE=${1:-}"                                                                       
   echo "ARG2_TAG=${2:-}"                                                                         
   echo "ARG3_NAMESPACE=${3:-}"                                                                   
   echo "ARG4_DEPLOYMENT=${4:-}"                                                                  
   echo "ARG5_CONTAINER=${5:-}"                                                                   
   echo "ARG6_PORT=${6:-}"                                                                        
                                                                                                  
   IMAGE="${1:-${RD_OPTION_IMAGE:-}}"                                                             
   TAG="${2:-${RD_OPTION_TAG:-}}"                                                                 
   NAMESPACE="${3:-${RD_OPTION_NAMESPACE:-default}}"                                              
   DEPLOYMENT="${4:-${RD_OPTION_DEPLOYMENT:-anipoll}}"                                            
   CONTAINER="${5:-${RD_OPTION_CONTAINER:-anipoll}}"                                              
   PORT="${6:-${RD_OPTION_PORT:-8080}}"                                                           
                                                                                                  
   : "${IMAGE:?image required}"                                                                   
   : "${TAG:?tag required}"                                                                       
                                                                                                  
   FULL_IMAGE="${IMAGE}:${TAG}"                                                                   
                                                                                                  
   if [[ -z "${IMAGE}" || -z "${TAG}" ]]; then                                                    
     echo "ERROR: IMAGE or TAG is empty"                                                          
     exit 1                                                                                       
   fi                                                                                             
                                                                                                  
   if [[ "${FULL_IMAGE}" == :* || "${FULL_IMAGE}" == *: ]]; then                                  
     echo "ERROR: Invalid image reference: ${FULL_IMAGE}"                                         
     exit 1                                                                                       
   fi                                                                                             
                                                                                                  
   echo "IMAGE=${IMAGE}"                                                                          
   echo "TAG=${TAG}"                                                                              
   echo "FULL_IMAGE=${FULL_IMAGE}"                                                                
   echo "NAMESPACE=${NAMESPACE}"                                                                  
   echo "DEPLOYMENT=${DEPLOYMENT}"                                                                
   echo "CONTAINER=${CONTAINER}"                                                                  
   echo "PORT=${PORT}"                                                                            
                                                                                                  
   kubectl get namespace "${NAMESPACE}" >/dev/null 2>&1 || kubectl create namespace               
 "${NAMESPACE}"                                                                                   
                                                                                                  
   if kubectl get deployment "${DEPLOYMENT}" -n "${NAMESPACE}" >/dev/null 2>&1; then              
     echo "Updating existing deployment ${DEPLOYMENT} in namespace ${NAMESPACE} to ${FULL_IMAGE}" 
     kubectl -n "${NAMESPACE}" set image "deployment/${DEPLOYMENT}" "${CONTAINER}=${FULL_IMAGE}"  
   else                                                                                           
     echo "Creating deployment ${DEPLOYMENT} in namespace ${NAMESPACE} with image ${FULL_IMAGE}"  
     sed \                                                                                        
       -e "s|__NAMESPACE__|${NAMESPACE}|g" \                                                      
       -e "s|__DEPLOYMENT__|${DEPLOYMENT}| g" \                                                   
       -e "s|__CONTAINER__|${CONTAINER}|g" \                                                      
       -e "s|__IMAGE__|${FULL_IMAGE}|g" \                                                         
       -e "s|__PORT__|${PORT}|g" \                                                                
       k8s/deployment.yaml | kubectl apply -f -                                                   
                                                                                                  
     sed \                                                                                        
       -e "s|__NAMESPACE__|${NAMESPACE}|g" \                                                      
       -e "s|__DEPLOYMENT__|${DEPLOYMENT}| g" \                                                   
       -e "s|__PORT__|${PORT}|g" \                                                                
       k8s/service.yaml | kubectl apply -f -                                                      
   fi                                                                                             
                                                                                                  
   kubectl -n "${NAMESPACE}" rollout status "deployment/${DEPLOYMENT}" --timeout=300s             
   kubectl -n "${NAMESPACE}" get deployment "${DEPLOYMENT}" -o wide                               
   kubectl -n "${NAMESPACE}" get pods -l app="${DEPLOYMENT}" -o wide