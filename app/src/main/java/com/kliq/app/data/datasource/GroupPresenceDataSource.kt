package com.kliq.app.data.datasource

import com.kliq.app.data.model.GroupMemberPresence
import com.kliq.app.data.model.GroupMemberRole
import com.kliq.app.data.model.GroupPresenceSummary
import com.kliq.app.data.model.UserStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

interface GroupPresenceDataSource {
    fun observeGroupPresence(chatId: String): Flow<GroupPresenceSummary>
    fun observeOnlineCount(chatId: String): Flow<Int>
    suspend fun updateMemberPresenceStatus(chatId: String, userId: String, status: UserStatus): Result<Unit>
    suspend fun fetchActiveMembers(chatId: String): List<GroupMemberPresence>
}

@Singleton
class GroupPresenceDataSourceImpl @Inject constructor() : GroupPresenceDataSource {

    private val presenceMap = ConcurrentHashMap<String, MutableStateFlow<GroupPresenceSummary>>()

    init {
        initializeDefaultCityPresence()
    }

    override fun observeGroupPresence(chatId: String): Flow<GroupPresenceSummary> {
        val flow = presenceMap.getOrPut(chatId) {
            MutableStateFlow(createDefaultSummaryForChat(chatId))
        }
        return flow.asStateFlow()
    }

    override fun observeOnlineCount(chatId: String): Flow<Int> {
        return observeGroupPresence(chatId).map { it.totalOnlineCount }
    }

    override suspend fun updateMemberPresenceStatus(
        chatId: String,
        userId: String,
        status: UserStatus
    ): Result<Unit> {
        val flow = presenceMap[chatId] ?: return Result.failure(IllegalArgumentException("Chat nicht gefunden"))
        val currentSummary = flow.value
        val memberExists = currentSummary.members.any { it.userId == userId }
        val updatedMembers = if (memberExists) {
            currentSummary.members.map { member ->
                if (member.userId == userId) {
                    member.copy(
                        status = status,
                        lastActiveTimestampMs = System.currentTimeMillis()
                    )
                } else {
                    member
                }
            }
        } else {
            currentSummary.members + GroupMemberPresence(
                userId = userId,
                displayName = if (userId == "current_user") "Du" else "Nutzer #$userId",
                avatarInitial = if (userId == "current_user") "D" else "N",
                status = status,
                role = GroupMemberRole.MEMBER,
                statusMessage = "Aktiv im Chat",
                distanceKm = 0.0
            )
        }

        val onlineCount = updatedMembers.count { it.status == UserStatus.ONLINE || it.status == UserStatus.AWAY }
        flow.value = currentSummary.copy(
            totalOnlineCount = onlineCount,
            members = updatedMembers
        )
        return Result.success(Unit)
    }

    override suspend fun fetchActiveMembers(chatId: String): List<GroupMemberPresence> {
        val summary = presenceMap[chatId]?.value ?: createDefaultSummaryForChat(chatId)
        return summary.members
    }

    private fun initializeDefaultCityPresence() {
        val berlinSummary = GroupPresenceSummary(
            chatId = "pub_1",
            chatTitle = "Berlin - Tonight",
            totalOnlineCount = 248,
            totalMembersCount = 1420,
            members = listOf(
                GroupMemberPresence("current_user", "Du (Ich)", null, "D", UserStatus.ONLINE, GroupMemberRole.MEMBER, "Online im Chat", 0.0),
                GroupMemberPresence("u_1", "Elena M.", null, "E", UserStatus.ONLINE, GroupMemberRole.HOST, "Vor Ort @ Watergate", 0.4),
                GroupMemberPresence("u_2", "Lukas K.", null, "L", UserStatus.ONLINE, GroupMemberRole.MODERATOR, "Cruising in Mitte", 1.2),
                GroupMemberPresence("u_3", "Sophie W.", null, "S", UserStatus.ONLINE, GroupMemberRole.VIP, "Techno & Vibes ✨", 0.8),
                GroupMemberPresence("u_4", "Maximilian B.", null, "M", UserStatus.AWAY, GroupMemberRole.MEMBER, "Gleich im KitKat", 2.5),
                GroupMemberPresence("u_5", "Laura S.", null, "L", UserStatus.ONLINE, GroupMemberRole.MEMBER, "Wer ist am Spati?", 0.3),
                GroupMemberPresence("u_6", "Jonas H.", null, "J", UserStatus.ONLINE, GroupMemberRole.MEMBER, "Matrix Pre-Drink 🍹", 1.9),
                GroupMemberPresence("u_7", "Mia R.", null, "M", UserStatus.OFFLINE, GroupMemberRole.MEMBER, "Pause", 3.1),
                GroupMemberPresence("u_8", "David T.", null, "D", UserStatus.ONLINE, GroupMemberRole.MEMBER, "RAW-Gelände Crew", 1.5)
            )
        )

        val klagenfurtMembers = listOf(
            GroupMemberPresence("current_user", "Du (Ich)", null, "D", UserStatus.ONLINE, GroupMemberRole.MEMBER, "Online im Chat", 0.0),
            GroupMemberPresence("u_seed_1", "Elena M.", null, "E", UserStatus.ONLINE, GroupMemberRole.MODERATOR, "Am Wörthersee", 0.8),
            GroupMemberPresence("u_seed_2", "Lukas K.", null, "L", UserStatus.ONLINE, GroupMemberRole.MEMBER, "Klagenfurt Innenstadt", 1.2)
        )
        val klagenfurtSummary = GroupPresenceSummary(
            chatId = "pub_klagenfurt",
            chatTitle = "Klagenfurt",
            totalOnlineCount = klagenfurtMembers.count { it.status == UserStatus.ONLINE || it.status == UserStatus.AWAY },
            totalMembersCount = klagenfurtMembers.size,
            members = klagenfurtMembers
        )
        presenceMap["pub_1"] = MutableStateFlow(berlinSummary)
        presenceMap["pub_klagenfurt"] = MutableStateFlow(klagenfurtSummary)
    }

    private fun createDefaultSummaryForChat(chatId: String): GroupPresenceSummary {
        val defaultMembers = listOf(
            GroupMemberPresence("current_user", "Du", null, "D", UserStatus.ONLINE, GroupMemberRole.MEMBER, "Aktiv", 0.0)
        )
        val onlineCount = defaultMembers.count { it.status == UserStatus.ONLINE || it.status == UserStatus.AWAY }
        val title = when (chatId) {
            "pub_klagenfurt" -> "Klagenfurt"
            "pub_villach" -> "Villach"
            "pub_graz" -> "Graz"
            "pub_wien" -> "Wien"
            "pub_salzburg" -> "Salzburg"
            "pub_innsbruck" -> "Innsbruck"
            "pub_linz" -> "Linz"
            else -> "Gruppe #$chatId"
        }
        return GroupPresenceSummary(
            chatId = chatId,
            chatTitle = title,
            totalOnlineCount = onlineCount,
            totalMembersCount = defaultMembers.size,
            members = defaultMembers
        )
    }
}
