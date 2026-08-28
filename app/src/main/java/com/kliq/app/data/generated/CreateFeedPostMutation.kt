
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



public interface CreateFeedPostMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      CreateFeedPostMutation.Data,
      CreateFeedPostMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val authorUserId: String,
  
    val authorName: String,
  
    val authorAvatarUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val contentText: String,
  
    val imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val clubId: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val clubName: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val locationName: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val isEventPinned: com.google.firebase.dataconnect.OptionalVariable<Boolean?>,
  
    val createdAtMs: Long,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: String
        public var authorUserId: String
        public var authorName: String
        public var authorAvatarUrl: String?
        public var contentText: String
        public var imageUrl: String?
        public var clubId: String?
        public var clubName: String?
        public var locationName: String?
        public var isEventPinned: Boolean?
        public var createdAtMs: Long
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,authorUserId: String,authorName: String,contentText: String,createdAtMs: Long,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var authorUserId= authorUserId
            var authorName= authorName
            var authorAvatarUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var contentText= contentText
            var imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var clubId: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var clubName: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var locationName: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var isEventPinned: com.google.firebase.dataconnect.OptionalVariable<Boolean?> =
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
              
            override var authorAvatarUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { authorAvatarUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var contentText: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { contentText = value_ }
              
            override var imageUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { imageUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var clubId: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { clubId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var clubName: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { clubName = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var locationName: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { locationName = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var isEventPinned: Boolean?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { isEventPinned = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var createdAtMs: Long
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { createdAtMs = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,authorUserId=authorUserId,authorName=authorName,authorAvatarUrl=authorAvatarUrl,contentText=contentText,imageUrl=imageUrl,clubId=clubId,clubName=clubName,locationName=locationName,isEventPinned=isEventPinned,createdAtMs=createdAtMs,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val feedPost_insert: FeedPostKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateFeedPost"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateFeedPostMutation.ref(
  
    id: String,authorUserId: String,authorName: String,contentText: String,createdAtMs: Long,

  
    block_: CreateFeedPostMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateFeedPostMutation.Data,
    CreateFeedPostMutation.Variables
  > =
  ref(
    
      CreateFeedPostMutation.Variables.build(
        id=id,authorUserId=authorUserId,authorName=authorName,contentText=contentText,createdAtMs=createdAtMs,
  
    block_
      )
    
  )

public suspend fun CreateFeedPostMutation.execute(

  
    
      id: String,authorUserId: String,authorName: String,contentText: String,createdAtMs: Long,

  
    block_: CreateFeedPostMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateFeedPostMutation.Data,
    CreateFeedPostMutation.Variables
  > =
  ref(
    
      id=id,authorUserId=authorUserId,authorName=authorName,contentText=contentText,createdAtMs=createdAtMs,
  
    block_
    
  ).execute()


