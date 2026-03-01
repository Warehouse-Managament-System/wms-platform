dependencies {
    implementation(project(":wms-common"))
    implementation(project(":inventory-service:warehouse-module"))
    implementation(rootProject.libs.apache.poi)
    implementation(rootProject.libs.apache.poi.ooxml)
}
