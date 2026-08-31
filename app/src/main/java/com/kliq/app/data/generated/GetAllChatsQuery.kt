@file:Suppress(
  "KotlinRedundantDiagnosticSuppress",
  "PropertyName",
  "MayBeConstant",
  "RedundantVisibilityModifier",
  "RedundantCompanionReference",
  "RemoveEmptyClassBody",
  "SpellCheckingInspection",
  "unused",
)

package com.kliq.app.data.generated

import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map

public interface GetAllChatsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetAllChatsQuery.Data,
      Unit
    >
{

    @kotlinx.serialization.Serializable
  public data class Data(

    val chats: List<ChatsItem>,

  ) {

        @kotlinx.serialization.Serializable
  public data class ChatsItem(

    val id: String,

    val name: String,

    val chatType: String,

    val cityRegion: String?,

    val lastMessageText: String,

    val lastMessageTimestampMs: Long,

    val lastMessageTimestampIso: String,

    val avatarInitial: String,

    val avatarUrl: String?,

    val unreadCount: Int,

    val isOnline: Boolean,

    val isPinned: Boolean,

    val isMuted: Boolean,

    val isArchived: Boolean,

  ) {

  }

  }

  public companion object {
    public val operationName: String = "GetAllChats"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetAllChatsQuery.ref(

): com.google.firebase.dataconnect.QueryRef<
    GetAllChatsQuery.Data,
    Unit
  > =
  ref(

      Unit

  )

public suspend fun GetAllChatsQuery.execute(

  ): com.google.firebase.dataconnect.QueryResult<
    GetAllChatsQuery.Data,
    Unit
  > =
  ref(

  ).execute()

  public fun GetAllChatsQuery.flow(

    ): kotlinx.coroutines.flow.Flow<GetAllChatsQuery.Data> =
    ref(

      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }
