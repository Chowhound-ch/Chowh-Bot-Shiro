plugins {
    java
    application
    // 保持你的 Spring Boot 版本
    id("org.springframework.boot") version "4.0.6"
    // 移除了 io.spring.dependency-management 插件，改用下文 Gradle 原生的 platform 模式
}

group = "per.chowh.bot"
version = "0.0.1"



repositories {
    mavenCentral()
}

dependencies {
    // 使用 Gradle 原生 platform 引入 Spring Boot 和 MyBatis-Plus 的 BOM 管理版本
//    implementation(platform("com.baomidou:mybatis-plus-spring-boot4-starter:3.5.15"))
    implementation(platform("com.baomidou:mybatis-plus-bom:3.5.15"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    testAnnotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.mikuac:shiro:2.5.4")
    implementation("cn.hutool:hutool-all:5.7.10")
    // 此时无需手动指定版本，由上方的 BOM 统一控制
    implementation("com.baomidou:mybatis-plus-spring-boot4-starter")
    implementation("com.baomidou:mybatis-plus-jsqlparser")

    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")

    implementation("com.microsoft.playwright:playwright:1.57.0")
    implementation("javax.cache:cache-api:1.1.1")
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation("org.xerial:sqlite-jdbc:3.50.3.0")

    implementation("org.ehcache:ehcache:3.10.8") {
        capabilities {
            requireCapability("org.ehcache:ehcache-jakarta")
        }
    }

// Lombok 的标准引入姿势（无需指定版本号）
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // 如果你在写单元测试时也需要用到 Lombok 提供的注解，请加上这两行：
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

// 确保测试任务使用 JUnit Platform
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// 如果需要对 Java 编译进行额外配置（例如开启编译参数以支持保留方法参数名）
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    // Spring Boot 3.x/4.x 强烈建议开启 -parameters 参数，以便反射能正确获取控制器方法的参数名
    options.compilerArgs.add("-parameters")
}