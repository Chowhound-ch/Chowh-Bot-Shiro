plugins {
    java
//    添加springboot
    application
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
    implementation("com.baomidou:mybatis-plus-spring-boot3-starter:3.5.12")
    implementation("org.springframework.boot:spring-boot-starter-aop")
//    mysql驱动
    implementation("com.mysql:mysql-connector-j")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}

// 将依赖包复制到lib目录
tasks.register<Copy>("copyJar") {
    // 清除现有的lib目录
    delete(layout.buildDirectory.dir("libs/lib"))
    from(configurations.runtimeClasspath) // 统一使用 runtimeClasspath
    into(layout.buildDirectory.dir("libs/lib"))

    // 添加编译类路径依赖（如果需要）
    from(configurations.compileClasspath)
    into(layout.buildDirectory.dir("libs/lib"))
}

// 拷贝配置文件
tasks.register<Copy>("copyConfigFile") {
    // 清除现有的配置目录
    delete(layout.buildDirectory.dir("libs/config"))
    from("src/main/resources")
    into(layout.buildDirectory.dir("libs/config"))
}

// 配置bootJar进行打包
tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    // jar包名称
    archiveBaseName.set("Chowh-Bot-Shiro")
    // jar包版本号
    archiveVersion.set("0.0.1")
//    // 排除所有的jar
    setExcludes(listOf("*.jar"))

    // 添加任务依赖
    dependsOn("copyJar")
    dependsOn("copyConfigFile")

    // 指定依赖包路径
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Implementation-Title" to "Halo Application",
            "Implementation-Version" to archiveVersion.get(),
            "Class-Path" to
                    configurations.runtimeClasspath.get().files.joinToString(" ") { "lib/${it.name}" } + " " +
                    configurations.compileClasspath.get().files.joinToString(" ") { "lib/${it.name}" }
        )
    }
}
