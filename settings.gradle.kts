// Plugin-Management: Definiert die Quellen für Plugins
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*") // Enthält Android-spezifische Plugins
                includeGroupByRegex("com\\.google.*") // Enthält Google-Dienste (z. B. Firebase)
                includeGroupByRegex("androidx.*") // Enthält AndroidX-Bibliotheken
            }
        }
        mavenCentral() // Fügt das Maven Central Repository hinzu
        gradlePluginPortal() // Enthält Gradle-Plugins
    }
}

// Verwaltung der Abhängigkeitsauflösung
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Verhindert das Überschreiben der Repositories durch Unterprojekte
    repositories {
        google() // Standard-Repository für Android-Bibliotheken
        mavenCentral() // Offizielles Maven-Repository für Java- und Android-Bibliotheken
        maven { url = uri("https://jitpack.io") } // Fügt JitPack hinzu, um Drittanbieter-Bibliotheken zu nutzen
    }
}

// Definiert den Namen des Root-Projekts
rootProject.name = "FinanzBuddy"

// Fügt das App-Modul zur Projektstruktur hinzu
include(":app")
