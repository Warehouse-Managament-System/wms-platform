dependencies {
    implementation(project(":wms-common"))
    implementation(project(":reservation-service:booking-module"))
    implementation(project(":reservation-service:payment-module"))
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.spring.cloud.starter.circuitbreaker.resilience4j)
    implementation(libs.stripe.java)
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")}
