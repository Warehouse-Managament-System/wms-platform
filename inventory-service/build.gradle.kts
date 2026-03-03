dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":wms-common"))
    implementation(project(":inventory-service:warehouse-module"))
    implementation(project(":inventory-service:goods-module"))
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.resilience4j.spring.boot3)
}
