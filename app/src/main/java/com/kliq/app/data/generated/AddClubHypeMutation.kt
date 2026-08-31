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

public interface AddClubHypeMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      AddClubHypeMutation.Data,
      AddClubHypeMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val clubId: String,

    val userId: String,

    val dateString: String,

    val createdAtMs: Long,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val clubHype_insert: ClubHypeKey,

  ) {

  }

  public companion object {
    public val operationName: String = "AddClubHype"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AddClubHypeMutation.ref(

    clubId: String,userId: String,dateString: String,createdAtMs: Long,

): com.google.firebase.dataconnect.MutationRef<
    AddClubHypeMutation.Data,
    AddClubHypeMutation.Variables
  > =
  ref(

      AddClubHypeMutation.Variables(
        clubId=clubId,userId=userId,dateString=dateString,createdAtMs=createdAtMs,

      )

  )

public suspend fun AddClubHypeMutation.execute(

      clubId: String,userId: String,dateString: String,createdAtMs: Long,

  ): com.google.firebase.dataconnect.MutationResult<
    AddClubHypeMutation.Data,
    AddClubHypeMutation.Variables
  > =
  ref(

      clubId=clubId,userId=userId,dateString=dateString,createdAtMs=createdAtMs,

  ).execute()
