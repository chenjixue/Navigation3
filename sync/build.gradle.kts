plugins {
    alias(libs.plugins.customplugin.android.library)
    alias(libs.plugins.customplugin.hilt)
}

android {
    namespace = "com.example.sync"
}

dependencies {
    ksp(libs.hilt.ext.compiler)

    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
    implementation(project(":core:network"))
    implementation(project(":core:data"))
}
