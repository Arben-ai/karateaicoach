# KarateAI Coach

![CI](https://github.com/Arben-ai/karateaicoach/actions/workflows/ci.yml/badge.svg)
![Coverage](https://github.com/Arben-ai/karateaicoach/blob/main/.github/badges/jacoco.svg)
![Branches](https://github.com/Arben-ai/karateaicoach/blob/main/.github/badges/branches.svg)

KarateAI Coach ist eine webbasierte Anwendung zur KI-gestützten Erstellung und Verwaltung individueller Trainingspläne für Karateka.

Die Applikation generiert auf Basis eines vom Coach definierten Trainingsfokus personalisierte Trainingspläne mittels KI und ermöglicht die strukturierte Verwaltung des Trainingsfortschritts.

## Rollen

- **Sportler**
  - Kann eigenen Trainingsfokus (Feedback vom Coach) einsehen
  - Kann Trainingsplan aus Trainingsfokus generieren lassen
  - Kann eigene Trainingspläne einsehen
  - Kann mit dem KI-Assistenten chatten

- **Coach (Admin)**
  - Kann Sportler verwalten (erstellen, bearbeiten, löschen)
  - Kann Trainingsfokusse für Sportler erstellen und verwalten
  - Kann Trainingspläne verwalten und Status ändern (Aktiv → Abgeschlossen → Archiviert)

# Inhaltsverzeichnis
- [Einleitung](#einleitung)
    - [Explore-Board](#explore-board)
    - [Create-Board](#create-board)
    - [Evaluate-Board](#evaluate-board)
    - [Diskussion Feedback Pitch](#diskussion-feedback-pitch)
- [Anforderungen](#anforderungen)
    - [Use-Case Diagramm](#use-case-diagramm)
    - [Use-Case Beschreibung](#use-case-beschreibung)
    - [Fachliches Datenmodell](#fachliches-datenmodell)
    - [UI-Mockup](#ui-mockup)
- [Implementation](#implementation)
    - [Frontend](#frontend)
    - [KI-Funktionen](#ki-funktionen)
	- [Drittsysteme](#drittsysteme)
- [Fazit](#fazit)
    - [Stand der Implementation](#stand-der-implementation)
    
# Einleitung

## Explore-Board
### TRENDS & TECHNOLOGIE
- **Megatrends**
  - Digitalisierung des Sports
  - Datafication / Quantified Self
  - Künstliche Intelligenz im Alltag
  - Individualisierung von Services
  - Plattformökonomie

- **Soziokulturelle Trends**
  - Leistungsoptimierung im Sport
  - Selbstoptimierung & Performance-Mindset
  - Hybrid Coaching (offline + digital)
  - Transparenz durch Daten

- **Konsum- & Zeitgeisttrends**
  - Fitness-Apps & Wearables
  - Mobile-first Nutzung
  - Gamification im Training
  - Abo-Modelle im digitalen Bereich

- **Technologien**
  - Large Language Models zur Trainingsplanung
  - Cloud Computing
  - MongoDB (NoSQL-Datenbank)
  - Rollenbasierte Authentifizierung
  - Web-App (SaaS-Modell)

### POTENTIELLE PARTNER & WETTBEWERB
- **Potenzielle Partner**
  - Nationale Karate-Verbände (z. B. Swiss Karate Federation)
  - Karate-Vereine und Leistungszentren
  - Sportwissenschaftliche Institute
  - Wearable-Hersteller (z. B. Garmin, Polar)
  - Turnierveranstalter
  - Videoanalyse-Plattformen

- **Wettbewerb**
  - Allgemeine Trainingsplan-Apps (z. B. Freeletics)
  - Fitness-Tracking-Apps
  - Individuelle Excel- oder Notion-Lösungen
  - Klassische PDF-Trainingspläne
  - Trainerinterne, nicht-digitale Planungssysteme

### FAKTEN
- Leistungs-Karate ist stark wettkampforientiert
- Trainingsplanung erfolgt häufig manuell oder erfahrungsbasiert
- Trainer betreuen mehrere Sportler gleichzeitig
- Trainingsschwerpunkte ändern sich je nach Wettkampfphase (Periodisierung)
- Leistungsdaten sind sensibel und unterliegen Datenschutzbestimmungen
- KI wird im Sport bisher hauptsächlich im Profibereich eingesetzt
- Es existieren kaum spezialisierte digitale Lösungen für Kampfsportarten
- Fortschrittsmessung erfolgt oft subjektiv statt datenbasiert

### USER
- **Primäre Nutzer**
  - Leistungsorientierte Karateka
  - Nachwuchssportler mit Wettkampffokus
  - Nationalkader-Athleten

- **Sekundäre Nutzer**
  - Karate-Trainer
  - Vereinstrainer
  - Leistungskoordinatoren

- **Charakteristika**
  - Zielorientiert
  - Hohe Trainingsfrequenz
  - Wettkampforientiert
  - Diszipliniert und strukturiert
  - Offen für Leistungsverbesserung

### POTENZIALFELDER
- Leistungssteigerung
- Strukturierte Trainingsplanung
- Fortschrittsmessung und Analyse
- Wettkampfvorbereitung
- Individualisierung von Trainingsplänen
- Trainer-Sportler-Kommunikation
- Verletzungsprävention
- Langfristige Leistungsentwicklung

### ERKENNTNISSE
- **Allgemeine Erkenntnisse**
  - Trainingsplanung ist häufig erfahrungsbasiert statt datenbasiert
  - Digitale Tools im Kampfsport sind wenig spezialisiert
  - Viele Trainer nutzen eigene, nicht standardisierte Systeme

- **Funktionale Erkenntnisse**
  - Sportler wünschen sich klare und strukturierte Trainingsvorgaben
  - Trainer benötigen Überblick über mehrere Athleten gleichzeitig
  - Trainingsschwerpunkte ändern sich je nach Wettkampfphase

- **Emotionale Erkenntnisse**
  - Unsicherheit vor wichtigen Wettkämpfen
  - Frustration bei stagnierender Leistung
  - Motivation durch sichtbaren Fortschritt

- **Soziale Erkenntnisse**
  - Trainer sind zentrale Autoritätspersonen
  - Regelmäßiges Feedback stärkt Vertrauen und Motivation
  - Leistungsbewertung erfolgt häufig im Vergleich zu anderen Athleten

### BEDÜRFNISSE
- Klare und strukturierte Trainingsplanung
- Individuell angepasste Trainingspläne
- Transparenz über Leistungsfortschritt
- Sicherheit in der Wettkampfvorbereitung
- Effiziente Kommunikation zwischen Trainer und Sportler
- Objektive und nachvollziehbare Leistungsanalyse
- Motivation durch messbare Entwicklung

### TOUCHPOINTS
- Smartphone während oder nach dem Training
- Laptop oder Tablet zur Trainingsplanung
- Trainingshalle / Dojo
- Wettkämpfe und Turnierplattformen
- Vereinskommunikation (z. B. WhatsApp, E-Mail)
- Videoanalyse-Tools
- Wearables und Fitness-Tracker
- Persönliche Trainer-Gespräche

### WIE KÖNNEN WIR?
Wie können wir leistungsorientierte Karateka und ihre Trainer dabei unterstützen, Trainingsplanung und Leistungsentwicklung datenbasiert, strukturiert und individuell zu gestalten, um Wettkampfleistungen gezielt zu verbessern?

## Create-Board
### IDEEN-BESCHREIBUNG
KarateAI Coach ist eine KI-gestützte Webanwendung, die auf Basis eines vom Trainer definierten Trainingsfokus automatisch strukturierte Trainingspläne für leistungsorientierte Karateka generiert.  
Die Plattform verbindet Trainerexpertise mit datenbasierter Trainingsplanung und ermöglicht eine transparente Fortschrittsanalyse.

### ADRESSIERTE NUTZER
- Leistungsorientierte Karateka mit Wettkampffokus
- Trainer im Leistungs- und Nachwuchsbereich

### ADRESSIERTE BEDÜRFNISSE
- Strukturierte und individuelle Trainingsplanung
- Transparenz über Leistungsfortschritt
- Sicherheit in der Wettkampfvorbereitung
- Effiziente Trainer-Sportler-Kommunikation

### PROBLEME
- Trainingsplanung erfolgt oft unsystematisch oder rein erfahrungsbasiert
- Fortschritte sind schwer messbar und wenig dokumentiert
- Trainer verlieren bei mehreren Athleten schnell den Überblick

### IDEENPOTENZIAL
- **Mehrwert (User Value):** 7/10
- **Übertragbarkeit (Scalability):** 6/10
- **Machbarkeit (Feasibility):** 7/10

### DAS WOW
Automatische Generierung eines strukturierten, wettkampforientierten Trainingsplans auf Basis eines klar definierten Trainingsfokus – inklusive Fortschrittsverfolgung und Statusverwaltung.

### HIGH-LEVEL-KONZEPT
„Notion für Karate-Coaches – kombiniert mit KI-gestützter Trainingsplanung."

### WERTVERSPRECHEN
KarateAI Coach ermöglicht leistungsorientierten Karateka und ihren Trainern eine strukturierte, datenbasierte und individuell zugeschnittene Trainingsplanung, um Wettkampfleistungen gezielt und messbar zu verbessern.

## Evaluate-Board
### KANÄLE
- Social Media (Instagram, TikTok – sportbezogener Content)
- Direktansprache über Karate-Vereine und Leistungszentren
- Kooperationen mit Verbänden
- Präsentationen bei Trainer-Weiterbildungen
- Empfehlungsmarketing unter Athleten
- Website mit Demo-Zugang
- E-Mail-Newsletter für Trainer

### UNFAIRER VORTEIL
- Kombination aus Trainer-definiertem Trainingsfokus und KI-Generierung
- Spezialisierung auf Wettkampf-Karate (starker Nischenfokus)
- Integration von Trainingsstatus und Fortschrittsanalyse
- Fachliches Know-how im Leistungs-Karate
- Frühe Partnerschaften mit Verbänden oder Leistungszentren

### KPI
- Anzahl registrierter Sportler
- Anzahl aktiver Trainer
- Anzahl generierter Trainingspläne pro Monat
- Nutzeraktivität (wöchentliche Logins)
- Retention-Rate nach 3 Monaten
- Anzahl Empfehlungen durch bestehende Nutzer
- Conversion-Rate von Testversion zu Bezahlmodell

### EINNAHMEQUELLEN
- Monatliches Abonnement für Trainer (SaaS-Modell)
- Vereinslizenz für Leistungszentren
- Freemium-Modell für Sportler mit kostenpflichtigen Premium-Funktionen
- Kooperationen mit Verbänden
- Sponsoring durch Sportmarken

## Diskussion Feedback Pitch

**Positives Feedback:**
- Die Idee ist klar und der Anwendungsfall direkt nachvollziehbar — man versteht sofort, welches Problem gelöst wird
- Die Kombination aus Trainer-definiertem Trainingsfokus und KI-Generierung wurde als elegante Lösung gelobt

**Kritisches Feedback / Fragen:**
- Der Workflow zwischen Trainer und Sportler war nicht ganz klar — muss der Trainer zuerst aktiv einen Fokus setzen, bevor der Sportler einen Plan generieren kann?
- Die Zielgruppe (Leistungskarateka) ist sehr spezifisch — ob das Modell auf andere Kampfsportarten erweiterbar wäre, wurde hinterfragt
- Eine mobile App wäre für den Einsatz direkt in der Trainingshalle sinnvoller als eine Web-App

# Anforderungen

## Use-Case Diagramm
![Use-Case Diagramm](doc/uc-diagram.drawio.svg)

## Use-Case Beschreibung

### Use Case Description

**ID:** 1  
**Title:** Registrieren  

**Pre-Conditions:**  
- Keine.  

**Actors:**  
Sportler  

**Sequence:**  
1. Sportler ruft die Registrierungsseite auf.  
2. Sportler gibt Name, E-Mail und Passwort ein.  
3. System erstellt ein Konto via Auth0.  
4. Sportler wird zur Startseite weitergeleitet.  

**Data Definitions:**  
Name: Typ Text  
E-Mail: Typ Text  
Passwort: Typ Passwort  

**Exception:**  
- E-Mail bereits vorhanden → Fehlermeldung wird angezeigt.  
- Ungültige E-Mail-Adresse → Registrierung nicht möglich.  

---

### Use Case Description

**ID:** 2  
**Title:** Login  

**Pre-Conditions:**  
- Sportler oder Coach besitzt ein registriertes Konto.  

**Actors:**  
Sportler, Coach  

**Sequence:**  
1. Benutzer ruft die Login-Seite auf.  
2. Benutzer gibt E-Mail und Passwort ein.  
3. System prüft die Zugangsdaten via Auth0.  
4. Benutzer wird zur Startseite weitergeleitet.  

**Data Definitions:**  
E-Mail: Typ Text  
Passwort: Typ Passwort  

**Exception:**  
- Falsche E-Mail oder Passwort → Fehlermeldung wird angezeigt, Login-Seite wird erneut angezeigt.  
- Kein Konto vorhanden → Anmeldung schlägt fehl.  

---

### Use Case Description

**ID:** 3  
**Title:** Profil verwalten  

**Pre-Conditions:**  
- Sportler ist erfolgreich eingeloggt (UC 2 Login).  

**Actors:**  
Sportler  

**Sequence:**  
1. Sportler navigiert zur Account-Seite.  
2. Sportler aktualisiert Gürtelgrad und/oder Gewicht.  
3. Sportler speichert die Änderungen.  
4. System aktualisiert das Profil und zeigt eine Bestätigung an.  

**Data Definitions:**  
Gürtelgrad: Typ Text  
Gewicht: Typ Zahl  

**Exception:**  
- Sportler-Profil nicht gefunden → Fehlermeldung wird angezeigt.  

---

### Use Case Description

**ID:** 4  
**Title:** Trainingsplan generieren  

**Pre-Conditions:**  
- Sportler ist erfolgreich eingeloggt (UC 2 Login).  
- Es existiert ein aktiver Trainingsfokus für den Sportler.  

**Actors:**  
Sportler  

**Sequence:**  
1. Sportler navigiert zur Seite „Meine Trainingspläne”.  
2. Sportler wählt einen aktiven Trainingsfokus aus.  
3. Sportler klickt auf „Trainingsplan generieren”.  
4. System übergibt den Trainingsfokus an die KI (UC 5).  
5. Trainingsplan wird gespeichert und angezeigt.  

**Data Definitions:**  
Trainingsfokus: Typ Text  
Titel: Typ Text  
Dauer: Typ Zahl  
Status: Typ Text  

**Exception:**  
- Kein aktiver Trainingsfokus vorhanden → Generierung nicht möglich.  
- Fehler bei KI-Generierung → Fehlermeldung wird angezeigt.  

---

### Use Case Description

**ID:** 5  
**Title:** KI-Trainingsplan generieren lassen  

**Pre-Conditions:**  
- UC 4 Trainingsplan generieren wurde ausgelöst.  
- Trainingsfokus ist vollständig befüllt.  

**Actors:**  
Sportler  

**Sequence:**  
1. System sendet Trainingsfokus, Gürtelgrad und Gewicht an das KI-Modell.  
2. KI generiert einen detaillierten, strukturierten Trainingsplan.  
3. System speichert den generierten Plan in der Datenbank.  
4. Trainingsplan wird dem Sportler angezeigt.  

**Data Definitions:**  
Schwerpunkt: Typ Text  
Kategorie: Typ Text  
Dauer in Wochen: Typ Zahl  
Einheiten pro Woche: Typ Zahl  
Minuten pro Einheit: Typ Zahl  
Inhalt (KI-generiert): Typ Text  

**Exception:**  
- KI-Modell nicht erreichbar → Fehlermeldung wird angezeigt.  
- Bereits ein Plan für diesen Fokus vorhanden → Bestehender Plan wird zurückgegeben.  

---

### Use Case Description

**ID:** 6  
**Title:** Trainingsfokus einsehen  

**Pre-Conditions:**  
- Sportler ist erfolgreich eingeloggt (UC 2 Login).  

**Actors:**  
Sportler  

**Sequence:**  
1. Sportler navigiert zur Seite „Mein Feedback”.  
2. System lädt alle Trainingsfokusse des Sportlers.  
3. Sportler wählt einen Trainingsfokus aus und sieht die Details.  
4. System markiert den Trainingsfokus als gelesen.  

**Data Definitions:**  
Schwerpunkt: Typ Text  
Kategorie: Typ Text  
Notiz: Typ Text  
Status: Typ Text  
Gelesen: Typ Boolean  

**Exception:**  
- Kein Trainingsfokus vorhanden → Hinweis wird angezeigt.  

---

### Use Case Description

**ID:** 7  
**Title:** Trainingsplan einsehen  

**Pre-Conditions:**  
- Sportler ist erfolgreich eingeloggt (UC 2 Login).  

**Actors:**  
Sportler  

**Sequence:**  
1. Sportler navigiert zur Seite „Meine Trainingspläne”.  
2. System lädt alle Trainingspläne des Sportlers.  
3. Sportler wählt einen Trainingsplan aus und sieht den Inhalt.  

**Data Definitions:**  
Titel: Typ Text  
Inhalt: Typ Text  
Status: Typ Text  
Dauer: Typ Zahl  

**Exception:**  
- Kein Trainingsplan vorhanden → Hinweis wird angezeigt.  

---

### Use Case Description

**ID:** 8  
**Title:** Mit KI chatten  

**Pre-Conditions:**  
- Sportler ist erfolgreich eingeloggt (UC 2 Login).  

**Actors:**  
Sportler  

**Sequence:**  
1. Sportler navigiert zur Chat-Seite.  
2. Sportler gibt eine Nachricht ein und sendet sie (Enter oder Button).  
3. System sendet die Nachricht an das KI-Modell.  
4. KI-Antwort wird angezeigt.  

**Data Definitions:**  
Nachricht: Typ Text  
Antwort: Typ Text  

**Exception:**  
- KI-Modell nicht erreichbar → Fehlermeldung wird angezeigt.  

---

### Use Case Description

**ID:** 9  
**Title:** Sportler verwalten  

**Pre-Conditions:**  
- Coach ist erfolgreich eingeloggt (UC 2 Login).  

**Actors:**  
Coach  

**Sequence:**  
1. Coach navigiert zur Sportler-Verwaltungsseite.  
2. Coach kann Sportler suchen, filtern, erstellen oder löschen.  
3. System speichert die Änderungen und aktualisiert die Liste.  

**Data Definitions:**  
Name: Typ Text  
E-Mail: Typ Text  
Gürtelgrad: Typ Text  
Gewicht: Typ Zahl  

**Exception:**  
- E-Mail bereits vorhanden → 409 Conflict, Fehlermeldung wird angezeigt.  
- Ungültige E-Mail → 400 Bad Request, Fehlermeldung wird angezeigt.  
- Sportler nicht gefunden → 404 Not Found.  

---

### Use Case Description

**ID:** 10  
**Title:** Trainingsfokus definieren  

**Pre-Conditions:**  
- Coach ist erfolgreich eingeloggt (UC 2 Login).  
- Sportler existiert im System.  

**Actors:**  
Coach  

**Sequence:**  
1. Coach navigiert zur Trainingsfokus-Seite.  
2. Coach wählt einen Sportler aus und erfasst Schwerpunkt, Kategorie und Notiz.  
3. Coach setzt optional Turniername, Turnierdatum, Dauer und Einheiten.  
4. Trainingsfokus wird gespeichert.  

**Data Definitions:**  
Schwerpunkt: Typ Text  
Kategorie: Typ Text  
Notiz: Typ Text  
Status: Typ Text  
Dauer in Wochen: Typ Zahl  
Einheiten pro Woche: Typ Zahl  
Minuten pro Einheit: Typ Zahl  
Turniername: Typ Text  
Turnierdatum: Typ Zeit  

**Exception:**  
- Sportler existiert nicht → Trainingsfokus kann nicht erstellt werden.  

---

### Use Case Description

**ID:** 11  
**Title:** Trainingsfokus verwalten  

**Pre-Conditions:**  
- Coach ist erfolgreich eingeloggt (UC 2 Login).  
- Trainingsfokus existiert im System.  

**Actors:**  
Coach  

**Sequence:**  
1. Coach navigiert zur Trainingsfokus-Seite.  
2. Coach kann den Status eines Trainingsfokus auf AKTIV oder INAKTIV setzen.  
3. Coach kann einen Trainingsfokus löschen.  
4. System speichert die Änderungen.  

**Data Definitions:**  
Status: Typ Text (AKTIV, INAKTIV)  

**Exception:**  
- Trainingsfokus nicht gefunden → 404 Not Found.  
- Ungültiger Status → 400 Bad Request.  

---

### Use Case Description

**ID:** 12  
**Title:** Trainingspläne verwalten  

**Pre-Conditions:**  
- Coach ist erfolgreich eingeloggt (UC 2 Login).  
- Trainingsplan existiert im System.  

**Actors:**  
Coach  

**Sequence:**  
1. Coach navigiert zur Trainingsplan-Verwaltungsseite.  
2. Coach kann Trainingspläne filtern und einsehen.  
3. Coach kann den Status eines Trainingsplans ändern (Aktiv → Abgeschlossen).  
4. System speichert die Änderungen und sendet eine E-Mail an den Sportler.  

**Data Definitions:**  
Titel: Typ Text  
Status: Typ Text (ACTIVE, COMPLETED)  
Dauer: Typ Zahl  

**Exception:**  
- Trainingsplan nicht gefunden → 400 Bad Request.  
- Falscher Status für Übergang → Statusänderung nicht möglich.  

---

### Use Case Description

**ID:** 13  
**Title:** Trainingsplan archivieren  

**Pre-Conditions:**  
- Coach ist erfolgreich eingeloggt (UC 2 Login).  
- Trainingsplan hat den Status COMPLETED.  

**Actors:**  
Coach  

**Sequence:**  
1. Coach navigiert zur Trainingsplan-Verwaltungsseite.  
2. Coach wählt einen abgeschlossenen Trainingsplan aus.  
3. Coach klickt auf „Archivieren”.  
4. System setzt den Status auf ARCHIVED.  

**Data Definitions:**  
Status: Typ Text (COMPLETED → ARCHIVED)  

**Exception:**  
- Trainingsplan hat nicht den Status COMPLETED → Archivierung nicht möglich.  
- Trainingsplan nicht gefunden → 400 Bad Request.  

## Fachliches Datenmodell 
### ER-Diagramm

![ER-Diagramm](doc/er-diagram.drawio.svg)

---

### Beschreibung des fachlichen Modells

Das fachliche Datenmodell bildet die zentrale Domänenlogik von **KarateAI Coach** ab.  
Es beschreibt ausschließlich fachliche Konzepte ohne technische IDs oder Implementierungsdetails.

#### Zentrale Entitäten

**Sportler**
- Name
- E-Mail
- Gürtelgrad
- Gewicht

Ein Sportler wird von genau einem Trainer betreut und kann mehrere Trainingspläne, Trainingsfokusse sowie Wettkämpfe besitzen.

---

**Trainer**
- Name
- E-Mail
- Lizenzstufe

Ein Trainer betreut mehrere Sportler, definiert Trainingsfokusse und erstellt Übungen.

---

**Trainingsfokus**
- Beschreibung
- Schwerpunkt
- Status

Ein Trainingsfokus wird von einem Trainer definiert und gehört genau einem Sportler.  
Er dient als fachliche Grundlage für die Generierung von Trainingsplänen.

---

**Trainingsplan**
- Titel
- Erstelldatum
- Dauer
- Status

Ein Trainingsplan gehört genau einem Sportler und basiert auf genau einem Trainingsfokus.  
Ein Trainingsplan besteht aus mehreren Trainingseinheiten.

---

**Trainingseinheit**
- Datum
- Intensität
- Trainingsstatus

Eine Trainingseinheit gehört genau einem Trainingsplan und enthält mehrere Übungen.  
Zwischen Trainingseinheit und Übung besteht eine M:N-Beziehung.

---

**Übung**
- Name
- Beschreibung

Eine Übung wird von einem Trainer erstellt und kann in mehreren Trainingseinheiten verwendet werden.

---

**Wettkampf**
- Name
- Datum
- Kategorie
- Gewichtsklasse
- Ort
- Status

Ein Wettkampf gehört genau einem Sportler.

---

### Zustände im System

Folgende Entitäten besitzen fachliche Zustände:

**Trainingsplan**
- DRAFT
- ACTIVE
- COMPLETED
- ARCHIVED

**Trainingsfokus**
- AKTIV
- INAKTIV

**Trainingseinheit**
- GEPLANT
- DURCHGEFÜHRT
- ABGESAGT

**Wettkampf**
- GEPLANT
- DONE

Diese Zustände steuern die fachliche Logik des Systems (z.B. darf ein Trainingsplan nur generiert werden, wenn ein aktiver Trainingsfokus existiert).

## UI-Mockup

### Startseite (nicht eingeloggt)
![Login](doc/Mockups/Login.png)

### Dashboard
![Dashboard](doc/Mockups/Dashboard.png)

### Sportler
![Sportler](doc/Mockups/Sportler.png)

### Trainingsfokus
![Trainingsfokus](doc/Mockups/Trainingsfokus.png)

### KI-Assistent
![KI-Assistent](doc/Mockups/KI-Assistent.png)

### Account
![Account](doc/Mockups/Account.png)

# Implementation

## Frontend
> Beschreibung des Frontends mit Screenshots der fertigen Applikation. Alle Teile des GUIs, die bewertet werden sollen, müssen abgebildet sein.

## KI-Funktionen
> Aufgaben und Funktionen des eingebundenen KI-Modells.

## Optionale Anforderungen
> Liste der umgesetzten optionalen Anforderungen mit Beschreibung.

# Fazit

## Stand der Implementation
> Stand der Implementation, nächste Schritte (mit Referenz auf den Backlog).