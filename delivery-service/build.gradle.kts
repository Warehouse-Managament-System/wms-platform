dependencies {
    implementation(project(":wms-common"))
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.resilience4j.spring.boot3)
}
