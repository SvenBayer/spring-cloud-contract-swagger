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
                        contentType(allValue())
                    }
                    body(
"""{
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
            contracts.toString() == [expectedContract].toString()
    }


    def "should convert from single swagger with path param to contract"() {
        given:
            File singleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/single_pathparam_swagger.yml").toURI())
            Contract expectedContract = Contract.make {
                label("takeoff_coffee_bean_rocket")
                name("Sends a coffee rocket to a bean planet and returns the bean planet.")
                description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
                priority(0)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/takeoff/rocket") {
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
                name("Landing coffee rocket")
                description("Lands a coffee rocket on a bean planet and returns the coffee rocket.")
                priority(2)
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
                description("Find planets in the given Solar System.")
                priority(3)
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
                description("Retrieve existing bean asteroids from a bean planet.")
                priority(4)
                request {
                    method(GET())
                    urlPath("/coffee-rocket-service/v1.0/planets/planet/asteroids/asteroidName") {
                        queryParameters {
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
  "shape" : "ROUND",
  "name" : "name",
  "speed" : 1
}""")
                }
            }
        Contract expectedContract4 = Contract.make {
            description("Updates an existing bean asteroids of a bean planet.")
            priority(5)
            request {
                method(PUT())
                urlPath("/coffee-rocket-service/v1.0/planets/planet/asteroids/asteroidName") {
                    queryParameters {
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
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
                description("Adds a bean asteroids to a bean planet.")
                priority(6)
                request {
                    method(POST())
                    urlPath("/coffee-rocket-service/v1.0/planets/planet/asteroids/asteroidName") {
                        queryParameters {
                        }
                    }
                    headers {
                        header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
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
                description("Removes an existing bean asteroids from a bean planet.")
                priority(7)
                request {
                    method(DELETE())
                    urlPath("/coffee-rocket-service/v1.0/planets/planet/asteroids/asteroidName") {
                        queryParameters {
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
