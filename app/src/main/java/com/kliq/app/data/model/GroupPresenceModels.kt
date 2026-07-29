package com.kliq.app.data.model

enum class GroupMemberRole {
    MEMBER,
    MODERATOR,
    HOST,
    VIP
}

data class GroupMemberPresence(
    val userId: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val avatarInitial: String,
    val status: UserStatus = UserStatus.ONLINE,
    val role: GroupMemberRole = GroupMemberRole.MEMBER,
    val statusMessage: String? = null,
    val distanceKm: Double? = null,
    val lastActiveTimestampMs: Long = System.currentTimeMillis()
)

data class GroupPresenceSummary(
    val chatId: String,
    val chatTitle: String,
    val totalOnlineCount: Int,
    val totalMembersCount: Int,
    val members: List<GroupMemberPresence> = emptyList()
)

sealed class GroupPresenceState {
    object Loading : GroupPresenceState()
    data class Success(val summary: GroupPresenceSummary) : GroupPresenceState()
    data class Error(val message: String) : GroupPresenceState()
}
