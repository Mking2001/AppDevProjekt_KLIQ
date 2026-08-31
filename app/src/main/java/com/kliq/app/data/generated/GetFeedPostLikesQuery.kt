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

import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map

public interface GetFeedPostLikesQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetFeedPostLikesQuery.Data,
      GetFeedPostLikesQuery.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val postId: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val feedPostLikes: List<FeedPostLikesItem>,

  ) {

        @kotlinx.serialization.Serializable
  public data class FeedPostLikesItem(

    val postId: String,

    val userId: String,

    val createdAtMs: Long,

  ) {

  }

  }

  public companion object {
    public val operationName: String = "GetFeedPostLikes"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetFeedPostLikesQuery.ref(

    postId: String,

): com.google.firebase.dataconnect.QueryRef<
    GetFeedPostLikesQuery.Data,
    GetFeedPostLikesQuery.Variables
  > =
  ref(

      GetFeedPostLikesQuery.Variables(
        postId=postId,

      )

  )

public suspend fun GetFeedPostLikesQuery.execute(

      postId: String,

  ): com.google.firebase.dataconnect.QueryResult<
    GetFeedPostLikesQuery.Data,
    GetFeedPostLikesQuery.Variables
  > =
  ref(

      postId=postId,

  ).execute()

  public fun GetFeedPostLikesQuery.flow(

      postId: String,

    ): kotlinx.coroutines.flow.Flow<GetFeedPostLikesQuery.Data> =
    ref(

          postId=postId,

      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }
