plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "smart.planner"
    compileSdk = 36

    defaultConfig {
        applicationId = "smart.planner"
        minSdk = 29
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    // ✅ thêm cái này để tránh lỗi packaging khi kéo lib (hay gặp)
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    /* ===================== ANDROID CORE ===================== */
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    /* ===================== COMPOSE ===================== */
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    /* ===================== UI VIEW ===================== */
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // ✅ update nhẹ
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2") // ✅ update nhẹ

    /* ===================== ROOM DATABASE ===================== */
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    /* ✅ bắt buộc nếu bạn có dùng ViewModelScope / lifecycle-viewmodel-ktx */
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    /* ===================== FIREBASE (BOM) ===================== */
    implementation(platform("com.google.firebase:firebase-bom:33.7.0")) // Cập nhật bản mới ổn định
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database") // Thêm dòng này (bản chính)
    implementation("androidx.compose.runtime:runtime-livedata")
    /* ===================== TEST ===================== */
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
