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

public interface ToggleChatMuteMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      ToggleChatMuteMutation.Data,
      ToggleChatMuteMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val id: String,

    val isMuted: Boolean,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val chat_update: ChatKey?,

  ) {

  }

  public companion object {
    public val operationName: String = "ToggleChatMute"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ToggleChatMuteMutation.ref(

    id: String,isMuted: Boolean,

): com.google.firebase.dataconnect.MutationRef<
    ToggleChatMuteMutation.Data,
    ToggleChatMuteMutation.Variables
  > =
  ref(

      ToggleChatMuteMutation.Variables(
        id=id,isMuted=isMuted,

      )

  )

public suspend fun ToggleChatMuteMutation.execute(

      id: String,isMuted: Boolean,

  ): com.google.firebase.dataconnect.MutationResult<
    ToggleChatMuteMutation.Data,
    ToggleChatMuteMutation.Variables
  > =
  ref(

      id=id,isMuted=isMuted,

  ).execute()
