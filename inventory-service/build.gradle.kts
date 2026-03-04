dependencies {
    implementation(project(":wms-common"))
    implementation(project(":inventory-service:warehouse-module"))
    implementation(project(":inventory-service:goods-module"))
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.spring.cloud.starter.circuitbreaker.resilience4j)
}
