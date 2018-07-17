# Spring Cloud Contract Swagger
**Converts Swagger files to contracts for Spring Cloud Contract**

This project enables Spring Cloud Contract to parse [Swagger API 2.0 specifications](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md) as Spring Cloud Contracts.

## Usage
You can check out [spring-cloud-contract-swagger-sample](https://github.com/SvenBayer/spring-cloud-contract-swagger-sample) for examples.
###Producer
To convert a Swagger file to a Spring Cloud Contract and to execute it against a Producer, add the **spring-cloud-contract-maven-plugin** as plugin and the converter **spring-cloud-contract-swagger** as plugin-dependency.
### Consumer
For the consumer, add as dependencies the **spring-cloud-starter-contract-stub-runner** and the converter **spring-cloud-contract-swagger**.
## Further Information
Read the blog about [Consumer Driven Contracts with Swagger](https://svenbayer.blog/cdc-with-swagger) for more information.
