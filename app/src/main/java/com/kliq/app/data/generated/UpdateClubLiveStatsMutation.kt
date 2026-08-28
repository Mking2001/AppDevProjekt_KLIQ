
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



public interface UpdateClubLiveStatsMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UpdateClubLiveStatsMutation.Data,
      UpdateClubLiveStatsMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val currentCapacityPercent: Int,
  
    val malePercentage: Int,
  
    val femalePercentage: Int,
  
    val totalLiveVisitors: Int,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val club_update: ClubKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpdateClubLiveStats"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateClubLiveStatsMutation.ref(
  
    id: String,currentCapacityPercent: Int,malePercentage: Int,femalePercentage: Int,totalLiveVisitors: Int,

  
  
): com.google.firebase.dataconnect.MutationRef<
    UpdateClubLiveStatsMutation.Data,
    UpdateClubLiveStatsMutation.Variables
  > =
  ref(
    
      UpdateClubLiveStatsMutation.Variables(
        id=id,currentCapacityPercent=currentCapacityPercent,malePercentage=malePercentage,femalePercentage=femalePercentage,totalLiveVisitors=totalLiveVisitors,
  
      )
    
  )

public suspend fun UpdateClubLiveStatsMutation.execute(

  
    
      id: String,currentCapacityPercent: Int,malePercentage: Int,femalePercentage: Int,totalLiveVisitors: Int,

  

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateClubLiveStatsMutation.Data,
    UpdateClubLiveStatsMutation.Variables
  > =
  ref(
    
      id=id,currentCapacityPercent=currentCapacityPercent,malePercentage=malePercentage,femalePercentage=femalePercentage,totalLiveVisitors=totalLiveVisitors,
  
    
  ).execute()


