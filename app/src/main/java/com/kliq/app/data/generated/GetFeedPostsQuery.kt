
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


public interface GetFeedPostsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetFeedPostsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val feedPosts: List<FeedPostsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class FeedPostsItem(
  
    val id: String,
  
    val authorUserId: String,
  
    val authorName: String,
  
    val authorAvatarUrl: String?,
  
    val contentText: String,
  
    val imageUrl: String?,
  
    val clubId: String?,
  
    val clubName: String?,
  
    val locationName: String?,
  
    val locationAddress: String?,
  
    val latitude: Double?,
  
    val longitude: Double?,
  
    val isEventPinned: Boolean,
  
    val isFollowersOnly: Boolean,
  
    val likeCount: Int,
  
    val commentCount: Int,
  
    val flameCount: Int,
  
    val flameDate: String?,
  
    val createdAtMs: Long,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetFeedPosts"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetFeedPostsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetFeedPostsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetFeedPostsQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetFeedPostsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetFeedPostsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetFeedPostsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

