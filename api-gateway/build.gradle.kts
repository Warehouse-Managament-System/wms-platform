dependencies {
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.cloud.starter.gateway)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
    testImplementation(libs.spring.security.test)
}
