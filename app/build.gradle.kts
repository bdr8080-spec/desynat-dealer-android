plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.desynatdealer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.desynatdealer"
        minSdk = 24
        targetSdk = 34
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
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.places)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    //fragment navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //circle image view
    implementation(libs.circleimageview)

    //country code picker and phone validation
    implementation(libs.libphonenumber)
    implementation(libs.ccp)


    //dagger hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")


    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    // passing primitive as RequestBody: retrofit
    implementation(libs.converter.scalars)

    //gson for serialize and deserialize
    implementation(libs.gson)

    //okhttp network interceptor
    implementation(libs.logging.interceptor)

//    coil
    implementation("io.coil-kt:coil:2.7.0")

    //glide
    implementation(libs.glide)

//    for opt
    implementation(libs.ccp)

    implementation("io.github.chaosleung:pinview:1.4.4")


    //map
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.libraries.places:places:3.5.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    implementation ("com.myfatoorah:myfatoorah:3.0.4")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("io.getstream:photoview:1.0.2")

    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation ("com.google.firebase:firebase-database-ktx")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation("com.github.sparrow007:carouselrecyclerview:1.2.6")

    implementation("com.google.firebase:firebase-analytics")

    implementation ("com.google.firebase:firebase-messaging:25.0.1")
    implementation ("org.greenrobot:eventbus:3.3.1")
}