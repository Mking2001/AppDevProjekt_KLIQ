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

public interface UnlikeFeedPostMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UnlikeFeedPostMutation.Data,
      UnlikeFeedPostMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val postId: String,

    val userId: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val feedPostLike_delete: FeedPostLikeKey?,

  ) {

  }

  public companion object {
    public val operationName: String = "UnlikeFeedPost"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UnlikeFeedPostMutation.ref(

    postId: String,userId: String,

): com.google.firebase.dataconnect.MutationRef<
    UnlikeFeedPostMutation.Data,
    UnlikeFeedPostMutation.Variables
  > =
  ref(

      UnlikeFeedPostMutation.Variables(
        postId=postId,userId=userId,

      )

  )

public suspend fun UnlikeFeedPostMutation.execute(

      postId: String,userId: String,

  ): com.google.firebase.dataconnect.MutationResult<
    UnlikeFeedPostMutation.Data,
    UnlikeFeedPostMutation.Variables
  > =
  ref(

      postId=postId,userId=userId,

  ).execute()
