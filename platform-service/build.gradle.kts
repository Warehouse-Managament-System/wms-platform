dependencies {
    implementation(project(":wms-common"))
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.batch.core)
    implementation(libs.spring.boot.starter.quartz)
}
