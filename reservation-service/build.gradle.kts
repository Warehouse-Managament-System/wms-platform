dependencies {
    implementation(project(":wms-common"))
    implementation(project(":reservation-service:booking-module"))
    implementation(project(":reservation-service:payment-module"))
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.spring.cloud.starter.circuitbreaker.resilience4j)
    implementation(libs.spring.boot.starter.data.redis)
}
