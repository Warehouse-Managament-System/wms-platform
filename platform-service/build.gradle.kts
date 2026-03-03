dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":wms-common"))
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.batch.core)
    implementation(libs.spring.boot.starter.quartz)
}
