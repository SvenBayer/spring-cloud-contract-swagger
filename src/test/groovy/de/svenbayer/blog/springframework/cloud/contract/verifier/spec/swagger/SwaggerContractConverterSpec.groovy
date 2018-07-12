package de.svenbayer.blog.springframework.cloud.contract.verifier.spec.swagger

import io.swagger.models.Swagger
import org.springframework.cloud.contract.spec.Contract
import org.springframework.cloud.contract.spec.internal.DslProperty
import spock.lang.Specification
import spock.lang.Subject

import java.util.regex.Pattern

/**
 * @author Sven Bayer
 */
class SwaggerContractConverterSpec extends Specification {

    @Subject SwaggerContractConverter converter = new SwaggerContractConverter()

    def "should accept yaml files that are swagger files"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/single_swagger.yml").toURI())
        expect:
            converter.isAccepted(singleSwaggerYaml)
    }

    def "should reject yaml files that are swagger files"() {
        given:
            File invalidSwagger = new File(SwaggerContractConverterSpec.getResource("/swagger/invalid_swagger.yml").toURI())
        expect:
            !converter.isAccepted(invalidSwagger)
    }

    def "should convert from single swagger to contract"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/single_swagger.yml").toURI())
            Contract expectedContract = Contract.make {
                label("takeoff_coffee_bean_rocket")
                name("Sends a coffee rocket to a bean planet and returns the bean planet.")
                description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
                priority(0)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/takeoff") {
                        queryParameters {
                            parameter("withWormhole", new DslProperty(Pattern.compile("(true|false)"), true))
                            parameter("viaHyperLoop", new DslProperty(Pattern.compile("(true|false)"), true))
                        }
                    }
                    headers {
                        header("X-Request-ID", "123456")
                        contentType(applicationJson())
                    }
                    body(
"""{
  "rocketName" : "rocketName",
  "itinerary" : {
    "departure" : "departure",
    "destination" : "destination"
  },
  "fuel" : 1.1,
  "weight" : 1.1,
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ]
}""")
                }
                response {
                    status(201)
                    headers {
                        contentType(allValue())
                    }
                    body(
"""{
  "name" : "name",
  "size" : 1,
  "asteroids" : [ {
    "name" : "name",
    "speed" : 1,
    "shape" : "ROUND"
  } ]
}""")
                }
            }
        when:
            Collection<Contract> contracts = converter.convertFrom(singleSwaggerYaml)
        then:
            contracts.toString() == [expectedContract].toString()
    }

    def "should convert from single parametrized swagger to contract"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/param_swagger.yml").toURI())
            Contract expectedContract = Contract.make {
                label("takeoff_coffee_bean_rocket")
                name("Sends a coffee rocket to a bean planet and returns the bean planet.")
                description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
                priority(0)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/takeoff") {
                        queryParameters {
                            parameter("withWormhole", false)
                            parameter("viaHyperLoop", false)
                        }
                    }
                    headers {
                        header("X-Request-ID", "123456")
                        contentType(applicationJson())
                    }
                    body(
"""{
  "rocketName" : "BeanRocket Heavy",
  "itinerary" : {
    "departure" : "Earth",
    "destination" : "Mars"
  },
  "fuel" : 980.3,
  "weight" : 20.85,
  "beanonauts" : [ {
    "name" : "Beanon Beanusk",
    "age" : 47
  } ]
}""")
                }
                response {
                    status(201)
                    headers {
                        contentType(allValue())
                    }
                    body(
"""{
  "name" : "Mars",
  "size" : 6779,
  "asteroids" : [ {
    "name" : "Phobos",
    "speed" : 23,
    "shape" : "BEAN"
  } ]
}""")
                }
            }
        when:
            Collection<Contract> contracts = converter.convertFrom(singleSwaggerYaml)
        then:
            contracts == [expectedContract]
    }

    def "should convert from multiple swagger to contract"() {
        given:
            File multipleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/multiple_swagger.yml").toURI())
            Contract expectedContract0 = Contract.make {
                label("takeoff_coffee_bean_rocket")
                name("Sends a coffee rocket to a bean planet and returns the bean planet.")
                description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
                priority(0)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/takeoff") {
                        queryParameters {
                            parameter("withWormhole", new DslProperty(Pattern.compile("(true|false)"), true))
                            parameter("viaHyperLoop", new DslProperty(Pattern.compile("(true|false)"), true))
                        }
                    }
                    headers {
                        header("X-Request-ID", "123456")
                        contentType(applicationJson())
                    }
                    body(
"""{
  "rocketName" : "rocketName",
  "itinerary" : {
    "departure" : "departure",
    "destination" : "destination"
  },
  "fuel" : 1.1,
  "weight" : 1.1,
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ]
}""")
                }
                response {
                    status(201)
                    headers {
                        contentType(allValue())
                    }
                    body(
"""{
  "name" : "name",
  "size" : 1,
  "asteroids" : [ {
    "name" : "name",
    "speed" : 1,
    "shape" : "ROUND"
  } ]
}""")
                }
            }
            Contract expectedContract1 = Contract.make {
                label("land")
                name("Landing coffee rocket")
                description("Lands a coffee rocket on a bean planet and returns the coffee rocket.")
                priority(1)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/land") {
                        queryParameters {
                        }
                    }
                    headers {
                        header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                        contentType(applicationJson())
                    }
                    body(
"""{
  "name" : "name",
  "size" : 1,
  "asteroids" : [ {
    "name" : "name",
    "speed" : 1,
    "shape" : "ROUND"
  } ]
}""")
                }
                response {
                    status(201)
                    headers {
                        contentType(applicationJson())
                    }
                    body(
                            """{
  "rocketName" : "rocketName",
  "itinerary" : {
    "departure" : "departure",
    "destination" : "destination"
  },
  "fuel" : 1.1,
  "weight" : 1.1,
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ]
}""")
                }
            }
            Contract expectedContract2 = Contract.make {
                description("Find planets in the given Solar System.")
                priority(2)
                request {
                    method(GET())
                    urlPath("/coffee-rocket-service/v1.0/find/planets/{solarSystem}") {
                        queryParameters {
                            parameter("solarSystem", new DslProperty(Pattern.compile(".+"), "solarSystem"))
                            parameter("planetName", new DslProperty(Pattern.compile(".+"), "planetName"))
                            parameter("numAsteroids", new DslProperty(Pattern.compile("[0-9]+"), 1))
                            parameter("minSize", new DslProperty(Pattern.compile("[0-9]+"), 1))
                        }
                    }
                    headers {
                        header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
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
  "name" : "name",
  "size" : 1,
  "asteroids" : [ {
    "name" : "name",
    "speed" : 1,
    "shape" : "ROUND"
  } ]
}""")
                }
            }
            Contract expectedContract3 = Contract.make {
                description("Retrieve existing bean asteroids from a bean planet.")
                priority(3)
                request {
                    method(GET())
                    urlPath("/coffee-rocket-service/v1.0/planets/{planet}/asteroids/{asteroidName}") {
                        queryParameters {
                            parameter("planet", new DslProperty(Pattern.compile(".+"), "planet"))
                            parameter("asteroidName", new DslProperty(Pattern.compile(".+"), "asteroidName"))
                        }
                    }
                    headers {
                        header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                    }
                }
                response {
                    status(200)
                    headers {
                        contentType(allValue())
                    }
                    body(
                            """{
  "name" : "name",
  "speed" : 1,
  "shape" : "ROUND"
}""")
                }
            }
        Contract expectedContract4 = Contract.make {
            description("Updates an existing bean asteroids of a bean planet.")
            priority(4)
            request {
                method(PUT())
                urlPath("/coffee-rocket-service/v1.0/planets/{planet}/asteroids/{asteroidName}") {
                    queryParameters {
                        parameter("planet", new DslProperty(Pattern.compile(".+"), "planet"))
                        parameter("asteroidName", new DslProperty(Pattern.compile(".+"), "asteroidName"))
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                    contentType(applicationJson())
                }
                body(
                        """{
  "name" : "name",
  "speed" : 1,
  "shape" : "ROUND"
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
                description("Adds a bean asteroids to a bean planet.")
                priority(5)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/planets/{planet}/asteroids/{asteroidName}") {
                        queryParameters {
                            parameter("planet", new DslProperty(Pattern.compile(".+"), "planet"))
                            parameter("asteroidName", new DslProperty(Pattern.compile(".+"), "asteroidName"))
                        }
                    }
                    headers {
                        header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                        contentType(applicationJson())
                    }
                    body(
                            """{
  "name" : "name",
  "speed" : 1,
  "shape" : "ROUND"
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
                description("Removes an existing bean asteroids from a bean planet.")
                priority(6)
                request {
                    method(DELETE())
                    urlPath("/coffee-rocket-service/v1.0/planets/{planet}/asteroids/{asteroidName}") {
                        queryParameters {
                            parameter("planet", new DslProperty(Pattern.compile(".+"), "planet"))
                            parameter("asteroidName", new DslProperty(Pattern.compile("[a-z]+"), "asteroidName"))
                        }
                    }
                    headers {
                        header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
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
            contracts.size() == 7
            contracts.getAt(0).toString() == expectedContract0.toString()
            contracts.getAt(1).toString() == expectedContract1.toString()
            contracts.getAt(2).toString() == expectedContract2.toString()
            contracts.getAt(3).toString() == expectedContract3.toString()
            contracts.getAt(4).toString() == expectedContract4.toString()
            contracts.getAt(5).toString() == expectedContract5.toString()
            contracts.getAt(6).toString() == expectedContract6.toString()
            contracts.toString() == [expectedContract0, expectedContract1, expectedContract2, expectedContract3, expectedContract4, expectedContract5, expectedContract6].toString()
    }

    def "should expect_exception_when_converting_contract_to_swagger"() {
        given:
            List<Contract> springCloudContracts = new ArrayList<>();
        when:
            Swagger swagger = converter.convertTo(springCloudContracts)
        then:
            swagger != null
    }
}
