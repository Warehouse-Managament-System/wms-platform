dependencies {
    implementation(project(":wms-common"))
    implementation(project(":reservation-service:booking-module"))
    implementation(rootProject.libs.stripe.java)
}
