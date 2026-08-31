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

public interface SendDirectMessageMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      SendDirectMessageMutation.Data,
      SendDirectMessageMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val id: String,

    val senderId: String,

    val receiverId: String,

    val text: String,

    val timestamp: Long,

    val messageType: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val mediaUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val caption: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val audioDurationMs: com.google.firebase.dataconnect.OptionalVariable<Long?>,

  ) {

      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: String
        public var senderId: String
        public var receiverId: String
        public var text: String
        public var timestamp: Long
        public var messageType: String?
        public var mediaUrl: String?
        public var caption: String?
        public var audioDurationMs: Long?

      }

      public companion object {

        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,senderId: String,receiverId: String,text: String,timestamp: Long,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var senderId= senderId
            var receiverId= receiverId
            var text= text
            var timestamp= timestamp
            var messageType: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var mediaUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var caption: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var audioDurationMs: com.google.firebase.dataconnect.OptionalVariable<Long?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }

            override var senderId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { senderId = value_ }

            override var receiverId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { receiverId = value_ }

            override var text: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { text = value_ }

            override var timestamp: Long
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { timestamp = value_ }

            override var messageType: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { messageType = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var mediaUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { mediaUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var caption: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { caption = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var audioDurationMs: Long?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { audioDurationMs = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

          }.apply(block_)
          .let {
            Variables(
              id=id,senderId=senderId,receiverId=receiverId,text=text,timestamp=timestamp,messageType=messageType,mediaUrl=mediaUrl,caption=caption,audioDurationMs=audioDurationMs,
            )
          }
        }
      }

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val directMessage_insert: DirectMessageKey,

  ) {

  }

  public companion object {
    public val operationName: String = "SendDirectMessage"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SendDirectMessageMutation.ref(

    id: String,senderId: String,receiverId: String,text: String,timestamp: Long,

    block_: SendDirectMessageMutation.Variables.Builder.() -> Unit = {}

): com.google.firebase.dataconnect.MutationRef<
    SendDirectMessageMutation.Data,
    SendDirectMessageMutation.Variables
  > =
  ref(

      SendDirectMessageMutation.Variables.build(
        id=id,senderId=senderId,receiverId=receiverId,text=text,timestamp=timestamp,

    block_
      )

  )

public suspend fun SendDirectMessageMutation.execute(

      id: String,senderId: String,receiverId: String,text: String,timestamp: Long,

    block_: SendDirectMessageMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    SendDirectMessageMutation.Data,
    SendDirectMessageMutation.Variables
  > =
  ref(

      id=id,senderId=senderId,receiverId=receiverId,text=text,timestamp=timestamp,

    block_

  ).execute()
