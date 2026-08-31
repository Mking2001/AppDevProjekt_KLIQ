
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



public interface DeleteUserFriendshipsMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteUserFriendshipsMutation.Data,
      DeleteUserFriendshipsMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val userId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val friend_deleteMany: Int,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteUserFriendships"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteUserFriendshipsMutation.ref(
  
    userId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteUserFriendshipsMutation.Data,
    DeleteUserFriendshipsMutation.Variables
  > =
  ref(
    
      DeleteUserFriendshipsMutation.Variables(
        userId=userId,
  
      )
    
  )

public suspend fun DeleteUserFriendshipsMutation.execute(

  
    
      userId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteUserFriendshipsMutation.Data,
    DeleteUserFriendshipsMutation.Variables
  > =
  ref(
    
      userId=userId,
  
    
  ).execute()


