plugins {
    java
//    添加springboot
    id("org.springframework.boot") version "3.3.6"
    id("io.spring.dependency-management") version "1.1.4"

}

group = "per.chowhound.bot"
version = "0.0.1"




repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.mikuac:shiro:2.4.6")
    implementation("cn.hutool:hutool-all:5.7.10")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}