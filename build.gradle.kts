plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    implementation("com.codeborne:selenide:7.9.3")
    implementation("org.testcontainers:testcontainers:1.21.0")
}

tasks.test {
    useJUnitPlatform()
}