dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation(project(":wms-common"))
    api(rootProject.libs.apache.poi)
    api(rootProject.libs.apache.poi.ooxml)
}
