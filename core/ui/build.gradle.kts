plugins {
    alias(libs.plugins.customplugin.android.library)
    alias(libs.plugins.customplugin.android.library.compose)
}

android {
    namespace = "com.example.core.ui"
}