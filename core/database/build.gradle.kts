plugins {
    alias(libs.plugins.customplugin.android.library)
    alias(libs.plugins.customplugin.android.library.compose)
    alias(libs.plugins.customplugin.android.room)
    alias(libs.plugins.customplugin.hilt)
}

android {
    namespace = "com.example.core.database"
}

dependencies {
    api(project(":core:model"))
}