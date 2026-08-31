
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



public interface DeleteUserPostsMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteUserPostsMutation.Data,
      DeleteUserPostsMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val authorUserId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val feedPost_deleteMany: Int,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteUserPosts"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteUserPostsMutation.ref(
  
    authorUserId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteUserPostsMutation.Data,
    DeleteUserPostsMutation.Variables
  > =
  ref(
    
      DeleteUserPostsMutation.Variables(
        authorUserId=authorUserId,
  
      )
    
  )

public suspend fun DeleteUserPostsMutation.execute(

  
    
      authorUserId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteUserPostsMutation.Data,
    DeleteUserPostsMutation.Variables
  > =
  ref(
    
      authorUserId=authorUserId,
  
    
  ).execute()


