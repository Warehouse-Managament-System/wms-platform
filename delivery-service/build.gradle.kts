dependencies {
    implementation(project(":wms-common"))
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.spring.cloud.starter.circuitbreaker.resilience4j)
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
}
