
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


public interface GetReviewsByClubQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetReviewsByClubQuery.Data,
      GetReviewsByClubQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val clubId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val reviews: List<ReviewsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ReviewsItem(
  
    val id: String,
  
    val reviewerUserId: String,
  
    val reviewerUsername: String,
  
    val reviewerAvatarUrl: String?,
  
    val rating: Int,
  
    val text: String,
  
    val timestamp: Long,
  
    val helpfulVotesCount: Int,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetReviewsByClub"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetReviewsByClubQuery.ref(
  
    clubId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetReviewsByClubQuery.Data,
    GetReviewsByClubQuery.Variables
  > =
  ref(
    
      GetReviewsByClubQuery.Variables(
        clubId=clubId,
  
      )
    
  )

public suspend fun GetReviewsByClubQuery.execute(

  
    
      clubId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetReviewsByClubQuery.Data,
    GetReviewsByClubQuery.Variables
  > =
  ref(
    
      clubId=clubId,
  
    
  ).execute()


  public fun GetReviewsByClubQuery.flow(
    
      clubId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetReviewsByClubQuery.Data> =
    ref(
        
          clubId=clubId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

