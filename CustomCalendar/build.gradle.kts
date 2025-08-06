plugins {
    id("com.android.library")
}

android {
    namespace = "fr.jadeveloppement.customcalendar"
    compileSdk = 34

    defaultConfig {
    minSdk = 30

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
       release {
           isMinifyEnabled = false
           proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
       }
    }
}

dependencies {

}