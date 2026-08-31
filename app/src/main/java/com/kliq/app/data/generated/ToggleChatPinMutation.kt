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

public interface ToggleChatPinMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      ToggleChatPinMutation.Data,
      ToggleChatPinMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val id: String,

    val isPinned: Boolean,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val chat_update: ChatKey?,

  ) {

  }

  public companion object {
    public val operationName: String = "ToggleChatPin"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ToggleChatPinMutation.ref(

    id: String,isPinned: Boolean,

): com.google.firebase.dataconnect.MutationRef<
    ToggleChatPinMutation.Data,
    ToggleChatPinMutation.Variables
  > =
  ref(

      ToggleChatPinMutation.Variables(
        id=id,isPinned=isPinned,

      )

  )

public suspend fun ToggleChatPinMutation.execute(

      id: String,isPinned: Boolean,

  ): com.google.firebase.dataconnect.MutationResult<
    ToggleChatPinMutation.Data,
    ToggleChatPinMutation.Variables
  > =
  ref(

      id=id,isPinned=isPinned,

  ).execute()
