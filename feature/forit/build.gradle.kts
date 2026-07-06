plugins {
    alias(libs.plugins.customplugin.android.feature)
    alias(libs.plugins.customplugin.android.library.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.forit"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    api(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}