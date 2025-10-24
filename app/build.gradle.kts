plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-android")
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.desynatdealer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.desynatdealer"
        minSdk = 24
        targetSdk = 36
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // Logging interceptor
    implementation(libs.okhttp)
//    coil
    implementation("io.coil-kt:coil:2.7.0")

//    for opt
    implementation(libs.ccp)

    implementation("io.github.chaosleung:pinview:1.4.4")


    //map
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.libraries.places:places:3.5.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.tbuonomo:dotsindicator:5.1.0")
}