plugins {
    id("java")
    id("io.freefair.lombok") version "8.3"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    //resto
    implementation("io.r2dbc:r2dbc-h2:1.0.0.RELEASE")
    implementation("io.r2dbc:r2dbc-pool:1.0.0.RELEASE")

    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    testImplementation("org.mockito:mockito-core:5.5.0")

    implementation("io.projectreactor:reactor-core:3.5.10")
    implementation("org.mybatis:mybatis:3.5.13")
    implementation("com.h2database:h2:2.1.210")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    // JWT
    implementation("com.auth0:java-jwt:4.2.1")

    // BCcrypt
    implementation("org.mindrot:jbcrypt:0.4")
}

tasks.test {
    useJUnitPlatform()
}