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
class ExampleContractConverterSpec extends Specification {

    @Subject SwaggerContractConverter converter = new SwaggerContractConverter()
    TestContractEquals testContractEquals = new TestContractEquals();

    def "should convert from swagger with json example to contract"() {
        given:
        File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/example/json_example_param_no_ref_swagger.yml").toURI())
        Contract expectedContract = Contract.make {
            label("takeoff_coffee_bean_rocket")
            name("1_takeoff_POST")
            description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
            priority(1)
            request {
                method(POST())
                urlPath("/coffee-rocket-service/v1.0/takeoff") {
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
                        """{ "beanonauts": [ { "name": "Tom Hanks", "age": 62.0 }], "fuel": 300000.5, "weight": 45931.0, "itinerary": { "destination": "Moon", "departure": "Earth" }, "rocketName": "Beanpollo 13" }""")
            }
            response {
                status(201)
                headers {
                    header("X-RateLimit-Limit", 1)
                    contentType(allValue())
                }
                body(
                        """{ "asteroids" : [ { "shape" : "BEAN", "aliens" : [ { "heads" : [ "big ones" ] } ], "name" : "Beansteroid", "speed" : 250, "istransparent" : false } ], "size" : 1, "name" : "Beanlet" }""")
            }
        }
        when:
        Collection<Contract> contracts = converter.convertFrom(singleSwaggerYaml)
        then:
        testContractEquals.assertContractEquals(Collections.singleton(expectedContract), contracts)
    }
}
