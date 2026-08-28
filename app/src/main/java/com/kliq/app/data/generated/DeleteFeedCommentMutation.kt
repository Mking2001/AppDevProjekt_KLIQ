
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



public interface DeleteFeedCommentMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteFeedCommentMutation.Data,
      DeleteFeedCommentMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val feedComment_delete: FeedCommentKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteFeedComment"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteFeedCommentMutation.ref(
  
    id: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteFeedCommentMutation.Data,
    DeleteFeedCommentMutation.Variables
  > =
  ref(
    
      DeleteFeedCommentMutation.Variables(
        id=id,
  
      )
    
  )

public suspend fun DeleteFeedCommentMutation.execute(

  
    
      id: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteFeedCommentMutation.Data,
    DeleteFeedCommentMutation.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


