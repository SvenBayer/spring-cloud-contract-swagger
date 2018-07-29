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
class PatternSwaggerContractSpec extends Specification {

    @Subject
    SwaggerContractConverter converter = new SwaggerContractConverter()
    TestContractEquals testContractEquals = new TestContractEquals()

    def "should convert from single swagger to contract"() {
        given:
        File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/pattern/pattern_swagger.yml").toURI())
        Contract expectedContract = Contract.make {
            label("takeoff_coffee_bean_rocket")
            name("1_takeoff_POST")
            description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
            priority(1)
            request {
                method(POST())
                urlPath("/coffee-rocket-service/v1.0/takeoff") {
                    queryParameters {
                        parameter("wormholeName", new DslProperty(Pattern.compile("[A-Z]{1}_[0-9]{3}"), "X_510"))
                        parameter("viaHyperLoop", new DslProperty(Pattern.compile("false"), false))
                    }
                }
                headers {
                    // pattern does not work
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                    contentType(applicationJson())
                }
                body("""{
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
  "rocketName" : "RocketB"
}""")
            }
            response {
                status(201)
                headers {
                    header("X-RateLimit-Limit", new DslProperty(5))
                    contentType(allValue())
                }
                body("""{
  "asteroids" : [ {
    "shape" : "ROUND",
    "aliens" : [ {
      "heads" : [ "heads" ]
    } ],
    "name" : "name",
    "speed" : 1,
    "istransparent" : true
  } ],
  "size" : 1,
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