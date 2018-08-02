package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.TestContractEquals
import io.swagger.models.Swagger
import org.springframework.cloud.contract.spec.Contract
import spock.lang.Specification
import spock.lang.Subject

/**
 * @author Sven Bayer
 */
class SwaggerContractConverterSpec extends Specification {

    @Subject SwaggerContractConverter converter = new SwaggerContractConverter()
    TestContractEquals testContractEquals = new TestContractEquals();

    def "should accept yaml files that are swagger files"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.class.getResource("/swagger/single_swagger.yml").toURI())
        expect:
            converter.isAccepted(singleSwaggerYaml)
    }

    def "should reject yaml files that are swagger files"() {
        given:
            File invalidSwagger = new File(SwaggerContractConverterSpec.getResource("/swagger/invalid_swagger.yml").toURI())
        expect:
            !converter.isAccepted(invalidSwagger)
    }

    def "should reject file that does not exist"() {
        given:
            File notExistingSwagger = new File("/aNotExistingFile")
        expect:
            !converter.isAccepted(notExistingSwagger)
    }

    def "should retrieve empty contract when converting from swagger"() {
        given:
            List<Contract> springCloudContracts = new ArrayList<>()
        when:
            Swagger swagger = converter.convertTo(springCloudContracts)
        then:
            swagger != null
    }
}
