plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.seraliziation)
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("/Users/mykalcuin/Documents/LiftingDice/liftingDiceAndroidKeyStore")
            storePassword = "LiftingDiceKeyPass"
            keyAlias = "LiftingDiceKey"
            keyPassword = "LiftingDiceKeyPass"
        }
    }
    namespace = "com.cuinsolutions.liftingdice.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.cuinsolutions.liftingdice.android"
        minSdk = 28
        targetSdk = 35
        versionCode = 2
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "muscle_groups_banner_ad_unit_id", "ca-app-pub-6601965971959563/3793773338")
            resValue("string", "equipment_settings_banner_ad_unit_id", "ca-app-pub-6601965971959563/2480691668")
            resValue("string", "exercises_banner_ad_unit_id", "ca-app-pub-6601965971959563/2436459632")
            resValue("string", "exercises_reroll_rewarded_ad_unit_id", "ca-app-pub-6601965971959563/4139218264")
        }
        debug {
            isMinifyEnabled = false
            resValue("string", "muscle_groups_banner_ad_unit_id", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "equipment_settings_banner_ad_unit_id", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "exercises_banner_ad_unit_id", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "exercises_reroll_rewarded_ad_unit_id", "ca-app-pub-3940256099942544/5224354917")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    ksp {
        arg("KOIN_USE_COMPOSE_VIEWMODEL","true")
    }
}

dependencies {
    implementation(projects.liftingDiceShared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.play.services.ads)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.databse)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.compose.navigation)
    implementation(platform(libs.koin.annotations.bom))
    implementation(libs.koin.annotations)
    ksp(libs.koin.compiler)
    implementation(libs.dataStore)
    implementation(libs.protobuf.java)
    implementation(libs.protobuf.kotlin)
    implementation(libs.json.serialization)
    implementation(libs.compose.navigation)
    implementation(libs.splashScreen)
    androidTestImplementation(libs.compose.navigation.testing)
    debugImplementation(libs.compose.ui.tooling)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.29.3"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}