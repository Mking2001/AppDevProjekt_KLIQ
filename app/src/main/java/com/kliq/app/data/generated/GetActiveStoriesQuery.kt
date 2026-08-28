
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


public interface GetActiveStoriesQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetActiveStoriesQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val stories: List<StoriesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class StoriesItem(
  
    val id: String,
  
    val authorUserId: String,
  
    val authorName: String,
  
    val avatarUrl: String?,
  
    val imageUrl: String,
  
    val headline: String,
  
    val clubName: String?,
  
    val createdAtMs: Long,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetActiveStories"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetActiveStoriesQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetActiveStoriesQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetActiveStoriesQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetActiveStoriesQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetActiveStoriesQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetActiveStoriesQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

