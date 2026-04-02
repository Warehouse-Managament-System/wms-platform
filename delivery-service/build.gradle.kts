dependencies {
    implementation(project(":wms-common"))
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.spring.cloud.starter.circuitbreaker.resilience4j)
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")}
