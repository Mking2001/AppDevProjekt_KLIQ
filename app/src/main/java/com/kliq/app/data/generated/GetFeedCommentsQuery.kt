
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


public interface GetFeedCommentsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetFeedCommentsQuery.Data,
      GetFeedCommentsQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val postId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val feedComments: List<FeedCommentsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class FeedCommentsItem(
  
    val id: String,
  
    val postId: String,
  
    val authorUserId: String,
  
    val authorName: String,
  
    val text: String,
  
    val createdAtMs: Long,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetFeedComments"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetFeedCommentsQuery.ref(
  
    postId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetFeedCommentsQuery.Data,
    GetFeedCommentsQuery.Variables
  > =
  ref(
    
      GetFeedCommentsQuery.Variables(
        postId=postId,
  
      )
    
  )

public suspend fun GetFeedCommentsQuery.execute(

  
    
      postId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetFeedCommentsQuery.Data,
    GetFeedCommentsQuery.Variables
  > =
  ref(
    
      postId=postId,
  
    
  ).execute()


  public fun GetFeedCommentsQuery.flow(
    
      postId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetFeedCommentsQuery.Data> =
    ref(
        
          postId=postId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

