
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


public interface GetAllClubsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetAllClubsQuery.Data,
      Unit
    >
{
  

  
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
  
    val region: String,
  
    val city: String,
  
    val currentCapacityPercent: Int,
  
    val malePercentage: Int,
  
    val femalePercentage: Int,
  
    val totalLiveVisitors: Int,
  
    val isFavorite: Boolean,
  
    val isPromoted: Boolean,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetAllClubs"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetAllClubsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetAllClubsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetAllClubsQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetAllClubsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetAllClubsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetAllClubsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

