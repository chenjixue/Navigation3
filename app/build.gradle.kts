plugins {
    alias(libs.plugins.customplugin.android.application)
    alias(libs.plugins.customplugin.android.application.compose)
    alias(libs.plugins.customplugin.hilt)
    alias(libs.plugins.customplugin.android.application.flavors)
    alias(libs.plugins.kotlin.serialization)
}

android {
    signingConfigs {
        create("prod-release") {
            storeFile = file("C:\\androidProject\\Navigation3\\buildKeyProdRelease")
            storePassword = "123456"
            keyAlias = "prodRelease"
            keyPassword = "123456"
        }
    }
    namespace = "com.example.navigation3"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.navigation3"
        minSdk = 24
        targetSdk = 36
        versionCode = 101919
        versionName = "1.19.19"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("prod-release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

androidComponents {
    onVariants { variant ->
        val appName = if (variant.name == "prodRelease") "藕带记账" else "藕带记账-${variant.name}"
        variant.manifestPlaceholders.put("appName", appName)
    }
}

dependencies {

    ksp(libs.hilt.compiler)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)
    implementation(libs.androidx.navigation3.ui)
    implementation(project(":core:navigation"))
//    implementation(project(":feature:foryou"))
//    implementation(project(":feature:forhe"))
    implementation(project(":feature:forit"))
    implementation(project(":sync"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}