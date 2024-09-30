import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

val propertiesFile = rootProject.file("local.properties")
val properties = Properties().apply { load(FileInputStream(propertiesFile)) }
val clientId: String by properties

android {
    namespace = "com.example.taskslisthub"
    compileSdk = 34

    defaultConfig {
        buildConfigField("String", "CLIENT_ID", "\"$clientId\"")
        manifestPlaceholders["CLIENT_ID"] = clientId

        applicationId = "com.example.taskslisthub"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
       viewBinding = true
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
    //google tasks API
    implementation ("com.google.android.gms:play-services-auth:21.2.0")  // Google Sign-In
    implementation ("com.google.api-client:google-api-client-android:1.33.0") // Google API client
    implementation ("com.google.apis:google-api-services-tasks:v1-rev123-1.25.0")  // Google Tasks API

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation ("org.apache.commons:commons-text:1.10.0")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.room:room-ktx:2.4.3")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.activity:activity-ktx:1.9.1")
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    annotationProcessor("android.arch.persistence.room:compiler:1.1.1")
    implementation("androidx.room:room-runtime:2.6.1")

}