package com.kliq.app.data.model

/**
 * Domänenmodell eines Beitrags im Home-Feed.
 *
 * @param id Eindeutige Beitrags-ID.
 * @param authorUserId ID des verfassenden Nutzers.
 * @param authorName Anzeigename des Verfassers.
 * @param authorAvatarUrl Optionale Profilbild-URL des Verfassers.
 * @param contentText Textinhalt des Beitrags.
 * @param imageUrl Optionale Bild-URL. Ist der Wert leer, rendert die UI eine Fallback-Grafik.
 * @param clubId Optionale Verknüpfung zu einem Club, über den der Beitrag verfasst wurde.
 * @param clubName Anzeigename des verknüpften Clubs.
 * @param createdAtMs Erstellungszeitpunkt in Millisekunden.
 * @param likeCount Anzahl der Likes.
 * @param isLikedByMe Ob der aktuelle Nutzer den Beitrag geliked hat.
 * @param commentCount Anzahl der Kommentare.
 */
data class FeedPost(
    val id: String,
    val authorUserId: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val contentText: String,
    val imageUrl: String? = null,
    val clubId: String? = null,
    val clubName: String? = null,
    val createdAtMs: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val commentCount: Int = 0
)

/**
 * Domänenmodell eines Kommentars zu einem Feed-Beitrag.
 */
data class FeedComment(
    val id: String,
    val postId: String,
    val authorUserId: String,
    val authorName: String,
    val text: String,
    val createdAtMs: Long = System.currentTimeMillis()
)

/**
 * Domänenmodell einer Story-Kachel in der horizontalen Story-Leiste.
 *
 * @param headline Kurztext, der im Story-Viewer über dem Motiv erscheint.
 * @param isSeen Ob die Story bereits geöffnet wurde. Steuert den Gradient-Rahmen.
 */
data class Story(
    val id: String,
    val authorUserId: String,
    val authorName: String,
    val avatarUrl: String? = null,
    val imageUrl: String? = null,
    val headline: String = "",
    val clubName: String? = null,
    val createdAtMs: Long = System.currentTimeMillis(),
    val isSeen: Boolean = false
)

/**
 * Wandelt einen Zeitstempel in eine relative Zeitangabe für die Feed-Darstellung um.
 * Ausgabe erfolgt in deutscher Kurzform, wie im Feed und in der Story-Leiste verwendet.
 *
 * @param timestampMs Zeitpunkt des Ereignisses.
 * @param nowMs Referenzzeitpunkt, standardmäßig die aktuelle Systemzeit.
 */
fun formatRelativeTime(timestampMs: Long, nowMs: Long = System.currentTimeMillis()): String {
    val deltaMs = (nowMs - timestampMs).coerceAtLeast(0L)
    val minutes = deltaMs / 60_000L
    val hours = minutes / 60L
    val days = hours / 24L

    return when {
        minutes < 1L -> "Gerade eben"
        minutes < 60L -> "Vor $minutes Min."
        hours < 24L -> "Vor $hours Std."
        days < 7L -> "Vor $days Tg."
        else -> "Vor ${days / 7L} Wo."
    }
}
