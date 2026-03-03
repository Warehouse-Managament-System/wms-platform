dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":wms-common"))
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.resilience4j.spring.boot3)
}
