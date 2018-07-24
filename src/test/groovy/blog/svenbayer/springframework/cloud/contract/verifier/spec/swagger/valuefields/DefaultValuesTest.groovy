package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields

import spock.lang.Specification

class DefaultValuesTest extends Specification {

    def "Default value for float and double is set correctly"() {
        when:
            def actualDefaultFloat = DefaultValues.DEFAULT_FLOAT
        then:
            actualDefaultFloat == 1.1d
    }

    def "Default value for boolean is set correctly"() {
        when:
            def actualDefaultBoolean = DefaultValues.DEFAULT_BOOLEAN
        then:
            // is true
            actualDefaultBoolean
    }

    def "Default value for int is set correctly"() {
        when:
            def actualDefaultInt = DefaultValues.DEFAULT_INT
        then:
            actualDefaultInt == 1
    }
}
