
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



public interface DeleteUserBlockedEntriesReverseMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteUserBlockedEntriesReverseMutation.Data,
      DeleteUserBlockedEntriesReverseMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val blockedUserId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val blockedUser_deleteMany: Int,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteUserBlockedEntriesReverse"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteUserBlockedEntriesReverseMutation.ref(
  
    blockedUserId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteUserBlockedEntriesReverseMutation.Data,
    DeleteUserBlockedEntriesReverseMutation.Variables
  > =
  ref(
    
      DeleteUserBlockedEntriesReverseMutation.Variables(
        blockedUserId=blockedUserId,
  
      )
    
  )

public suspend fun DeleteUserBlockedEntriesReverseMutation.execute(

  
    
      blockedUserId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteUserBlockedEntriesReverseMutation.Data,
    DeleteUserBlockedEntriesReverseMutation.Variables
  > =
  ref(
    
      blockedUserId=blockedUserId,
  
    
  ).execute()


