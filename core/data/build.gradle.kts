plugins {
    alias(libs.plugins.customplugin.android.library)
    alias(libs.plugins.customplugin.android.library.compose)
    alias(libs.plugins.customplugin.hilt)
}

android {
    namespace = "com.example.data.repository"
}


dependencies {
    api(project(":core:model"))
    api(project(":core:datastore-proto"))
    implementation(project(":core:network"))
}