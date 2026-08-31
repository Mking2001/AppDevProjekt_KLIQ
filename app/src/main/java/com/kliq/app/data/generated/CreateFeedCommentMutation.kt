
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



public interface CreateFeedCommentMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      CreateFeedCommentMutation.Data,
      CreateFeedCommentMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val postId: String,
  
    val authorUserId: String,
  
    val authorName: String,
  
    val text: String,
  
    val createdAtMs: Long,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val feedComment_insert: FeedCommentKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateFeedComment"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateFeedCommentMutation.ref(
  
    id: String,postId: String,authorUserId: String,authorName: String,text: String,createdAtMs: Long,

  
  
): com.google.firebase.dataconnect.MutationRef<
    CreateFeedCommentMutation.Data,
    CreateFeedCommentMutation.Variables
  > =
  ref(
    
      CreateFeedCommentMutation.Variables(
        id=id,postId=postId,authorUserId=authorUserId,authorName=authorName,text=text,createdAtMs=createdAtMs,
  
      )
    
  )

public suspend fun CreateFeedCommentMutation.execute(

  
    
      id: String,postId: String,authorUserId: String,authorName: String,text: String,createdAtMs: Long,

  

  ): com.google.firebase.dataconnect.MutationResult<
    CreateFeedCommentMutation.Data,
    CreateFeedCommentMutation.Variables
  > =
  ref(
    
      id=id,postId=postId,authorUserId=authorUserId,authorName=authorName,text=text,createdAtMs=createdAtMs,
  
    
  ).execute()


