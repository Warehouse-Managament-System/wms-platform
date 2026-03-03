dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":wms-common"))
    implementation(rootProject.libs.apache.poi)
    implementation(rootProject.libs.apache.poi.ooxml)
}
