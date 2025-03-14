// Plugins für das Android-Projekt und Firebase-Dienste
plugins {
    alias(libs.plugins.android.application) // Android-Anwendungs-Plugin
    alias(libs.plugins.google.gms.google.services) // Google-Dienste (z. B. Firebase)
}

android {
    namespace = "com.example.finanzbuddy" // Definiert den Paketnamen der App
    compileSdk = 35 // Die verwendete Android SDK-Version

    defaultConfig {
        applicationId = "com.example.finanzbuddy" // Eindeutige App-ID
        minSdk = 24 // Mindest-Android-Version, die unterstützt wird
        targetSdk = 35 // Ziel-Android-Version
        versionCode = 1 // Interne Versionsnummer für Updates
        versionName = "1.0" // Sichtbare App-Version

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // Test-Runner für Instrumented Tests
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Deaktiviert Code-Minimierung für das Release-Build
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), // Standard-Proguard-Regeln
                "proguard-rules.pro" // Benutzerdefinierte Proguard-Regeln
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // Java 11 als Quellversion
        targetCompatibility = JavaVersion.VERSION_11 // Java 11 als Zielversion
    }

    buildFeatures {
        viewBinding = true // Aktiviert ViewBinding für einfachere UI-Verwaltung
    }
}

dependencies {
    // Android-Bibliotheken für UI und Navigation
    implementation(libs.appcompat) // Unterstützung für ältere Android-Versionen
    implementation(libs.material) // Material Design Komponenten
    implementation(libs.constraintlayout) // Flexibles Layout-Management
    implementation(libs.lifecycle.livedata.ktx) // LiveData für ViewModel
    implementation(libs.lifecycle.viewmodel.ktx) // ViewModel für UI-Logik
    implementation(libs.navigation.fragment) // Navigation zwischen Fragmenten
    implementation(libs.navigation.ui) // Navigation UI-Unterstützung

    // Firebase-Integration
    implementation(libs.firebase.auth) // Firebase-Authentifizierung
    implementation(libs.firebase.database) // Firebase-Realtime-Datenbank

    // Zusätzliche Bibliotheken
    implementation(libs.activity) // Unterstützung für Activities
    implementation(libs.legacy.support.v4) // Legacy-Support für ältere APIs
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") // Bibliothek für Diagramme
    implementation("de.hdodenhof:circleimageview:3.1.0") // Runde Profilbilder

    // Testabhängigkeiten
    testImplementation(libs.junit) // JUnit-Tests für lokale Tests
    androidTestImplementation(libs.ext.junit) // Erweiterte JUnit-Tests für Android
    androidTestImplementation(libs.espresso.core) // UI-Tests mit Espresso
}
