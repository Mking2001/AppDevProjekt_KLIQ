
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



public interface RemoveFriendMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      RemoveFriendMutation.Data,
      RemoveFriendMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val userId: String,
  
    val friendUserId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val friend_delete: FriendKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RemoveFriend"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RemoveFriendMutation.ref(
  
    userId: String,friendUserId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    RemoveFriendMutation.Data,
    RemoveFriendMutation.Variables
  > =
  ref(
    
      RemoveFriendMutation.Variables(
        userId=userId,friendUserId=friendUserId,
  
      )
    
  )

public suspend fun RemoveFriendMutation.execute(

  
    
      userId: String,friendUserId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    RemoveFriendMutation.Data,
    RemoveFriendMutation.Variables
  > =
  ref(
    
      userId=userId,friendUserId=friendUserId,
  
    
  ).execute()


