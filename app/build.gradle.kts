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
<<<<<<< HEAD
    // Core & Compose cơ bản
=======

    /* ===================== ANDROID CORE ===================== */
>>>>>>> origin/Phát
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    /* ===================== COMPOSE ===================== */
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
<<<<<<< HEAD
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // --- BỔ SUNG QUAN TRỌNG CHO COMPOSE & VIEWMODEL ---
    // Hỗ trợ hàm viewModel() trong @Composable
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    // Hỗ trợ observeAsState() để dùng LiveData trong Compose
    implementation("androidx.compose.runtime:runtime-livedata:1.7.6")
    // Hỗ trợ các thuộc tính delegated (by tasks)
    implementation("androidx.compose.runtime:runtime:1.7.6")

    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")

    // Retrofit & Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Testing
=======
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
>>>>>>> origin/Phát
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
<<<<<<< HEAD
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
=======
}
>>>>>>> origin/Phát
