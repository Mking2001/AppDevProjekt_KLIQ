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

public interface DeleteUserMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteUserMutation.Data,
      DeleteUserMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val id: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val user_delete: UserKey?,

  ) {

  }

  public companion object {
    public val operationName: String = "DeleteUser"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteUserMutation.ref(

    id: String,

): com.google.firebase.dataconnect.MutationRef<
    DeleteUserMutation.Data,
    DeleteUserMutation.Variables
  > =
  ref(

      DeleteUserMutation.Variables(
        id=id,

      )

  )

public suspend fun DeleteUserMutation.execute(

      id: String,

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteUserMutation.Data,
    DeleteUserMutation.Variables
  > =
  ref(

      id=id,

  ).execute()
