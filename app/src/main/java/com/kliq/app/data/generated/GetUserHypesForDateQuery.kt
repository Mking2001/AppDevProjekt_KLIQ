
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


public interface GetUserHypesForDateQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetUserHypesForDateQuery.Data,
      GetUserHypesForDateQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val userId: String,
  
    val dateString: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val clubHypes: List<ClubHypesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ClubHypesItem(
  
    val clubId: String,
  
    val userId: String,
  
    val dateString: String,
  
    val createdAtMs: Long,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetUserHypesForDate"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetUserHypesForDateQuery.ref(
  
    userId: String,dateString: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetUserHypesForDateQuery.Data,
    GetUserHypesForDateQuery.Variables
  > =
  ref(
    
      GetUserHypesForDateQuery.Variables(
        userId=userId,dateString=dateString,
  
      )
    
  )

public suspend fun GetUserHypesForDateQuery.execute(

  
    
      userId: String,dateString: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetUserHypesForDateQuery.Data,
    GetUserHypesForDateQuery.Variables
  > =
  ref(
    
      userId=userId,dateString=dateString,
  
    
  ).execute()


  public fun GetUserHypesForDateQuery.flow(
    
      userId: String,dateString: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetUserHypesForDateQuery.Data> =
    ref(
        
          userId=userId,dateString=dateString,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

