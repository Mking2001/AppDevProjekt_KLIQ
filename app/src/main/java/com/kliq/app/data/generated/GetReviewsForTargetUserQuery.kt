
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


public interface GetReviewsForTargetUserQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetReviewsForTargetUserQuery.Data,
      GetReviewsForTargetUserQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val targetUserId: String,
  
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
  
    val rating: Int,
  
    val text: String,
  
    val timestamp: Long,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetReviewsForTargetUser"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetReviewsForTargetUserQuery.ref(
  
    targetUserId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetReviewsForTargetUserQuery.Data,
    GetReviewsForTargetUserQuery.Variables
  > =
  ref(
    
      GetReviewsForTargetUserQuery.Variables(
        targetUserId=targetUserId,
  
      )
    
  )

public suspend fun GetReviewsForTargetUserQuery.execute(

  
    
      targetUserId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetReviewsForTargetUserQuery.Data,
    GetReviewsForTargetUserQuery.Variables
  > =
  ref(
    
      targetUserId=targetUserId,
  
    
  ).execute()


  public fun GetReviewsForTargetUserQuery.flow(
    
      targetUserId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetReviewsForTargetUserQuery.Data> =
    ref(
        
          targetUserId=targetUserId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

