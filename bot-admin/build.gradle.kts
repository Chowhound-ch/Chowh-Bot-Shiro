plugins {
    id("java")
}

group = "per.chowh.bot"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("per.chowh.bot:Chowh-Bot-Shiro:0.0.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}