package gov.dhs.ice.secdevops

class sharedUtility implements Serializable {

    private final def _ // the member variable that contains the jenkinsFileScript
    def steps

    sharedUtility(steps) {
        this.steps = steps
    }

    def tester(args) {
        steps.echo "This is a debug statement: ${args}"
    }
}
