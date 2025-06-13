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
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.codeborne:selenide:7.9.3")
    testImplementation("org.testcontainers:testcontainers:1.21.0")
}

tasks.test {
    useJUnitPlatform()
}