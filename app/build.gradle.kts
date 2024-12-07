import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.su.communityconnect"
    compileSdk = 35
 composeOptions{

         kotlinCompilerExtensionVersion = "1.5.3" // Use the Compose Compiler compatible with Kotlin 1.9.0
 }
    defaultConfig {
        applicationId = "com.su.communityconnect"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Get the API key from local.properties
        val localProperties = Properties().apply {
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localPropertiesFile.inputStream().use { load(it) }
            }
        }
        buildConfigField("String", "PLACES_API_KEY", "\"${localProperties["PLACES_API_KEY"]}\"")
    }
    buildFeatures {
        compose = true
        buildConfig = true
        kotlinOptions {
            jvmTarget = "1.8"
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material)
    implementation (libs.androidx.ui.tooling)
    implementation (libs.androidx.material.icons.extended)
    implementation (libs.androidx.activity.compose)
    implementation (libs.androidx.ui.tooling.preview)
    implementation (libs.androidx.lifecycle.runtime.ktx)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)

    // Firebase Realtime Database
    implementation(libs.firebase.database)

    // Firebase Storage
    implementation(libs.firebase.storage)

    // Google Places
    implementation(libs.places)

    //Datetime picker (wheel)
    implementation(libs.kmp.date.time.picker)

    implementation(libs.coil.compose)

    implementation(libs.core)

}