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

public interface UpdateChatLastMessageMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UpdateChatLastMessageMutation.Data,
      UpdateChatLastMessageMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val id: String,

    val lastMessageText: String,

    val lastMessageTimestampMs: Long,

    val unreadCount: com.google.firebase.dataconnect.OptionalVariable<Int?>,

  ) {

      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: String
        public var lastMessageText: String
        public var lastMessageTimestampMs: Long
        public var unreadCount: Int?

      }

      public companion object {

        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,lastMessageText: String,lastMessageTimestampMs: Long,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var lastMessageText= lastMessageText
            var lastMessageTimestampMs= lastMessageTimestampMs
            var unreadCount: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }

            override var lastMessageText: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { lastMessageText = value_ }

            override var lastMessageTimestampMs: Long
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { lastMessageTimestampMs = value_ }

            override var unreadCount: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { unreadCount = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

          }.apply(block_)
          .let {
            Variables(
              id=id,lastMessageText=lastMessageText,lastMessageTimestampMs=lastMessageTimestampMs,unreadCount=unreadCount,
            )
          }
        }
      }

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val chat_update: ChatKey?,

  ) {

  }

  public companion object {
    public val operationName: String = "UpdateChatLastMessage"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateChatLastMessageMutation.ref(

    id: String,lastMessageText: String,lastMessageTimestampMs: Long,

    block_: UpdateChatLastMessageMutation.Variables.Builder.() -> Unit = {}

): com.google.firebase.dataconnect.MutationRef<
    UpdateChatLastMessageMutation.Data,
    UpdateChatLastMessageMutation.Variables
  > =
  ref(

      UpdateChatLastMessageMutation.Variables.build(
        id=id,lastMessageText=lastMessageText,lastMessageTimestampMs=lastMessageTimestampMs,

    block_
      )

  )

public suspend fun UpdateChatLastMessageMutation.execute(

      id: String,lastMessageText: String,lastMessageTimestampMs: Long,

    block_: UpdateChatLastMessageMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateChatLastMessageMutation.Data,
    UpdateChatLastMessageMutation.Variables
  > =
  ref(

      id=id,lastMessageText=lastMessageText,lastMessageTimestampMs=lastMessageTimestampMs,

    block_

  ).execute()
