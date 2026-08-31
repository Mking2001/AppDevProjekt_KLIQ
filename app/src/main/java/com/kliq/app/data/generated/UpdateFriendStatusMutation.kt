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

public interface UpdateFriendStatusMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UpdateFriendStatusMutation.Data,
      UpdateFriendStatusMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val userId: String,

    val friendUserId: String,

    val status: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val friend_update: FriendKey?,

  ) {

  }

  public companion object {
    public val operationName: String = "UpdateFriendStatus"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateFriendStatusMutation.ref(

    userId: String,friendUserId: String,status: String,

): com.google.firebase.dataconnect.MutationRef<
    UpdateFriendStatusMutation.Data,
    UpdateFriendStatusMutation.Variables
  > =
  ref(

      UpdateFriendStatusMutation.Variables(
        userId=userId,friendUserId=friendUserId,status=status,

      )

  )

public suspend fun UpdateFriendStatusMutation.execute(

      userId: String,friendUserId: String,status: String,

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateFriendStatusMutation.Data,
    UpdateFriendStatusMutation.Variables
  > =
  ref(

      userId=userId,friendUserId=friendUserId,status=status,

  ).execute()
