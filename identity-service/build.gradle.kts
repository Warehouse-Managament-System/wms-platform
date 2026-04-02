dependencies {
    implementation(project(":wms-common"))
    implementation(libs.spring.boot.starter.security)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
    testImplementation(libs.spring.security.test)
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")}
