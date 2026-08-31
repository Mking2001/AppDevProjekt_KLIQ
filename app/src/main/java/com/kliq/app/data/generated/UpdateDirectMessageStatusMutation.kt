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

public interface UpdateDirectMessageStatusMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UpdateDirectMessageStatusMutation.Data,
      UpdateDirectMessageStatusMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val id: String,

    val deliveryStatus: String,

    val deliveredAtMs: com.google.firebase.dataconnect.OptionalVariable<Long?>,

    val readAtMs: com.google.firebase.dataconnect.OptionalVariable<Long?>,

  ) {

      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: String
        public var deliveryStatus: String
        public var deliveredAtMs: Long?
        public var readAtMs: Long?

      }

      public companion object {

        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,deliveryStatus: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var deliveryStatus= deliveryStatus
            var deliveredAtMs: com.google.firebase.dataconnect.OptionalVariable<Long?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var readAtMs: com.google.firebase.dataconnect.OptionalVariable<Long?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }

            override var deliveryStatus: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { deliveryStatus = value_ }

            override var deliveredAtMs: Long?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { deliveredAtMs = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var readAtMs: Long?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { readAtMs = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

          }.apply(block_)
          .let {
            Variables(
              id=id,deliveryStatus=deliveryStatus,deliveredAtMs=deliveredAtMs,readAtMs=readAtMs,
            )
          }
        }
      }

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val directMessage_update: DirectMessageKey?,

  ) {

  }

  public companion object {
    public val operationName: String = "UpdateDirectMessageStatus"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateDirectMessageStatusMutation.ref(

    id: String,deliveryStatus: String,

    block_: UpdateDirectMessageStatusMutation.Variables.Builder.() -> Unit = {}

): com.google.firebase.dataconnect.MutationRef<
    UpdateDirectMessageStatusMutation.Data,
    UpdateDirectMessageStatusMutation.Variables
  > =
  ref(

      UpdateDirectMessageStatusMutation.Variables.build(
        id=id,deliveryStatus=deliveryStatus,

    block_
      )

  )

public suspend fun UpdateDirectMessageStatusMutation.execute(

      id: String,deliveryStatus: String,

    block_: UpdateDirectMessageStatusMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateDirectMessageStatusMutation.Data,
    UpdateDirectMessageStatusMutation.Variables
  > =
  ref(

      id=id,deliveryStatus=deliveryStatus,

    block_

  ).execute()
