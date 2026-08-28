
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


public interface GetUserPhotosQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetUserPhotosQuery.Data,
      GetUserPhotosQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val userId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val userPhotos: List<UserPhotosItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class UserPhotosItem(
  
    val id: String,
  
    val userId: String,
  
    val imageUrl: String,
  
    val displayOrder: Int,
  
    val createdAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetUserPhotos"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetUserPhotosQuery.ref(
  
    userId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetUserPhotosQuery.Data,
    GetUserPhotosQuery.Variables
  > =
  ref(
    
      GetUserPhotosQuery.Variables(
        userId=userId,
  
      )
    
  )

public suspend fun GetUserPhotosQuery.execute(

  
    
      userId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetUserPhotosQuery.Data,
    GetUserPhotosQuery.Variables
  > =
  ref(
    
      userId=userId,
  
    
  ).execute()


  public fun GetUserPhotosQuery.flow(
    
      userId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetUserPhotosQuery.Data> =
    ref(
        
          userId=userId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

