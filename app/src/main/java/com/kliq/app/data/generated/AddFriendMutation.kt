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

public interface AddFriendMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      AddFriendMutation.Data,
      AddFriendMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val userId: String,

    val friendUserId: String,

    val createdAtTimestampMs: Long,

    val isQrVerified: com.google.firebase.dataconnect.OptionalVariable<Boolean?>,

  ) {

      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var userId: String
        public var friendUserId: String
        public var createdAtTimestampMs: Long
        public var isQrVerified: Boolean?

      }

      public companion object {

        @Suppress("NAME_SHADOWING")
        public fun build(
          userId: String,friendUserId: String,createdAtTimestampMs: Long,
          block_: Builder.() -> Unit
        ): Variables {
          var userId= userId
            var friendUserId= friendUserId
            var createdAtTimestampMs= createdAtTimestampMs
            var isQrVerified: com.google.firebase.dataconnect.OptionalVariable<Boolean?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined

          return object : Builder {
            override var userId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { userId = value_ }

            override var friendUserId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { friendUserId = value_ }

            override var createdAtTimestampMs: Long
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { createdAtTimestampMs = value_ }

            override var isQrVerified: Boolean?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { isQrVerified = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

          }.apply(block_)
          .let {
            Variables(
              userId=userId,friendUserId=friendUserId,createdAtTimestampMs=createdAtTimestampMs,isQrVerified=isQrVerified,
            )
          }
        }
      }

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val friend_insert: FriendKey,

  ) {

  }

  public companion object {
    public val operationName: String = "AddFriend"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AddFriendMutation.ref(

    userId: String,friendUserId: String,createdAtTimestampMs: Long,

    block_: AddFriendMutation.Variables.Builder.() -> Unit = {}

): com.google.firebase.dataconnect.MutationRef<
    AddFriendMutation.Data,
    AddFriendMutation.Variables
  > =
  ref(

      AddFriendMutation.Variables.build(
        userId=userId,friendUserId=friendUserId,createdAtTimestampMs=createdAtTimestampMs,

    block_
      )

  )

public suspend fun AddFriendMutation.execute(

      userId: String,friendUserId: String,createdAtTimestampMs: Long,

    block_: AddFriendMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    AddFriendMutation.Data,
    AddFriendMutation.Variables
  > =
  ref(

      userId=userId,friendUserId=friendUserId,createdAtTimestampMs=createdAtTimestampMs,

    block_

  ).execute()
