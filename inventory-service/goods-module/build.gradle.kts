dependencies {
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:${rootProject.libs.versions.spring.cloud.get()}"))
    implementation(project(":wms-common"))
    implementation(project(":inventory-service:warehouse-module"))
    implementation(rootProject.libs.apache.poi)
    implementation(rootProject.libs.apache.poi.ooxml)
    implementation(rootProject.libs.opencsv)
    implementation(rootProject.libs.spring.cloud.starter.openfeign)
}
