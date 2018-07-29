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
class ComplexSwaggerContractSpec extends Specification {

    @Subject
    SwaggerContractConverter converter = new SwaggerContractConverter()
    TestContractEquals testContractEquals = new TestContractEquals()

    def "should convert from single parametrized swagger to contract"() {
        given:
        File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/complex_definitions/param_swagger.yml").toURI())
        Contract expectedContract = Contract.make {
            label("takeoff_coffee_bean_rocket")
            name("1_takeoff_POST")
            description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
            priority(1)
            request {
                method(POST())
                urlPath("/coffee-rocket-service/v1.0/takeoff") {
                    queryParameters {
                        parameter("withWormhole", new DslProperty(Pattern.compile("(true|false)"), false))
                        parameter("viaHyperLoop", new DslProperty(Pattern.compile("(true|false)"), false))
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                    contentType(applicationJson())
                }
                body(
                        """{
  "beanonauts" : [ {
    "name" : "Beanon Beanusk",
    "age" : 47
  } ],
  "fuel" : 980.3,
  "weight" : 20.85,
  "itinerary" : {
    "destination" : "Mars",
    "departure" : "Earth"
  },
  "rocketName" : "BeanRocket Heavy"
}""")
            }
            response {
                status(201)
                headers {
                    header("X-RateLimit-Limit", 1)
                    contentType(allValue())
                }
                body(
                        """{
  "asteroids" : [ {
    "shape" : "BEAN",
    "name" : "Phobos",
    "speed" : 23
  } ],
  "size" : 6779,
  "name" : "Mars"
}""")
            }
        }
        when:
        Collection<Contract> contracts = converter.convertFrom(singleSwaggerYaml)
        then:
        testContractEquals.assertContractEquals(Collections.singleton(expectedContract), contracts)
    }
}