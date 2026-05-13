plugins {
    alias(libs.plugins.customplugin.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.domain"
}

dependencies {
    api(project(":core:data"))
    api(project(":core:model"))
    implementation(libs.javax.inject)
}
