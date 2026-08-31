
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



public interface DeleteUserReviewsMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteUserReviewsMutation.Data,
      DeleteUserReviewsMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val reviewerUserId: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val review_deleteMany: Int,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteUserReviews"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteUserReviewsMutation.ref(
  
    reviewerUserId: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteUserReviewsMutation.Data,
    DeleteUserReviewsMutation.Variables
  > =
  ref(
    
      DeleteUserReviewsMutation.Variables(
        reviewerUserId=reviewerUserId,
  
      )
    
  )

public suspend fun DeleteUserReviewsMutation.execute(

  
    
      reviewerUserId: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteUserReviewsMutation.Data,
    DeleteUserReviewsMutation.Variables
  > =
  ref(
    
      reviewerUserId=reviewerUserId,
  
    
  ).execute()


