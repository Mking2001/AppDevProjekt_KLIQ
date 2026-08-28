
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



public interface CreateReviewMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      CreateReviewMutation.Data,
      CreateReviewMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val reviewerUserId: String,
  
    val targetUserId: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val clubId: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val eventId: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val rating: Int,
  
    val text: String,
  
    val timestamp: Long,
  
    val reviewerUsername: String,
  
    val reviewerAvatarUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val verificationMethod: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val isVerified: com.google.firebase.dataconnect.OptionalVariable<Boolean?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: String
        public var reviewerUserId: String
        public var targetUserId: String?
        public var clubId: String?
        public var eventId: String?
        public var rating: Int
        public var text: String
        public var timestamp: Long
        public var reviewerUsername: String
        public var reviewerAvatarUrl: String?
        public var verificationMethod: String?
        public var isVerified: Boolean?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,reviewerUserId: String,rating: Int,text: String,timestamp: Long,reviewerUsername: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var reviewerUserId= reviewerUserId
            var targetUserId: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var clubId: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var eventId: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var rating= rating
            var text= text
            var timestamp= timestamp
            var reviewerUsername= reviewerUsername
            var reviewerAvatarUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var verificationMethod: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var isVerified: com.google.firebase.dataconnect.OptionalVariable<Boolean?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var reviewerUserId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { reviewerUserId = value_ }
              
            override var targetUserId: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { targetUserId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var clubId: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { clubId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var eventId: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { eventId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var rating: Int
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { rating = value_ }
              
            override var text: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { text = value_ }
              
            override var timestamp: Long
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { timestamp = value_ }
              
            override var reviewerUsername: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { reviewerUsername = value_ }
              
            override var reviewerAvatarUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { reviewerAvatarUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var verificationMethod: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { verificationMethod = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var isVerified: Boolean?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { isVerified = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,reviewerUserId=reviewerUserId,targetUserId=targetUserId,clubId=clubId,eventId=eventId,rating=rating,text=text,timestamp=timestamp,reviewerUsername=reviewerUsername,reviewerAvatarUrl=reviewerAvatarUrl,verificationMethod=verificationMethod,isVerified=isVerified,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val review_insert: ReviewKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateReview"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateReviewMutation.ref(
  
    id: String,reviewerUserId: String,rating: Int,text: String,timestamp: Long,reviewerUsername: String,

  
    block_: CreateReviewMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateReviewMutation.Data,
    CreateReviewMutation.Variables
  > =
  ref(
    
      CreateReviewMutation.Variables.build(
        id=id,reviewerUserId=reviewerUserId,rating=rating,text=text,timestamp=timestamp,reviewerUsername=reviewerUsername,
  
    block_
      )
    
  )

public suspend fun CreateReviewMutation.execute(

  
    
      id: String,reviewerUserId: String,rating: Int,text: String,timestamp: Long,reviewerUsername: String,

  
    block_: CreateReviewMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateReviewMutation.Data,
    CreateReviewMutation.Variables
  > =
  ref(
    
      id=id,reviewerUserId=reviewerUserId,rating=rating,text=text,timestamp=timestamp,reviewerUsername=reviewerUsername,
  
    block_
    
  ).execute()


