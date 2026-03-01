rootProject.name = "wms-platform"

include(
    "wms-common",
    "identity-service",
    "inventory-service",
    "inventory-service:warehouse-module",
    "inventory-service:goods-module",
    "reservation-service",
    "reservation-service:booking-module",
    "reservation-service:payment-module",
    "delivery-service",
    "platform-service",
    "api-gateway",
    "config-server",
    "eureka-server",
)
