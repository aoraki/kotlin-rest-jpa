import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("plugin.jpa") version "1.4.32"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
}

group = "jk.codespace"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Required for Kotlin dev
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// DB Related dependencies
	runtimeOnly("com.h2database:h2")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// Logging
	implementation("io.github.microutils:kotlin-logging:2.1.21")
	implementation("io.mockk:mockk:1.10.2")
	implementation("org.assertj:assertj-core:3.18.1")

	// Testing dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
