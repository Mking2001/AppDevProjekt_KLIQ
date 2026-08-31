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

public interface BlockUserMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      BlockUserMutation.Data,
      BlockUserMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val userId: String,

    val blockedUserId: String,

    val reason: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val blockedAtTimestampMs: Long,

  ) {

      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var userId: String
        public var blockedUserId: String
        public var reason: String?
        public var blockedAtTimestampMs: Long

      }

      public companion object {

        @Suppress("NAME_SHADOWING")
        public fun build(
          userId: String,blockedUserId: String,blockedAtTimestampMs: Long,
          block_: Builder.() -> Unit
        ): Variables {
          var userId= userId
            var blockedUserId= blockedUserId
            var reason: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var blockedAtTimestampMs= blockedAtTimestampMs

          return object : Builder {
            override var userId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { userId = value_ }

            override var blockedUserId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { blockedUserId = value_ }

            override var reason: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { reason = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var blockedAtTimestampMs: Long
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { blockedAtTimestampMs = value_ }

          }.apply(block_)
          .let {
            Variables(
              userId=userId,blockedUserId=blockedUserId,reason=reason,blockedAtTimestampMs=blockedAtTimestampMs,
            )
          }
        }
      }

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val blockedUser_insert: BlockedUserKey,

  ) {

  }

  public companion object {
    public val operationName: String = "BlockUser"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun BlockUserMutation.ref(

    userId: String,blockedUserId: String,blockedAtTimestampMs: Long,

    block_: BlockUserMutation.Variables.Builder.() -> Unit = {}

): com.google.firebase.dataconnect.MutationRef<
    BlockUserMutation.Data,
    BlockUserMutation.Variables
  > =
  ref(

      BlockUserMutation.Variables.build(
        userId=userId,blockedUserId=blockedUserId,blockedAtTimestampMs=blockedAtTimestampMs,

    block_
      )

  )

public suspend fun BlockUserMutation.execute(

      userId: String,blockedUserId: String,blockedAtTimestampMs: Long,

    block_: BlockUserMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    BlockUserMutation.Data,
    BlockUserMutation.Variables
  > =
  ref(

      userId=userId,blockedUserId=blockedUserId,blockedAtTimestampMs=blockedAtTimestampMs,

    block_

  ).execute()
