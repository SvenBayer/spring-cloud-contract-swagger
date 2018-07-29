package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.TestContractEquals
import org.springframework.cloud.contract.spec.Contract
import org.springframework.cloud.contract.spec.internal.DslProperty
import spock.lang.Specification
import spock.lang.Subject

import java.util.regex.Pattern

/**
 * @author Sven Bayer
 */
class SinglePathParamSwaggerContractSpec extends Specification {

    @Subject
    SwaggerContractConverter converter = new SwaggerContractConverter()
    TestContractEquals testContractEquals = new TestContractEquals()

    def "should convert from single swagger with path param to contract"() {
        given:
        File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/single_pathparam_swagger.yml").toURI())
        Contract expectedContract = Contract.make {
            label("takeoff_coffee_bean_rocket")
            name("1_takeoff_rocket_POST")
            description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
            priority(1)
            request {
                method(POST())
                urlPath("/coffee-rocket-service/v1.0/takeoff/rocket") {
                    queryParameters {
                        parameter("withWormhole", new DslProperty(Pattern.compile("(true|false)"), true))
                        parameter("viaHyperLoop", new DslProperty(Pattern.compile("(true|false)"), true))
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                    contentType(applicationJson())
                }
                body(
                        """{
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ],
  "boxes" : [ "boxes" ],
  "fuel" : 1.1,
  "weight" : 1.1,
  "itinerary" : {
    "destination" : "destination",
    "departure" : "departure"
  },
  "rocketName" : "rocketName"
}""")
            }
            response {
                status(201)
                headers {
                    header("X-RateLimit-Limit", 1)
                    contentType(applicationJson())
                }
                body(
                        """{
  "name" : "name"
}""")
            }
        }
        when:
        Collection<Contract> contracts = converter.convertFrom(singleSwaggerYaml)
        then:
        testContractEquals.assertContractEquals(Collections.singleton(expectedContract), contracts)
    }
}