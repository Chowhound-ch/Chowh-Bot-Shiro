plugins {
    java
//    添加springboot
    id("org.springframework.boot") version "3.3.6"
    id("io.spring.dependency-management") version "1.1.4"

}

group = "per.chowhound.bot"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.mikuac:shiro:2.3.5")
    implementation("cn.hutool:hutool-all:5.7.10")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}