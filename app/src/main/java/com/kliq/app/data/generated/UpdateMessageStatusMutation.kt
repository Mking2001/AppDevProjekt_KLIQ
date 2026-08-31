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

public interface UpdateMessageStatusMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UpdateMessageStatusMutation.Data,
      UpdateMessageStatusMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val id: String,

    val status: String,

    val deliveredAtMs: com.google.firebase.dataconnect.OptionalVariable<Long?>,

    val readAtMs: com.google.firebase.dataconnect.OptionalVariable<Long?>,

  ) {

      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: String
        public var status: String
        public var deliveredAtMs: Long?
        public var readAtMs: Long?

      }

      public companion object {

        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,status: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var status= status
            var deliveredAtMs: com.google.firebase.dataconnect.OptionalVariable<Long?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var readAtMs: com.google.firebase.dataconnect.OptionalVariable<Long?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }

            override var status: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { status = value_ }

            override var deliveredAtMs: Long?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { deliveredAtMs = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var readAtMs: Long?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { readAtMs = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

          }.apply(block_)
          .let {
            Variables(
              id=id,status=status,deliveredAtMs=deliveredAtMs,readAtMs=readAtMs,
            )
          }
        }
      }

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val message_update: MessageKey?,

  ) {

  }

  public companion object {
    public val operationName: String = "UpdateMessageStatus"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateMessageStatusMutation.ref(

    id: String,status: String,

    block_: UpdateMessageStatusMutation.Variables.Builder.() -> Unit = {}

): com.google.firebase.dataconnect.MutationRef<
    UpdateMessageStatusMutation.Data,
    UpdateMessageStatusMutation.Variables
  > =
  ref(

      UpdateMessageStatusMutation.Variables.build(
        id=id,status=status,

    block_
      )

  )

public suspend fun UpdateMessageStatusMutation.execute(

      id: String,status: String,

    block_: UpdateMessageStatusMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateMessageStatusMutation.Data,
    UpdateMessageStatusMutation.Variables
  > =
  ref(

      id=id,status=status,

    block_

  ).execute()
