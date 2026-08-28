
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



public interface UnblockUserMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UnblockUserMutation.Data,
      UnblockUserMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val userId: String,
  
    val blockedUserId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val blockedUser_delete: BlockedUserKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UnblockUser"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UnblockUserMutation.ref(
  
    userId: String,blockedUserId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    UnblockUserMutation.Data,
    UnblockUserMutation.Variables
  > =
  ref(
    
      UnblockUserMutation.Variables(
        userId=userId,blockedUserId=blockedUserId,
  
      )
    
  )

public suspend fun UnblockUserMutation.execute(

  
    
      userId: String,blockedUserId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    UnblockUserMutation.Data,
    UnblockUserMutation.Variables
  > =
  ref(
    
      userId=userId,blockedUserId=blockedUserId,
  
    
  ).execute()


