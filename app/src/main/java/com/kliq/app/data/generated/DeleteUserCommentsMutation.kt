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

public interface DeleteUserCommentsMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteUserCommentsMutation.Data,
      DeleteUserCommentsMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val authorUserId: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val feedComment_deleteMany: Int,

  ) {

  }

  public companion object {
    public val operationName: String = "DeleteUserComments"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteUserCommentsMutation.ref(

    authorUserId: String,

): com.google.firebase.dataconnect.MutationRef<
    DeleteUserCommentsMutation.Data,
    DeleteUserCommentsMutation.Variables
  > =
  ref(

      DeleteUserCommentsMutation.Variables(
        authorUserId=authorUserId,

      )

  )

public suspend fun DeleteUserCommentsMutation.execute(

      authorUserId: String,

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteUserCommentsMutation.Data,
    DeleteUserCommentsMutation.Variables
  > =
  ref(

      authorUserId=authorUserId,

  ).execute()
