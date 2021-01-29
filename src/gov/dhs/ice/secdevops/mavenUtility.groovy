package gov.dhs.ice.secdevops

class mavenUtility implements Serializable {

    private final def _ // the member variable that contains the jenkinsFileScript
    def steps

    mavenUtility(steps) {
        this.steps = steps
    }

    def tester(arg) {
        steps.echo "This is a debug statement: ${arg}"
    }

    def String getMavenCache(){
        def kubelabel = "kubepod-${UUID.randomUUID().toString()}"
        def zone                   // The AZ in AWS we are in
        def kubenode               //The name of the kube node we are on
        def pvc                    // Name of the PVC for this branch
        def branch                 // Branch name
        def namespace = "cistack"  // Namespace pods execute in
        podTemplate(
                label: kubelabel,
                containers: [
                        containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl', ttyEnabled: true, command: '/bin/cat')
                ],
                serviceAccount: 'jenkins',
                nodeSelector: 'role=workers'
        ) {
            node(kubelabel) {
                stage('cache check') {

                    container('kubectl'){
                        //Get the node so we can get the availability zone
                        kubenode=steps.sh returnStdout: true, script: "kubectl get pod -o=custom-columns=NODE:.spec.nodeName,NAME:.metadata.name -n cistack | grep ${kubelabel} | sed -e 's/  .*//g'"
                        kubenode=kubenode.trim()
                        zone=steps.sh returnStdout: true, script: "kubectl describe node \"${kubenode}\"| grep ProviderID | sed -e 's/.*aws:\\/\\/\\///g' | sed -e 's/\\/.*//g'"
                        zone=zone.trim()
                        branch=steps.env.BRANCH_NAME
                        // Sanitize the branch name so it can be made part of the pvc
                        branch=branch.replaceAll("[/_]","-").replaceAll("[^-.a-zA-Z0-9]","").take(62-zone.length()).toLowerCase();
                        pvc = "${branch}-${zone}"

                        steps.echo "I am checking for a maven cache for ${branch} in ${zone}"
                        // Create a pvc base on the AZ
/*                        def claim = steps.readYaml text:"""
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: ${pvc}
  namespace: ${namespace}
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
                        steps.sh 'rm -rf dynamicclaim.yaml'
                        steps.writeYaml file: 'dynamicclaim.yaml', data: claim
                        steps.sh 'kubectl apply -f dynamicclaim.yaml'

 */
                        return ${pvc}
                    }
                }
            }
        }
    }
}
