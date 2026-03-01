dependencies {
    implementation(project(":wms-common"))
    implementation(libs.spring.cloud.starter.gateway)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
}
