plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ticket"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ticket"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation(fileTree(mapOf("dir" to "C:\\Users\\hoang\\Downloads\\ZaloLib",
            "include" to listOf("*.aar", "*.jar"),
            "exclude" to listOf(""))))

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //thư viện load ảnh
    implementation ("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.2")

    implementation ("me.relex:circleindicator:2.1.6")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    //Thu vien api
    implementation("com.android.volley:volley:1.2.1")

   implementation("com.google.android.gms:play-services-auth:20.0.1")

    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("commons-codec:commons-codec:1.14")

    //thư viên vân tay
    implementation ("androidx.biometric:biometric:1.0.0-rc01")
}
