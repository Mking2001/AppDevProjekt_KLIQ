
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



public interface DeleteFeedPostMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteFeedPostMutation.Data,
      DeleteFeedPostMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val feedPost_delete: FeedPostKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteFeedPost"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteFeedPostMutation.ref(
  
    id: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteFeedPostMutation.Data,
    DeleteFeedPostMutation.Variables
  > =
  ref(
    
      DeleteFeedPostMutation.Variables(
        id=id,
  
      )
    
  )

public suspend fun DeleteFeedPostMutation.execute(

  
    
      id: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteFeedPostMutation.Data,
    DeleteFeedPostMutation.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


