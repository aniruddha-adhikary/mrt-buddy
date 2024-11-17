import com.github.jk1.license.render.InventoryMarkdownReportRenderer
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.jaredsburrowsLicense)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            //  implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutine)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.napier)
            implementation(libs.multiplatform.settings)
            implementation(libs.navigation.compose)

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            api(libs.compose.webview.multiplatform)
            implementation(libs.multiplatform.markdown.renderer)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }
    }
}

android {
    namespace = "net.adhikary.mrtbuddy"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "net.adhikary.mrtbuddy"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 20
        versionName = "0.0.20"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")

            // Optional, specify signing versions used
            enableV1Signing = true
            enableV2Signing = true
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}


dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    debugImplementation(compose.uiTooling)
}

room {
    schemaDirectory("$projectDir/schemas")
}

licenseReport {
    unionParentPomLicenses = false
    renderers = arrayOf(
        InventoryMarkdownReportRenderer(
            "open-source-licenses.md",
            "Open Source Libraries"
        )
    )
}

tasks.register("processLicenseReport") {
    dependsOn("generateLicenseReport")
    
    doLast {
        val reportPath = file("build/reports/dependency-license/open-source-licenses.md")
        val processedPath = file("build/reports/dependency-license/processed-open-source-licenses.md")
        
        if (!reportPath.exists()) {
            throw GradleException("License report not found at $reportPath")
        }
        
        val content = reportPath.readText()
        val processedContent = content.replace(
            Regex("_\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} [A-Z]+_"),
            ""
        )
        processedPath.writeText(processedContent)
    }
}

tasks.register("copyLicenseReportToAssets") {
    dependsOn("processLicenseReport")
    
    doLast {
        val processedPath = file("build/reports/dependency-license/processed-open-source-licenses.md")
        val commonAssetsPath = file("src/commonMain/composeResources/files")

        if (!processedPath.exists()) {
            throw GradleException("Processed license report not found at $processedPath")
        }

        copy {
            from(processedPath)
            into(commonAssetsPath)
            rename { "open-source-licenses.md" }
        }
    }
}

tasks.named("preBuild").configure {
    dependsOn("copyLicenseReportToAssets")
}
