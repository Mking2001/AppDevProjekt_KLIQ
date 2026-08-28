
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


public interface GetBlockedUsersQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetBlockedUsersQuery.Data,
      GetBlockedUsersQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val userId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val blockedUsers: List<BlockedUsersItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class BlockedUsersItem(
  
    val userId: String,
  
    val blockedUserId: String,
  
    val reason: String?,
  
    val blockedAtTimestampMs: Long,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetBlockedUsers"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetBlockedUsersQuery.ref(
  
    userId: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetBlockedUsersQuery.Data,
    GetBlockedUsersQuery.Variables
  > =
  ref(
    
      GetBlockedUsersQuery.Variables(
        userId=userId,
  
      )
    
  )

public suspend fun GetBlockedUsersQuery.execute(

  
    
      userId: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetBlockedUsersQuery.Data,
    GetBlockedUsersQuery.Variables
  > =
  ref(
    
      userId=userId,
  
    
  ).execute()


  public fun GetBlockedUsersQuery.flow(
    
      userId: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetBlockedUsersQuery.Data> =
    ref(
        
          userId=userId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

