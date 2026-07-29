package com.kliq.app.data.repository

import com.kliq.app.data.model.GroupMemberPresence
import com.kliq.app.data.model.GroupPresenceSummary
import com.kliq.app.data.model.UserStatus
import kotlinx.coroutines.flow.Flow

interface GroupPresenceRepository {
    fun observeGroupPresence(chatId: String): Flow<GroupPresenceSummary>
    fun observeOnlineCount(chatId: String): Flow<Int>
    fun filterActiveMembers(chatId: String, query: String): Flow<List<GroupMemberPresence>>
    suspend fun updatePresenceStatus(chatId: String, userId: String, status: UserStatus): Result<Unit>
    suspend fun getActiveMembers(chatId: String): List<GroupMemberPresence>
}
