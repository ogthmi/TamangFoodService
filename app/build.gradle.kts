import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

val stripePublishableKey: String = run {
    val propsFile = rootProject.file("local.properties")
    if (!propsFile.exists()) return@run ""
    val props = Properties()
    propsFile.inputStream().use { props.load(it) }
    props.getProperty("stripe.publishableKey").orEmpty()
}

val baseUrl: String = run {
    val propsFile = rootProject.file("local.properties")
    if (!propsFile.exists()) return@run ""

    val props = Properties()
    propsFile.inputStream().use { props.load(it) }

    props.getProperty("baseUrl").orEmpty()
}

android {
    namespace = "com.example.tamangfood"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.tamangfood"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Keep the key out of git (read from local.properties which is ignored).
        buildConfigField(
            "String",
            "STRIPE_PUBLISHABLE_KEY",
            "\"$stripePublishableKey\""
        )

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
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
        buildConfig = true
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
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.paging:paging-runtime-ktx:3.2.1")

    implementation("androidx.navigation:navigation-fragment:2.9.5")
    implementation("androidx.navigation:navigation-ui:2.9.5")

    implementation("com.google.dagger:hilt-android:2.57.1")
    kapt("com.google.dagger:hilt-android-compiler:2.57.1")

    // Retrofit for HTTP API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.biometric:biometric:1.2.0-alpha05")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.github.yalantis:ucrop:2.2.8")
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    // Google Play Services Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Stripe
    implementation("com.stripe:stripe-android:20.48.0")

    // Background tasks for delayed order notifications
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // Fetch Image by URL
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    kapt ("com.github.bumptech.glide:compiler:4.15.1")

}