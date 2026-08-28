
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


public interface GetReviewsByUserQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetReviewsByUserQuery.Data,
      GetReviewsByUserQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val userId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val reviews: List<ReviewsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ReviewsItem(
  
    val id: String,
  
    val clubId: String?,
  
    val rating: Int,
  
    val text: String,
  
    val timestamp: Long,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetReviewsByUser"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetReviewsByUserQuery.ref(
  
    userId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetReviewsByUserQuery.Data,
    GetReviewsByUserQuery.Variables
  > =
  ref(
    
      GetReviewsByUserQuery.Variables(
        userId=userId,
  
      )
    
  )

public suspend fun GetReviewsByUserQuery.execute(

  
    
      userId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetReviewsByUserQuery.Data,
    GetReviewsByUserQuery.Variables
  > =
  ref(
    
      userId=userId,
  
    
  ).execute()


  public fun GetReviewsByUserQuery.flow(
    
      userId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetReviewsByUserQuery.Data> =
    ref(
        
          userId=userId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

