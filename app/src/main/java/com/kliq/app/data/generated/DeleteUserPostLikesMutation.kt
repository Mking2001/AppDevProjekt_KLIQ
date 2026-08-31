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

public interface DeleteUserPostLikesMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteUserPostLikesMutation.Data,
      DeleteUserPostLikesMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val userId: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val feedPostLike_deleteMany: Int,

  ) {

  }

  public companion object {
    public val operationName: String = "DeleteUserPostLikes"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteUserPostLikesMutation.ref(

    userId: String,

): com.google.firebase.dataconnect.MutationRef<
    DeleteUserPostLikesMutation.Data,
    DeleteUserPostLikesMutation.Variables
  > =
  ref(

      DeleteUserPostLikesMutation.Variables(
        userId=userId,

      )

  )

public suspend fun DeleteUserPostLikesMutation.execute(

      userId: String,

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteUserPostLikesMutation.Data,
    DeleteUserPostLikesMutation.Variables
  > =
  ref(

      userId=userId,

  ).execute()
