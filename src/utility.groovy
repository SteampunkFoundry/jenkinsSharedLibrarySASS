class sharedUtility implements Serializable {

    private final def _ // the member variable that contains the jenkinsFileScript


    sharedUtility(def jenkinsFileScript) {
        _ = jenkinsFileScript
        this.buildTagName = _.env.BUILD_TAG.replaceAll(' ','-')
    }



    sharedUtility tester() {
        _.echo "This is a debug statement"
    }


}