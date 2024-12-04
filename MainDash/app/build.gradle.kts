plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.maindash"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.maindash"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.mapbox.navigation:android:2.17.1")
    implementation ("com.mapbox.search:mapbox-search-android-ui:1.0.0-rc.6")
    implementation ("com.mapbox.maps:android:10.0.0")
    implementation ("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")
    implementation ("com.github.yuriy-budiyev:code-scanner:2.3.0")

        implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

        // Required to use `ListenableFuture` from Guava Android for one-shot generation
        implementation("com.google.guava:guava:31.0.1-android")

        // Required to use `Publisher` from Reactive Streams for streaming operations
        implementation("org.reactivestreams:reactive-streams:1.0.4")

}