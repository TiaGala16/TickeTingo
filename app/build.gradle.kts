//import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
//val localProperties = gradleLocalProperties(rootDir, providers)
//val cloudName = localProperties.getProperty("CLOUDINARY_CLOUD_NAME")
//val apiKey = localProperties.getProperty("CLOUDINARY_API_KEY")
import java.util.Properties
val localProps = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProps.load(localPropertiesFile.inputStream())
}
plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}
configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}
android {
    namespace = "com.example.ticketingo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ticketingo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val cloudname: String = localProps.getProperty("CLOUDINARY_CLOUD_NAME") as String? ?: ""
        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"${cloudname}\"")
        val apikey: String = localProps.getProperty("CLOUDINARY_API_KEY") as String? ?: ""
        buildConfigField("String", "CLOUDINARY_API_KEY", "\"${apikey}\"")
    }
        buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.recyclerview)
    implementation(libs.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation ("com.google.firebase:firebase-auth")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("com.google.firebase:firebase-firestore:26.0.2")
    implementation ("com.google.firebase:firebase-storage")
    implementation ("com.github.bumptech.glide:glide:5.0.5"){
        exclude(group = "com.intellij")
    }
    annotationProcessor ("com.github.bumptech.glide:compiler:5.0.5")
    implementation("com.cloudinary:cloudinary-android:3.1.2"){
        exclude(group = "com.intellij")
    }
    implementation("com.squareup.okhttp3:okhttp:5.2.1")
    implementation("org.jetbrains:annotations:23.0.0")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")

//    implementation ("com.cashfree.pg.java:cashfree_pg:5.0.8")
}