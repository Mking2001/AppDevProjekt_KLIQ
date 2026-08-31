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

public interface GetMessagesByChatQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetMessagesByChatQuery.Data,
      GetMessagesByChatQuery.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val chatId: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val messages: List<MessagesItem>,

  ) {

        @kotlinx.serialization.Serializable
  public data class MessagesItem(

    val id: String,

    val senderUserId: String,

    val senderName: String,

    val text: String,

    val timestampMs: Long,

    val timestampIso: String,

    val mediaUrl: String?,

    val messageType: String,

    val thumbnailUrl: String?,

    val caption: String?,

    val audioDurationMs: Long,

    val status: String,

    val deliveredAtMs: Long?,

    val readAtMs: Long?,

    val isMine: Boolean,

    val replyToMessageId: String?,

    val isEdited: Boolean,

  ) {

  }

  }

  public companion object {
    public val operationName: String = "GetMessagesByChat"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetMessagesByChatQuery.ref(

    chatId: String,

): com.google.firebase.dataconnect.QueryRef<
    GetMessagesByChatQuery.Data,
    GetMessagesByChatQuery.Variables
  > =
  ref(

      GetMessagesByChatQuery.Variables(
        chatId=chatId,

      )

  )

public suspend fun GetMessagesByChatQuery.execute(

      chatId: String,

  ): com.google.firebase.dataconnect.QueryResult<
    GetMessagesByChatQuery.Data,
    GetMessagesByChatQuery.Variables
  > =
  ref(

      chatId=chatId,

  ).execute()

  public fun GetMessagesByChatQuery.flow(

      chatId: String,

    ): kotlinx.coroutines.flow.Flow<GetMessagesByChatQuery.Data> =
    ref(

          chatId=chatId,

      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }
