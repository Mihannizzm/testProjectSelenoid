plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.codeborne:selenide:7.9.3")
    implementation(platform("org.junit:junit-bom:5.13.1"))
    implementation("org.junit.jupiter:junit-jupiter")
    runtimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}