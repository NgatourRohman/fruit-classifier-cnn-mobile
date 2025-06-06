import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.serialization")
    id ("com.google.gms.google-services")
}

android {
    namespace = "com.ngatour.fruitclassifier"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ngatour.fruitclassifier"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load properties from local.properties
        val localProperties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties") // Access from the root project
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { input ->
                localProperties.load(input)
            }
        } else {
            println("Warning: local.properties not found. Using default empty values for API keys.")
        }

        val supabaseApiKey = localProperties.getProperty("SUPABASE_API_KEY", "")
        val supabaseBaseUrl = localProperties.getProperty("SUPABASE_BASE_URL", "")

        buildConfigField("String", "SUPABASE_API_KEY", "\"$supabaseApiKey\"")
        buildConfigField("String", "SUPABASE_BASE_URL", "\"$supabaseBaseUrl\"")

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    //Auth
    implementation("io.ktor:ktor-client-okhttp:2.3.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.3")
    implementation("io.ktor:ktor-client-core:2.3.3")
    implementation("io.ktor:ktor-client-serialization:2.3.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation ("com.google.firebase:firebase-auth-ktx") // for Auth Supabase
    implementation ("com.google.firebase:firebase-messaging-ktx") // opsional: push notif

    //Material 3
    implementation("androidx.compose.material3:material3:1.3.2")

    implementation ("com.google.android.material:material:1.11.0")

    //Permission
    implementation ("com.google.accompanist:accompanist-permissions:0.31.5-beta")

    //Navigation
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.31.5-beta")

    //PyTorch
    implementation("org.pytorch:pytorch_android:1.13.1")
    implementation("org.pytorch:pytorch_android_torchvision:1.13.1")

    implementation ("androidx.navigation:navigation-compose:2.7.6")
    implementation ("androidx.core:core-splashscreen:1.0.1")

    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("androidx.activity:activity-ktx:1.8.1")
    implementation ("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.compose.material:material-icons-extended:1.5.1")

    //Local Database
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    testImplementation("androidx.room:room-testing:2.6.1")

    //ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    //Chart
    implementation("androidx.compose.foundation:foundation:1.5.4")

    //Camera
    implementation ("androidx.camera:camera-camera2:1.3.0")
    implementation ("androidx.camera:camera-lifecycle:1.3.0")
    implementation ("androidx.camera:camera-view:1.3.0")
    implementation ("androidx.camera:camera-core:1.3.0")

    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //Debug
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")

    //Default
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}