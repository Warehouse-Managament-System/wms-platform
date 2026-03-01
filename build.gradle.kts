plugins {
    java
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spotless)
}

allprojects {
    group = "com.wms"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-parameters"))
        options.encoding = "UTF-8"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    dependencies {
        compileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)
        testCompileOnly(rootProject.libs.lombok)
        testAnnotationProcessor(rootProject.libs.lombok)

        implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
        annotationProcessor(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
    }
}

spotless {
    java {
        target("*/src/**/*.java")
        googleJavaFormat(
            libs.versions.google.java.format
                .get(),
        )
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint()
    }
}

val serviceProjects =
    setOf(
        "identity-service",
        "inventory-service",
        "reservation-service",
        "delivery-service",
        "platform-service",
        "api-gateway",
        "config-server",
        "eureka-server",
    )

configure(subprojects.filter { it.name in serviceProjects }) {
    apply(plugin = "org.springframework.boot")

    dependencies {
        implementation(platform("org.springframework.cloud:spring-cloud-dependencies:${rootProject.property("springCloudVersion")}"))
        implementation(rootProject.libs.spring.boot.starter.actuator)
        testImplementation(rootProject.libs.spring.boot.starter.test)
    }
}

val eurekaClientProjects =
    setOf(
        "identity-service",
        "inventory-service",
        "reservation-service",
        "delivery-service",
        "platform-service",
        "api-gateway",
        "config-server",
    )

configure(subprojects.filter { it.name in eurekaClientProjects }) {
    dependencies {
        implementation(rootProject.libs.spring.cloud.starter.netflix.eureka.client)
    }
}

val libraryProjects =
    setOf(
        "wms-common",
        "warehouse-module",
        "goods-module",
        "booking-module",
        "payment-module",
    )

configure(subprojects.filter { it.name in libraryProjects }) {
    apply(plugin = "java-library")
}

tasks.register("installGitHooks") {
    group = "setup"
    description = "Configures Git to use .githooks/ and makes hook scripts executable."

    doLast {
        val hooksDir = file(".githooks")
        if (!hooksDir.exists()) {
            logger.warn("WARNING: .githooks/ directory not found — skipping hook installation.")
            return@doLast
        }

        val result =
            ProcessBuilder("git", "config", "core.hooksPath", ".githooks")
                .directory(projectDir)
                .redirectErrorStream(true)
                .start()
                .waitFor()

        if (result != 0) {
            logger.warn("WARNING: Not a git repository — skipping hook installation.")
            return@doLast
        }

        hooksDir.listFiles()?.forEach { it.setExecutable(true) }
        logger.lifecycle("Git hooks installed from .githooks/")
    }
}

tasks.named("build") {
    dependsOn("installGitHooks")
}
