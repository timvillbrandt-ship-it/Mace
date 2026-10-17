# MaceClient (Fabric, MC 26.2)

Client-only Utility-Mod mit Mace-Modulen und Panel-ClickGUI.

## Bauen
1. JDK 25 installieren.
2. `./gradlew build` (Gradle-Wrapper vom Fabric-Template, https://fabricmc.net/develop/template/ ,
   in diesen Ordner kopieren - gradle/wrapper + gradlew).
3. Jar liegt in `build/libs/maceclient-1.0.0.jar` -> in den mods-Ordner (zusammen mit Fabric API).

## Bedienung
- Rechts-Shift: ClickGUI auf/zu
- Linksklick Modul: an/aus, Rechtsklick: Einstellungen, Mittelklick: Taste binden (ESC/Entf = loeschen)
- Header ziehen: Panel verschieben, Rechtsklick auf Header: einklappen
- Tipp: WindBoost an eine Taste binden, er feuert dann bei jedem Tastendruck einmal.

## Module
Mace: AutoMace, BreachSwap, WindBoost | Combat: StunSlam | Visual: HUD

## Wenn der Build meckert
Alle versionsempfindlichen Aufrufe stehen in `Compat.java`; Import-Pfade und
Methodennamen der 26.2-API ggf. dort und in `ClickGuiScreen` anpassen.
