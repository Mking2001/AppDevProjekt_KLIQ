
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


import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map


public interface GetClubsByRegionQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetClubsByRegionQuery.Data,
      GetClubsByRegionQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val region: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val clubs: List<ClubsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ClubsItem(
  
    val id: String,
  
    val name: String,
  
    val latitude: Double,
  
    val longitude: Double,
  
    val address: String,
  
    val category: String,
  
    val rating: Double,
  
    val imageUrl: String,
  
    val city: String,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetClubsByRegion"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetClubsByRegionQuery.ref(
  
    region: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetClubsByRegionQuery.Data,
    GetClubsByRegionQuery.Variables
  > =
  ref(
    
      GetClubsByRegionQuery.Variables(
        region=region,
  
      )
    
  )

public suspend fun GetClubsByRegionQuery.execute(

  
    
      region: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetClubsByRegionQuery.Data,
    GetClubsByRegionQuery.Variables
  > =
  ref(
    
      region=region,
  
    
  ).execute()


  public fun GetClubsByRegionQuery.flow(
    
      region: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetClubsByRegionQuery.Data> =
    ref(
        
          region=region,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

