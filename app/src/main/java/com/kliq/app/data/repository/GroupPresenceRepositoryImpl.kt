package com.kliq.app.data.repository

import com.kliq.app.data.datasource.GroupPresenceDataSource
import com.kliq.app.data.model.GroupMemberPresence
import com.kliq.app.data.model.GroupPresenceSummary
import com.kliq.app.data.model.UserStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupPresenceRepositoryImpl @Inject constructor(
    private val dataSource: GroupPresenceDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GroupPresenceRepository {

    override fun observeGroupPresence(chatId: String): Flow<GroupPresenceSummary> {
        return dataSource.observeGroupPresence(chatId).flowOn(ioDispatcher)
    }

    override fun observeOnlineCount(chatId: String): Flow<Int> {
        return dataSource.observeOnlineCount(chatId).flowOn(ioDispatcher)
    }

    override fun filterActiveMembers(chatId: String, query: String): Flow<List<GroupMemberPresence>> {
        return dataSource.observeGroupPresence(chatId).map { summary ->
            if (query.isBlank()) {
                summary.members
            } else {
                summary.members.filter { member ->
                    member.displayName.contains(query, ignoreCase = true) ||
                            (member.statusMessage?.contains(query, ignoreCase = true) == true)
                }
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun updatePresenceStatus(
        chatId: String,
        userId: String,
        status: UserStatus
    ): Result<Unit> = withContext(ioDispatcher) {
        dataSource.updateMemberPresenceStatus(chatId, userId, status)
    }

    override suspend fun getActiveMembers(chatId: String): List<GroupMemberPresence> = withContext(ioDispatcher) {
        dataSource.fetchActiveMembers(chatId)
    }
}
