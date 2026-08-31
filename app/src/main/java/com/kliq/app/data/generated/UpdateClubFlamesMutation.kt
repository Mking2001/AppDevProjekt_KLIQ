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

public interface UpdateClubFlamesMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UpdateClubFlamesMutation.Data,
      UpdateClubFlamesMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val id: String,

    val flameCount: Int,

    val flameDate: com.google.firebase.dataconnect.OptionalVariable<String?>,

  ) {

      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: String
        public var flameCount: Int
        public var flameDate: String?

      }

      public companion object {

        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,flameCount: Int,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var flameCount= flameCount
            var flameDate: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }

            override var flameCount: Int
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { flameCount = value_ }

            override var flameDate: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { flameDate = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

          }.apply(block_)
          .let {
            Variables(
              id=id,flameCount=flameCount,flameDate=flameDate,
            )
          }
        }
      }

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val club_update: ClubKey?,

  ) {

  }

  public companion object {
    public val operationName: String = "UpdateClubFlames"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateClubFlamesMutation.ref(

    id: String,flameCount: Int,

    block_: UpdateClubFlamesMutation.Variables.Builder.() -> Unit = {}

): com.google.firebase.dataconnect.MutationRef<
    UpdateClubFlamesMutation.Data,
    UpdateClubFlamesMutation.Variables
  > =
  ref(

      UpdateClubFlamesMutation.Variables.build(
        id=id,flameCount=flameCount,

    block_
      )

  )

public suspend fun UpdateClubFlamesMutation.execute(

      id: String,flameCount: Int,

    block_: UpdateClubFlamesMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateClubFlamesMutation.Data,
    UpdateClubFlamesMutation.Variables
  > =
  ref(

      id=id,flameCount=flameCount,

    block_

  ).execute()
