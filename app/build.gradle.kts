import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.tmap.ecd"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.tmap.ecd"
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

            val p = Properties()
            p.load(project.rootProject.file("local.properties").reader())

            val apiKey: String = p.getProperty("API_KEY")
            buildConfigField("String", "API_KEY", apiKey)

            val userKey: String = p.getProperty("USER_KEY")
            buildConfigField("String", "USER_KEY", userKey)

            val clientId: String = p.getProperty("CLIENT_ID")
            buildConfigField("String", "CLIENT_ID", clientId)
        }
        debug {
            val p = Properties()
            p.load(project.rootProject.file("local.properties").reader())

            val apiKey: String = p.getProperty("API_KEY")
            buildConfigField("String", "API_KEY", apiKey)

            val userKey: String = p.getProperty("USER_KEY")
            buildConfigField("String", "USER_KEY", userKey)

            val clientId: String = p.getProperty("CLIENT_ID")
            buildConfigField("String", "CLIENT_ID", clientId)
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

    implementation(libs.tmap.edc.sdk)
}