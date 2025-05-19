plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {

    namespace = "com.epson.moverio.moverioarworkflow"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.epson.moverio.moverioarworkflow"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += "arm64-v8a"
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "MAXST_LICENSE_KEY",
                "\"rqJmQjpPYisMwkdCbWy4ZkNTObv2+wH5jqkr3e4IG0c=\""
            )
        }
        debug {
            buildConfigField(
                "String",
                "MAXST_LICENSE_KEY",
                "\"rqJmQjpPYisMwkdCbWy4ZkNTObv2+wH5jqkr3e4IG0c=\""
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

    packagingOptions {
        exclude("com/epson/moverio/btcontrol/**")
    }
}



dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.fragment:fragment:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(files("libs\\BasicFunctionSDK_1.2.1.aar"))
    implementation(files("libs\\MaxstAR.aar"))
    implementation(files("libs\\VideoPlayer.jar"))
    //implementation(files("libs\\ControllerFunctionSDK_1.1.0.aar"))


    implementation("com.google.code.gson:gson:2.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}