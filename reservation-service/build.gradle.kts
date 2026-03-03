dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":wms-common"))
    implementation(project(":reservation-service:booking-module"))
    implementation(project(":reservation-service:payment-module"))
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.resilience4j.spring.boot3)
    implementation(libs.spring.boot.starter.data.redis)
}
