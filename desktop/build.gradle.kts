import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.memoamber"
version = "1.4.0"

kotlin { jvmToolchain(17) }

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")
}

// 创建 fat jar（包含所有依赖）
tasks.register<Jar>("fatJar") {
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest { attributes["Main-Class"] = "com.memoamber.desktop.MainKt" }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get())
}

compose.desktop {
    application {
        mainClass = "com.memoamber.desktop.MainKt"
    }
}
