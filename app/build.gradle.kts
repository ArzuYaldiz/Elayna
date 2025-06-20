plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.mycloset"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mycloset"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose=true
        dataBinding{
            dataBinding=true
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

}
composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}

dependencies {

    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.appcheck)
    implementation(libs.firebase.storage.v2021)
    implementation (libs.firebase.auth)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.appcheck.debug.testing)
    implementation(libs.media3.common.ktx)
    implementation(libs.media3.exoplayer)
    implementation(libs.scenecore)
    implementation(libs.runtime.android)
    implementation(libs.androidx.camera.view)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Retrofit for network requests
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // OkHttp for HTTP operations
    implementation(libs.okhttp)
    // JWT decoding library
    implementation(libs.java.jwt)
    implementation(libs.imagepicker)
    implementation(libs.github.glide)
    annotationProcessor (libs.glide.compiler)
    implementation (libs.firebase.ui.storage)
    implementation(platform(libs.firebase.bom))
    /////,
    implementation (libs.core.ktx)
    //implementation(libs.sceneform.ux)
    //implementation(libs.sceneform)
    //implementation(libs.sceneview.v230)

    implementation (libs.androidx.core.ktx.v170)
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.androidx.activity.compose.v131)
    implementation (libs.androidx.ui.vcomposeuiversion)
    implementation (libs.androidx.ui.tooling.preview)
    implementation (libs.androidx.material)
    implementation (libs.androidx.junit.v115)
    implementation (libs.androidx.espresso.core.v351)
    implementation (libs.androidx.ui.test.junit4)
    implementation (libs.androidx.ui.tooling)
    implementation (libs.androidx.ui.test.manifest)
    implementation ("io.github.sceneview:arsceneview:0.10.0")



}
