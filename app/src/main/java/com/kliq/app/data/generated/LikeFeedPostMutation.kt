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

public interface LikeFeedPostMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      LikeFeedPostMutation.Data,
      LikeFeedPostMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val postId: String,

    val userId: String,

    val createdAtMs: Long,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val feedPostLike_insert: FeedPostLikeKey,

  ) {

  }

  public companion object {
    public val operationName: String = "LikeFeedPost"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun LikeFeedPostMutation.ref(

    postId: String,userId: String,createdAtMs: Long,

): com.google.firebase.dataconnect.MutationRef<
    LikeFeedPostMutation.Data,
    LikeFeedPostMutation.Variables
  > =
  ref(

      LikeFeedPostMutation.Variables(
        postId=postId,userId=userId,createdAtMs=createdAtMs,

      )

  )

public suspend fun LikeFeedPostMutation.execute(

      postId: String,userId: String,createdAtMs: Long,

  ): com.google.firebase.dataconnect.MutationResult<
    LikeFeedPostMutation.Data,
    LikeFeedPostMutation.Variables
  > =
  ref(

      postId=postId,userId=userId,createdAtMs=createdAtMs,

  ).execute()
