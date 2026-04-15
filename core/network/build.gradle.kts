plugins {
    alias(libs.plugins.customplugin.android.library)
    alias(libs.plugins.customplugin.hilt)
}

android {
    namespace = "com.example.network"
}


//dependencies {
//    api(project(":core:model"))
//}