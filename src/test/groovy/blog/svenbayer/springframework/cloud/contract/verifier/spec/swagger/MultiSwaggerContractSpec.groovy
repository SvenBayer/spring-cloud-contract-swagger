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
class MultiSwaggerContractSpec extends Specification {

    @Subject
    SwaggerContractConverter converter = new SwaggerContractConverter()
    TestContractEquals testContractEquals = new TestContractEquals()

    def "should convert from multiple swagger to contract"() {
        given:
        File multipleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/multiple/multiple_swagger.yml").toURI())
        Contract expectedContract0 = Contract.make {
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
                        """{
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ],
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
                    contentType(allValue())
                }
                body(
                        """{
  "asteroids" : [ {
    "shape" : "ROUND",
    "name" : "name",
    "speed" : 1
  } ],
  "size" : 1,
  "name" : "name"
}""")
            }
        }
        Contract expectedContract1 = Contract.make {
            label("land")
            name("3_land_POST")
            description("Lands a coffee rocket on a bean planet and returns the coffee rocket.")
            priority(3)
            request {
                method(POST())
                urlPath("/coffee-rocket-service/v1.0/land") {
                    queryParameters {
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                    contentType(applicationJson())
                }
                body(
                        """{
  "asteroids" : [ {
    "shape" : "ROUND",
    "name" : "name",
    "speed" : 1
  } ],
  "size" : 1,
  "name" : "name"
}""")
            }
            response {
                status(201)
                headers {
                    contentType(applicationJson())
                }
                body(
                        """{
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ],
  "fuel" : 1.1,
  "weight" : 1.1,
  "itinerary" : {
    "destination" : "destination",
    "departure" : "departure"
  },
  "rocketName" : "rocketName"
}""")
            }
        }
        Contract expectedContract2 = Contract.make {
            name("4_find_planets_solarSystem_GET")
            description("Find planets in the given Solar System.")
            priority(4)
            request {
                method(GET())
                urlPath("/coffee-rocket-service/v1.0/find/planets/solarSystem") {
                    queryParameters {
                        parameter("planetName", new DslProperty(Pattern.compile(".+"), "planetName"))
                        parameter("numAsteroids", new DslProperty(Pattern.compile("[0-9]+"), 1))
                        parameter("minSize", new DslProperty(Pattern.compile("[0-9]+"), 1))
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                    contentType(applicationJson())
                }
            }
            response {
                status(200)
                headers {
                    contentType(allValue())
                }
                body(
                        """{
  "asteroids" : [ {
    "shape" : "ROUND",
    "name" : "name",
    "speed" : 1
  } ],
  "size" : 1,
  "name" : "name"
}""")
            }
        }
        Contract expectedContract3 = Contract.make {
            name("5_planets_planet_asteroids_asteroidName_GET")
            description("Retrieve existing bean asteroids from a bean planet.")
            priority(5)
            request {
                method(GET())
                urlPath("/coffee-rocket-service/v1.0/planets/planet/asteroids/asteroidName") {
                    queryParameters {
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                }
            }
            response {
                status(200)
                headers {
                    contentType(allValue())
                }
                body(
                        """{
  "shape" : "ROUND",
  "name" : "name",
  "speed" : 1
}""")
            }
        }
        Contract expectedContract4 = Contract.make {
            name("6_planets_planet_asteroids_asteroidName_PUT")
            description("Updates an existing bean asteroids of a bean planet.")
            priority(6)
            request {
                method(PUT())
                urlPath("/coffee-rocket-service/v1.0/planets/planet/asteroids/asteroidName") {
                    queryParameters {
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                    contentType(applicationJson())
                }
                body(
                        """{
  "shape" : "ROUND",
  "name" : "name",
  "speed" : 1
}""")
            }
            response {
                status(200)
                headers {
                    contentType(allValue())
                }
                body(
                        """{
  "x" : 1,
  "y" : 1
}""")
            }
        }
        Contract expectedContract5 = Contract.make {
            name("7_planets_planet_asteroids_asteroidName_POST")
            description("Adds a bean asteroids to a bean planet.")
            priority(7)
            request {
                method(POST())
                urlPath("/coffee-rocket-service/v1.0/planets/planet/asteroids/asteroidName") {
                    queryParameters {
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                    contentType(applicationJson())
                }
                body(
                        """{
  "shape" : "ROUND",
  "name" : "name",
  "speed" : 1
}""")
            }
            response {
                status(200)
                headers {
                    contentType(allValue())
                }
                body(
                        """{
  "x" : 1,
  "y" : 1
}""")
            }
        }
        Contract expectedContract6 = Contract.make {
            name("8_planets_planet_asteroids_asteroidName_DELETE")
            description("Removes an existing bean asteroids from a bean planet.")
            priority(8)
            request {
                method(DELETE())
                urlPath("/coffee-rocket-service/v1.0/planets/planet/asteroids/asteroidName") {
                    queryParameters {
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                }
            }
            response {
                status(204)
                headers {
                    contentType(allValue())
                }
            }
        }
        when:
        Collection<Contract> contracts = converter.convertFrom(multipleSwaggerYaml)
        then:
        testContractEquals.assertContractEquals([expectedContract0, expectedContract1, expectedContract2, expectedContract3, expectedContract4, expectedContract5, expectedContract6], contracts)
    }
}