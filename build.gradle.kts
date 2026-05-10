import org.gradle.api.Project.DEFAULT_VERSION
import org.springframework.boot.gradle.tasks.bundling.BootJar

fun getGitHash(): String {
    return runCatching {
        providers.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText.get().trim()
    }.getOrElse { "init" }
}

plugins {
    java
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allprojects {
    val projectGroup: String by project
    group = projectGroup
    version = if (version == DEFAULT_VERSION) getGitHash() else version

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    dependencyManagement {
        imports {
            mavenBom("org.testcontainers:testcontainers-bom:${project.properties["testcontainersVersion"]}")
        }
    }

    dependencies {
        runtimeOnly("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

        implementation("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        testRuntimeOnly("com.mysql:mysql-connector-j")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.mockito:mockito-core:${project.properties["mockitoVersion"]}")
        testImplementation("org.instancio:instancio-junit:${project.properties["instancioJUnitVersion"]}")
        testImplementation("org.springframework.boot:spring-boot-testcontainers")
        testImplementation("org.testcontainers:testcontainers")
        testImplementation("org.testcontainers:junit-jupiter")
    }

    tasks.withType<Jar> { enabled = true }
    tasks.withType<BootJar> { enabled = false }

    configure(allprojects.filter { it.parent?.name == "apps" }) {
        tasks.withType<Jar> { enabled = false }
        tasks.withType<BootJar> { enabled = true }
    }

    tasks.test {
        maxParallelForks = 1
        useJUnitPlatform()
        systemProperty("user.timezone", "Asia/Seoul")
        systemProperty("spring.profiles.active", "test")
        jvmArgs("-Xshare:off")
    }

    tasks.withType<JacocoReport> {
        mustRunAfter("test")
        executionData(fileTree(layout.buildDirectory.asFile).include("jacoco/*.exec"))
        reports {
            xml.required = true
            csv.required = false
            html.required = false
        }
        afterEvaluate {
            classDirectories.setFrom(
                files(
                    classDirectories.files.map {
                        fileTree(it)
                    },
                ),
            )
        }
    }
}

project("apps") { tasks.configureEach { enabled = false } }
project("modules") { tasks.configureEach { enabled = false } }
