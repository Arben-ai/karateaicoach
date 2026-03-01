# KarateAI Coach

KarateAI Coach ist eine webbasierte Anwendung zur KI-gestützten Erstellung und Verwaltung individueller Trainingspläne für Karateka.  

Die Applikation generiert auf Basis von Gürtelgrad, Trainingszielen und Trainingshäufigkeit personalisierte Trainingspläne und ermöglicht die strukturierte Nachverfolgung des Trainingsfortschritts.

## Rollen

- **Sportler**
  - Kann Trainingspläne generieren lassen
  - Kann Trainingspläne einsehen und verwalten
  - Kann Trainingsfortschritte dokumentieren

- **Trainer**  
  - Kann Sportler betreuen
  - Kann Trainingspläne einsehen und anpassen
  - Kann Fortschritte analysieren

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
> Hier das Explore-Board einfügen

## Create-Board
> Hier das Create-Board einfügen

## Evaluate-Board
> Hier das Evaluate-Board einfügen

## Diskussion Feedback Pitch
> Diskussion des Feedbacks aus dem Pitch (bezogen auf Projektinhalt)

# Anforderungen

## Use-Case Diagramm
![Use-Case Diagramm](doc/uc-diagram.drawio.svg)

## Use-Case Beschreibung
### Use Case Description

**ID:** 1  
**Title:** Trainingsplan generieren  

**Pre-Conditions:**  
- Der Sportler ist erfolgreich eingeloggt (UC Login).  
- Es existiert ein aktiver Trainingsfokus für den Sportler.  

**Actors:**  
Sportler  

**Sequence:**  
1. Sportler wählt „Trainingsplan generieren“.  
2. System prüft, ob ein aktiver Trainingsfokus existiert.  
3. System lädt Trainingsfokus und verfügbare Übungen.  
4. KI generiert einen Trainingsplan basierend auf den Daten.  
5. Trainingsplan wird gespeichert und angezeigt.  

**Data Definitions:**  
Trainingsfokus: Typ Text  
Übung: Typ Text  
Titel: Typ Text  
Erstelldatum: Typ Zeit  
Dauer: Typ Zahl  
Status: Typ Text  

**Exception:**  
- Kein aktiver Trainingsfokus vorhanden → Trainingsplan kann nicht generiert werden.  
- Fehler bei KI-Generierung → Fehlermeldung wird angezeigt.  

---

### Use Case Description

**ID:** 2  
**Title:** Trainingsfokus definieren  

**Pre-Conditions:**  
- Trainer ist erfolgreich eingeloggt (UC Login).  
- Sportler existiert im System.  

**Actors:**  
Trainer  

**Sequence:**  
1. Trainer wählt einen Sportler aus.  
2. Trainer erfasst Beschreibung und Schwerpunkt.  
3. Trainer setzt Status auf „Aktiv“.  
4. Trainingsfokus wird gespeichert.  

**Data Definitions:**  
Beschreibung: Typ Text  
Schwerpunkt: Typ Text  
Status: Typ Text  

**Exception:**  
- Sportler existiert nicht → Trainingsfokus kann nicht erstellt werden.  

---

### Use Case Description

**ID:** 3  
**Title:** Trainingsfortschritt erfassen  

**Pre-Conditions:**  
- Sportler ist erfolgreich eingeloggt (UC Login).  
- Ein Trainingsplan existiert.  

**Actors:**  
Sportler  

**Sequence:**  
1. Sportler öffnet eine Trainingseinheit.  
2. Sportler trägt Intensität und Status ein.  
3. Änderungen werden gespeichert.  

**Data Definitions:**  
Datum: Typ Zeit  
Intensität: Typ Zahl  
Trainingsstatus: Typ Text  

**Exception:**  
- Trainingseinheit nicht vorhanden → Eingabe nicht möglich.   

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
> Mockup oder Skizze des UIs

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