plugins {
    `java-library`
}

dependencies {
    api(libs.spring.boot.starter.data.jpa)
    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.validation)
    api(libs.spring.kafka)
    api(libs.postgresql)
    api(libs.liquibase.core)
    api(libs.jackson.databind)
    api(libs.jackson.datatype.jsr310)
    api(libs.micrometer.tracing.bridge.otel)
    api(libs.opentelemetry.exporter.otlp)
    api(libs.logback.logstash.encoder)
    testImplementation(libs.spring.boot.starter.test)
}
