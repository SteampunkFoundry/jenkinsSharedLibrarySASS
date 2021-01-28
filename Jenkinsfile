@Library('jenkinsSharedLibrarySASS@feature/initial') _
def label = "ImageBuildPod-${UUID.randomUUID().toString()}"

podTemplate(label: label,
        containers: [
                containerTemplate(name: 'ansible', image: 'ansible/ansible-runner', ttyEnabled: true, command: 'cat'),
                containerTemplate(name: 'amazoncli', image: 'amazon/aws-cli', ttyEnabled: true, command: 'cat'),
                containerTemplate(name: 'packer', image: 'hashicorp/packer:latest', ttyEnabled: true, command: 'cat')
        ],
         nodeSelector: 'kubernetes.io/arch=amd64')
{
    def utils = new sharedUtility(this)
    node(label) {
       utils.tester("Print me for testing purposes")
    }
}
