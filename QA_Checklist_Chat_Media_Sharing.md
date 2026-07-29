# QA Checklist: Kapitel 6.6 - Medien-Versand (Fotos in Chats)

| ID | Testfall | Erwartetes Ergebnis | Status |
|---|---|---|---|
| TC-MEDIA-01 | Galerie-Auswahl (System Photo Picker) | Öffnet den nativen System-Picker (`PickVisualMedia`); ausgewählte Bild-URI wird an das Preview-Modal übergeben. | PASSED |
| TC-MEDIA-02 | Kamera-Fotoaufnahme | Startet den nativen Kamera-Intent (`TakePicturePreview`); aufgenommenes Foto wird temporär gecacht und im Vorschau-Modal angezeigt. | PASSED |
| TC-MEDIA-03 | Bild-Komprimierung (`ImageCompressor`) | Bilder werden auf max. 1280px skaliert und mit 80% JPEG-Qualität gespeichert. Es tritt kein OOM auf. | PASSED |
| TC-MEDIA-04 | Bild-Vorschau-Modal (`ImageAttachmentPreviewDialog`) | Vorschau wird korrekt dargestellt; Bildunterschrift kann eingegeben oder Abbrechen (X) geklickt werden. | PASSED |
| TC-MEDIA-05 | Bild-Sprechblasen-Rendering | Bild wird im Kliq Dark/Purple-Design in der Sprechblase mit Seitenverhältnis (Aspect Ratio) gerendert. | PASSED |
| TC-MEDIA-06 | Lade-Indikator beim Senden | Während der Komprimierung/Des Sendens erscheint ein CircularProgressIndicator über dem Bild. | PASSED |
| TC-MEDIA-07 | Vollbild-Bildbetrachter | Antippen der Bild-Sprechblase öffnet den `FullscreenImageViewerDialog` mit Schließen-Button. | PASSED |
| TC-MEDIA-08 | Room-Datenbankmigration (v14 auf v15) | DB-Migration `MIGRATION_14_15` führt Spaltenänderungen fehlerfrei und ohne Datenverlust aus. | PASSED |
| TC-MEDIA-09 | Null-Transparenz-Regel | Keinerlei KI-Verweise oder automatische Kommentare im gesamten Quellcode vorhanden. | PASSED |
