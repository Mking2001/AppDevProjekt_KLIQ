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
        val updatedMembers = currentSummary.members.map { member ->
            if (member.userId == userId) {
                member.copy(
                    status = status,
                    lastActiveTimestampMs = System.currentTimeMillis()
                )
            } else {
                member
            }
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

        val munichSummary = GroupPresenceSummary(
            chatId = "pub_2",
            chatTitle = "München - Party Radar",
            totalOnlineCount = 184,
            totalMembersCount = 980,
            members = listOf(
                GroupMemberPresence("u_20", "Korbinian W.", null, "K", UserStatus.ONLINE, GroupMemberRole.HOST, "Pacha München 💃", 0.6),
                GroupMemberPresence("u_21", "Anna L.", null, "A", UserStatus.ONLINE, GroupMemberRole.MODERATOR, "Glockenbachviertel", 1.1),
                GroupMemberPresence("u_22", "Florian S.", null, "F", UserStatus.ONLINE, GroupMemberRole.MEMBER, "089 Bar warmup", 1.4)
            )
        )

        presenceMap["pub_1"] = MutableStateFlow(berlinSummary)
        presenceMap["pub_2"] = MutableStateFlow(munichSummary)
    }

    private fun createDefaultSummaryForChat(chatId: String): GroupPresenceSummary {
        return GroupPresenceSummary(
            chatId = chatId,
            chatTitle = "Gruppe #$chatId",
            totalOnlineCount = 12,
            totalMembersCount = 45,
            members = listOf(
                GroupMemberPresence("current_user", "Du", null, "D", UserStatus.ONLINE, GroupMemberRole.MEMBER, "Aktiv", 0.0),
                GroupMemberPresence("u_99", "Alex N.", null, "A", UserStatus.ONLINE, GroupMemberRole.MEMBER, "In der Gruppe", 1.0)
            )
        )
    }
}
