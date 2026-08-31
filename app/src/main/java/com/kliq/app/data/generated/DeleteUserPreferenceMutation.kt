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

public interface DeleteUserPreferenceMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteUserPreferenceMutation.Data,
      DeleteUserPreferenceMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val userId: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val userPreference_delete: UserPreferenceKey?,

  ) {

  }

  public companion object {
    public val operationName: String = "DeleteUserPreference"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteUserPreferenceMutation.ref(

    userId: String,

): com.google.firebase.dataconnect.MutationRef<
    DeleteUserPreferenceMutation.Data,
    DeleteUserPreferenceMutation.Variables
  > =
  ref(

      DeleteUserPreferenceMutation.Variables(
        userId=userId,

      )

  )

public suspend fun DeleteUserPreferenceMutation.execute(

      userId: String,

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteUserPreferenceMutation.Data,
    DeleteUserPreferenceMutation.Variables
  > =
  ref(

      userId=userId,

  ).execute()
