dependencies {
    implementation(project(":wms-common"))
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.batch.core)
    implementation(libs.spring.boot.starter.quartz)
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")}
