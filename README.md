# FinanzBuddy - Projektbeschreibung

FinanzBuddy ist eine mobile Anwendung zur Verwaltung persönlicher Finanzen. Die Anwendung ermöglicht es Nutzern, Einnahmen und Ausgaben zu erfassen, Finanzstatistiken einzusehen und ihre finanzielle Situation zu analysieren.
Ziel der App ist es, eine benutzerfreundliche und effiziente Lösung für die persönliche Finanzplanung bereitzustellen.

Die App verwendet Firebase Authentication für eine sichere Anmeldung sowie eine RecyclerView für die effiziente Anzeige von Transaktionen.

# Mitwirkende

Das Projekt wurde von den folgenden Studierenden entwickelt:

Samed Sevinc - Matrikelnummer: 77211971390

Ekber Pala - Matrikelnummer: 77211964967



# Technische Architektur

### Frontend

- XML und Jetpack Navigation für das Benutzerinterface und die Navigation zwischen Ansichten.

- RecyclerView und Adapter für die Darstellung von Transaktionslisten.

- ViewModel und LiveData zur Verwaltung des UI-Status und zur Trennung von UI-Logik und Datenverarbeitung.

### Backend

- Java (Android) als primäre Programmiersprache zur Implementierung der Geschäftslogik.

- Firebase Authentication zur Verwaltung der Benutzerauthentifizierung.

- Firebase Realtime Database zur Speicherung und Verwaltung von Finanzdaten.

- Build- und Konfigurationsmanagement

- Gradle für die Verwaltung von Abhängigkeiten und den Build-Prozess.

- AndroidManifest.xml zur Definition der App-Berechtigungen und App-Struktur.


# Systemvoraussetzungen

- Android Studio in der aktuellen Version

- Gradle und die erforderliche Firebase-Konfiguration



# Installationsschritte

## Klonen des Repositorys:

1. git clone [https://github.com/dein-repo/finanzbuddy.git](https://github.com/DoctorBrain55/Finanzbuddy.git)

2. Öffnen des Projekts in Android Studio.

3. Start der App auf einem Emulator oder einem physischen Gerät.


## Funktionalitäten

- Benutzerregistrierung

- Verwaltung von Einnahmen und Ausgaben.

- Dashboard mit Finanzübersicht und Statistiken.

- Nutzung von RecyclerView zur Darstellung von Transaktionen.

- Speicherung der Finanzdaten in Firebase Realtime Database.

## Verbesserungspotenzial & Backlog


- Passwort zurücksetzen
  
- Neue Fragments und ViewModels für erweiterte Funktionalitäten.

- Anpassung der Datenmodelle in Transaction.java, um weitere Finanzdaten zu erfassen.

- Änderungen im UI-Design

- Implementierung eines Dunkelmodus für eine verbesserte Benutzererfahrung.

- Integration von Benachrichtigungen für wiederkehrende Zahlungen.

- Import/Export von Finanzberichten im CSV- oder PDF-Format.

 
# Kontakt

Für Fragen oder Beiträge zum Projekt kontaktieren Sie uns gerne über den E-Mail Weg:

E-Mail: s_pala22@stud.hwr-berlin.de

GitHub: [GitHub-Projektseite](https://github.com/dein-repo/finanzbuddy.git)

# Quellen
- [ https://github.com/DoctorBrain55/Finanzbuddy.git](https://michaelkipp.de/android/hilfe.html)
- https://michaelkipp.de/android/intro.html
- https://michaelkipp.de/android/erste-app.html
- https://www.youtube.com/playlist?list=PLYx38U7gxBf3pmsHVTUwRT_lGON6ZIBHi
- https://www.youtube.com/watch?v=N8p7IJiwSLA
- https://xdaforums.com/t/great-android-app-ui-design-examples.788071/
- ChatGPT als Unterstützung bei der Fehlersuche
