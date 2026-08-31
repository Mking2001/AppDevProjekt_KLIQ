
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



public interface DeleteUserBlockedEntriesMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteUserBlockedEntriesMutation.Data,
      DeleteUserBlockedEntriesMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val userId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val blockedUser_deleteMany: Int,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteUserBlockedEntries"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteUserBlockedEntriesMutation.ref(
  
    userId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteUserBlockedEntriesMutation.Data,
    DeleteUserBlockedEntriesMutation.Variables
  > =
  ref(
    
      DeleteUserBlockedEntriesMutation.Variables(
        userId=userId,
  
      )
    
  )

public suspend fun DeleteUserBlockedEntriesMutation.execute(

  
    
      userId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteUserBlockedEntriesMutation.Data,
    DeleteUserBlockedEntriesMutation.Variables
  > =
  ref(
    
      userId=userId,
  
    
  ).execute()


