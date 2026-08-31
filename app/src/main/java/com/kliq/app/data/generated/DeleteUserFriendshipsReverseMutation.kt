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

public interface DeleteUserFriendshipsReverseMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteUserFriendshipsReverseMutation.Data,
      DeleteUserFriendshipsReverseMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val friendUserId: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val friend_deleteMany: Int,

  ) {

  }

  public companion object {
    public val operationName: String = "DeleteUserFriendshipsReverse"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteUserFriendshipsReverseMutation.ref(

    friendUserId: String,

): com.google.firebase.dataconnect.MutationRef<
    DeleteUserFriendshipsReverseMutation.Data,
    DeleteUserFriendshipsReverseMutation.Variables
  > =
  ref(

      DeleteUserFriendshipsReverseMutation.Variables(
        friendUserId=friendUserId,

      )

  )

public suspend fun DeleteUserFriendshipsReverseMutation.execute(

      friendUserId: String,

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteUserFriendshipsReverseMutation.Data,
    DeleteUserFriendshipsReverseMutation.Variables
  > =
  ref(

      friendUserId=friendUserId,

  ).execute()
