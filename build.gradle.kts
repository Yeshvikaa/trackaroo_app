// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.firebase.appdistribution") version "5.0.0" apply false
    // 👇 Add this line for Firebase Google Services
    id("com.google.gms.google-services") version "4.4.3" apply false
}