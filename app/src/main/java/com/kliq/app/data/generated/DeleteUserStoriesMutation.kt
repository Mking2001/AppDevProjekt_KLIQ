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

public interface DeleteUserStoriesMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteUserStoriesMutation.Data,
      DeleteUserStoriesMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val authorUserId: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val story_deleteMany: Int,

  ) {

  }

  public companion object {
    public val operationName: String = "DeleteUserStories"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteUserStoriesMutation.ref(

    authorUserId: String,

): com.google.firebase.dataconnect.MutationRef<
    DeleteUserStoriesMutation.Data,
    DeleteUserStoriesMutation.Variables
  > =
  ref(

      DeleteUserStoriesMutation.Variables(
        authorUserId=authorUserId,

      )

  )

public suspend fun DeleteUserStoriesMutation.execute(

      authorUserId: String,

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteUserStoriesMutation.Data,
    DeleteUserStoriesMutation.Variables
  > =
  ref(

      authorUserId=authorUserId,

  ).execute()
