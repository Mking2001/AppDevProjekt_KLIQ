
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



public interface RemoveClubHypeMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      RemoveClubHypeMutation.Data,
      RemoveClubHypeMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val clubId: String,
  
    val userId: String,
  
    val dateString: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val clubHype_delete: ClubHypeKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RemoveClubHype"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RemoveClubHypeMutation.ref(
  
    clubId: String,userId: String,dateString: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    RemoveClubHypeMutation.Data,
    RemoveClubHypeMutation.Variables
  > =
  ref(
    
      RemoveClubHypeMutation.Variables(
        clubId=clubId,userId=userId,dateString=dateString,
  
      )
    
  )

public suspend fun RemoveClubHypeMutation.execute(

  
    
      clubId: String,userId: String,dateString: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    RemoveClubHypeMutation.Data,
    RemoveClubHypeMutation.Variables
  > =
  ref(
    
      clubId=clubId,userId=userId,dateString=dateString,
  
    
  ).execute()


