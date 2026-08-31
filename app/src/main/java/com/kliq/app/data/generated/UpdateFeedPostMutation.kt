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

public interface UpdateFeedPostMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UpdateFeedPostMutation.Data,
      UpdateFeedPostMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val id: String,

    val contentText: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val likeCount: com.google.firebase.dataconnect.OptionalVariable<Int?>,

    val commentCount: com.google.firebase.dataconnect.OptionalVariable<Int?>,

  ) {

      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: String
        public var contentText: String?
        public var imageUrl: String?
        public var likeCount: Int?
        public var commentCount: Int?

      }

      public companion object {

        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var contentText: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var likeCount: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var commentCount: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }

            override var contentText: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { contentText = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var imageUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { imageUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var likeCount: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { likeCount = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var commentCount: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { commentCount = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

          }.apply(block_)
          .let {
            Variables(
              id=id,contentText=contentText,imageUrl=imageUrl,likeCount=likeCount,commentCount=commentCount,
            )
          }
        }
      }

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val feedPost_update: FeedPostKey?,

  ) {

  }

  public companion object {
    public val operationName: String = "UpdateFeedPost"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateFeedPostMutation.ref(

    id: String,

    block_: UpdateFeedPostMutation.Variables.Builder.() -> Unit = {}

): com.google.firebase.dataconnect.MutationRef<
    UpdateFeedPostMutation.Data,
    UpdateFeedPostMutation.Variables
  > =
  ref(

      UpdateFeedPostMutation.Variables.build(
        id=id,

    block_
      )

  )

public suspend fun UpdateFeedPostMutation.execute(

      id: String,

    block_: UpdateFeedPostMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateFeedPostMutation.Data,
    UpdateFeedPostMutation.Variables
  > =
  ref(

      id=id,

    block_

  ).execute()
