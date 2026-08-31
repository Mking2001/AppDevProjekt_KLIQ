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

public interface CreateStoryMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      CreateStoryMutation.Data,
      CreateStoryMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val id: String,

    val authorUserId: String,

    val authorName: String,

    val avatarUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val imageUrl: String,

    val headline: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val clubName: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val createdAtMs: Long,

  ) {

      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: String
        public var authorUserId: String
        public var authorName: String
        public var avatarUrl: String?
        public var imageUrl: String
        public var headline: String?
        public var clubName: String?
        public var createdAtMs: Long

      }

      public companion object {

        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,authorUserId: String,authorName: String,imageUrl: String,createdAtMs: Long,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var authorUserId= authorUserId
            var authorName= authorName
            var avatarUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var imageUrl= imageUrl
            var headline: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var clubName: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var createdAtMs= createdAtMs

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }

            override var authorUserId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { authorUserId = value_ }

            override var authorName: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { authorName = value_ }

            override var avatarUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { avatarUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var imageUrl: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { imageUrl = value_ }

            override var headline: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { headline = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var clubName: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { clubName = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var createdAtMs: Long
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { createdAtMs = value_ }

          }.apply(block_)
          .let {
            Variables(
              id=id,authorUserId=authorUserId,authorName=authorName,avatarUrl=avatarUrl,imageUrl=imageUrl,headline=headline,clubName=clubName,createdAtMs=createdAtMs,
            )
          }
        }
      }

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val story_insert: StoryKey,

  ) {

  }

  public companion object {
    public val operationName: String = "CreateStory"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateStoryMutation.ref(

    id: String,authorUserId: String,authorName: String,imageUrl: String,createdAtMs: Long,

    block_: CreateStoryMutation.Variables.Builder.() -> Unit = {}

): com.google.firebase.dataconnect.MutationRef<
    CreateStoryMutation.Data,
    CreateStoryMutation.Variables
  > =
  ref(

      CreateStoryMutation.Variables.build(
        id=id,authorUserId=authorUserId,authorName=authorName,imageUrl=imageUrl,createdAtMs=createdAtMs,

    block_
      )

  )

public suspend fun CreateStoryMutation.execute(

      id: String,authorUserId: String,authorName: String,imageUrl: String,createdAtMs: Long,

    block_: CreateStoryMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateStoryMutation.Data,
    CreateStoryMutation.Variables
  > =
  ref(

      id=id,authorUserId=authorUserId,authorName=authorName,imageUrl=imageUrl,createdAtMs=createdAtMs,

    block_

  ).execute()
