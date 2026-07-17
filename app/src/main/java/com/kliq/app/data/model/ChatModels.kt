package com.kliq.app.data.model

/**
 * Typ-Unterscheidung zwischen öffentlichen Gruppen-Chats
 * und privaten Einzelgesprächen.
 */
enum class ChatType {
    PUBLIC,
    PRIVATE
}

/**
 * Repräsentiert einen einzelnen Chat-Eintrag in der Übersichtsliste.
 * Enthält alle Informationen, die für die Vorschau-Darstellung
 * in der Chat-Liste benötigt werden.
 *
 * @param id Eindeutige Chat-ID.
 * @param name Anzeigename des Chats (Gruppenname oder Kontaktname).
 * @param lastMessage Vorschautext der letzten Nachricht.
 * @param timestamp Zeitstempel der letzten Aktivität (formatiert).
 * @param avatarInitial Initiale für den Platzhalter-Avatar.
 * @param unreadCount Anzahl ungelesener Nachrichten.
 * @param chatType Unterscheidung zwischen PUBLIC und PRIVATE Chat.
 * @param isOnline Ob der Kontakt online ist (nur bei PRIVATE relevant).
 */
data class ChatConversation(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: String,
    val avatarInitial: String,
    val unreadCount: Int = 0,
    val chatType: ChatType,
    val isOnline: Boolean = false
)

/**
 * Einzelne Nachricht innerhalb eines Chatverlaufs.
 * Das [isMine]-Flag bestimmt die Ausrichtung und Farbgebung
 * der Sprechblase in der UI (eigene = Lila, fremde = Grau).
 *
 * @param id Eindeutige Nachrichten-ID.
 * @param senderName Anzeigename des Absenders.
 * @param text Nachrichteninhalt.
 * @param timestamp Zeitstempel (formatiert).
 * @param isMine Ob die Nachricht vom aktuellen Nutzer stammt.
 * @param dateHeader Optionaler Datums-Header (z.B. "Heute", "Gestern").
 */
data class ChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isMine: Boolean,
    val dateHeader: String? = null
)
