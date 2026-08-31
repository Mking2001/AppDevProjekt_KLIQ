
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



public interface VoteReviewHelpfulMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      VoteReviewHelpfulMutation.Data,
      VoteReviewHelpfulMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val helpfulVotesCount: Int,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val review_update: ReviewKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "VoteReviewHelpful"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun VoteReviewHelpfulMutation.ref(
  
    id: String,helpfulVotesCount: Int,

  
  
): com.google.firebase.dataconnect.MutationRef<
    VoteReviewHelpfulMutation.Data,
    VoteReviewHelpfulMutation.Variables
  > =
  ref(
    
      VoteReviewHelpfulMutation.Variables(
        id=id,helpfulVotesCount=helpfulVotesCount,
  
      )
    
  )

public suspend fun VoteReviewHelpfulMutation.execute(

  
    
      id: String,helpfulVotesCount: Int,

  

  ): com.google.firebase.dataconnect.MutationResult<
    VoteReviewHelpfulMutation.Data,
    VoteReviewHelpfulMutation.Variables
  > =
  ref(
    
      id=id,helpfulVotesCount=helpfulVotesCount,
  
    
  ).execute()


