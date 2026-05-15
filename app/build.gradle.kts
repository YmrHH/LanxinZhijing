import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.lanxin.zhijing"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.lanxin.zhijing"
        minSdk = 26
        targetSdk = 36
        versionCode = 9
        versionName = "0.1.10"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProps = Properties()
        val lpFile = rootProject.file("local.properties")
        if (lpFile.exists()) {
            lpFile.inputStream().use { localProps.load(it) }
        }
        val rawBase = localProps.getProperty("ai.backend.baseUrl", "").trim()
        val escapedBase = rawBase.replace("\\", "\\\\").replace("\"", "\\\"")
        buildConfigField("String", "AI_BACKEND_BASE_URL", "\"$escapedBase\"")
        val fbRaw = localProps.getProperty("ai.backend.fallbackToMock", "true").trim().lowercase()
        val fallbackToMock = when (fbRaw) {
            "false", "0", "no" -> "false"
            else -> "true"
        }
        buildConfigField("boolean", "AI_BACKEND_FALLBACK_TO_MOCK", fallbackToMock)

        val vivoId = localProps.getProperty("ai.vivo.appId", "2026140581").trim()
        val vivoKey = localProps.getProperty(
            "ai.vivo.appKey",
            "sk-xuanji-2026140581-WkdQZWpPZHFIZVIYbVFrTg=="
        ).trim()
        val vivoModel = localProps.getProperty("ai.vivo.model", "vivo-BlueLM-TB-Pro").trim()
        fun esc(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")
        buildConfigField("String", "VIVO_AIGC_APP_ID", "\"${esc(vivoId)}\"")
        buildConfigField("String", "VIVO_AIGC_APP_KEY", "\"${esc(vivoKey)}\"")
        buildConfigField("String", "VIVO_AIGC_MODEL", "\"${esc(vivoModel)}\"")
    }

    buildTypes {
        release {
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
    kotlin {
        jvmToolchain(11)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.material)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.mlkit.text.recognition.chinese)
    ksp(libs.androidx.room.compiler)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui)
}
