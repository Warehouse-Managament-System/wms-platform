dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":wms-common"))
    implementation(project(":reservation-service:booking-module"))
    implementation(rootProject.libs.stripe.java)
}
