
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


public interface GetDirectMessagesQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetDirectMessagesQuery.Data,
      GetDirectMessagesQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val senderId: String,
  
    val receiverId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val directMessages: List<DirectMessagesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class DirectMessagesItem(
  
    val id: String,
  
    val senderId: String,
  
    val receiverId: String,
  
    val text: String,
  
    val timestamp: Long,
  
    val deliveryStatus: String,
  
    val mediaUrl: String?,
  
    val messageType: String,
  
    val caption: String?,
  
    val audioDurationMs: Long,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetDirectMessages"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetDirectMessagesQuery.ref(
  
    senderId: String,receiverId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetDirectMessagesQuery.Data,
    GetDirectMessagesQuery.Variables
  > =
  ref(
    
      GetDirectMessagesQuery.Variables(
        senderId=senderId,receiverId=receiverId,
  
      )
    
  )

public suspend fun GetDirectMessagesQuery.execute(

  
    
      senderId: String,receiverId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetDirectMessagesQuery.Data,
    GetDirectMessagesQuery.Variables
  > =
  ref(
    
      senderId=senderId,receiverId=receiverId,
  
    
  ).execute()


  public fun GetDirectMessagesQuery.flow(
    
      senderId: String,receiverId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetDirectMessagesQuery.Data> =
    ref(
        
          senderId=senderId,receiverId=receiverId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

