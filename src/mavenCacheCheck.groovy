class SharedLib implements Serializable {

    private final def _ // the member variable that contains the jenkinsFileScript


    // the parsed contents of the SFDX project's configuration

    SharedLib(def jenkinsFileScript) {
        _ = jenkinsFileScript
        this.buildTagName = _.env.BUILD_TAG.replaceAll(' ','-')
    }



    SharedLib initializ() {
        //Get the node so we can get the availability zone
        def kubenode=sh returnStdout: true, script: "kubectl get pod -o=custom-columns=NODE:.spec.nodeName,NAME:.metadata.name -n cistack | grep ${_.env.kubelabel} | sed -e 's/  .*//g'"
        kubenode=kubenode.trim()
        def zone=sh returnStdout: true, script: "kubectl describe node \"${kubenode}\"| grep ProviderID | sed -e 's/.*aws:\\/\\/\\///g' | sed -e 's/\\/.*//g'"
        zone=zone.trim()
        //def branch=_.env.BRANCH_NAME
        // Sanitize the branch name so it can be made part of the pvc
        branch=_.branch.replaceAll("[/_]","-").replaceAll("[^-.a-zA-Z0-9]","").take(62-zone.length()).toLowerCase()
        def pvc = "${branch}-${zone}"

        echo "I am checking for a maven cache for ${branch} in ${zone}"
        // Create a pvc base on the AZ
        def claim = _.readYaml text:"""
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: ${pvc}
  namespace: ${_.env.namespace}
  annotations:
    purpose: mavencache
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: ebs-sc
  resources:
    requests:
      storage: 4Gi
"""
        _.sh 'rm -rf dynamicclaim.yaml'
        _.writeYaml file: 'dynamicclaim.yaml', data: claim
        _.sh 'kubectl apply -f dynamicclaim.yaml'
        return this
    }


}