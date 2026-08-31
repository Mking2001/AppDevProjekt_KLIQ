
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


public interface GetEventsByClubQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetEventsByClubQuery.Data,
      GetEventsByClubQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val clubId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val events: List<EventsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class EventsItem(
  
    val id: String,
  
    val title: String,
  
    val description: String,
  
    val startTime: Long,
  
    val endTime: Long,
  
    val price: String,
  
    val time: String,
  
    val imageUrl: String?,
  
    val category: String,
  
    val isCancelled: Boolean,
  
    val capacityLimit: Int,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetEventsByClub"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetEventsByClubQuery.ref(
  
    clubId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetEventsByClubQuery.Data,
    GetEventsByClubQuery.Variables
  > =
  ref(
    
      GetEventsByClubQuery.Variables(
        clubId=clubId,
  
      )
    
  )

public suspend fun GetEventsByClubQuery.execute(

  
    
      clubId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetEventsByClubQuery.Data,
    GetEventsByClubQuery.Variables
  > =
  ref(
    
      clubId=clubId,
  
    
  ).execute()


  public fun GetEventsByClubQuery.flow(
    
      clubId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetEventsByClubQuery.Data> =
    ref(
        
          clubId=clubId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

