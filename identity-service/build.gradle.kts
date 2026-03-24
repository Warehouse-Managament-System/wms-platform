dependencies {
    implementation(project(":wms-common"))
    implementation(libs.spring.boot.starter.security)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
    testImplementation(libs.spring.security.test)
    implementation("org.springframework.boot:spring-boot-starter-liquibase")

}
