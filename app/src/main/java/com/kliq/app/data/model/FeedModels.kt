package com.kliq.app.data.model

data class FeedPost(
    val id: String,
    val authorUserId: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val contentText: String,
    val imageUrl: String? = null,
    val clubId: String? = null,
    val clubName: String? = null,
    val locationAddress: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isEventPinned: Boolean = false,
    val isFollowersOnly: Boolean = false,
    val createdAtMs: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val commentCount: Int = 0,
    val flameCount: Int = 0,
    val flameDate: String = "",
    val isHypedToday: Boolean = false
)

data class FeedComment(
    val id: String,
    val postId: String,
    val authorUserId: String,
    val authorName: String,
    val text: String,
    val createdAtMs: Long = System.currentTimeMillis()
)

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
