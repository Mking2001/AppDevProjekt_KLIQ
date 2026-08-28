
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



public interface SendMessageMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      SendMessageMutation.Data,
      SendMessageMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val chatId: String,
  
    val senderUserId: String,
  
    val senderName: String,
  
    val text: String,
  
    val timestampMs: Long,
  
    val isMine: Boolean,
  
    val messageType: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val mediaUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val thumbnailUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val caption: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val audioDurationMs: com.google.firebase.dataconnect.OptionalVariable<Long?>,
  
    val replyToMessageId: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: String
        public var chatId: String
        public var senderUserId: String
        public var senderName: String
        public var text: String
        public var timestampMs: Long
        public var isMine: Boolean
        public var messageType: String?
        public var mediaUrl: String?
        public var thumbnailUrl: String?
        public var caption: String?
        public var audioDurationMs: Long?
        public var replyToMessageId: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,chatId: String,senderUserId: String,senderName: String,text: String,timestampMs: Long,isMine: Boolean,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var chatId= chatId
            var senderUserId= senderUserId
            var senderName= senderName
            var text= text
            var timestampMs= timestampMs
            var isMine= isMine
            var messageType: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var mediaUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var thumbnailUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var caption: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var audioDurationMs: com.google.firebase.dataconnect.OptionalVariable<Long?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var replyToMessageId: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var chatId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { chatId = value_ }
              
            override var senderUserId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { senderUserId = value_ }
              
            override var senderName: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { senderName = value_ }
              
            override var text: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { text = value_ }
              
            override var timestampMs: Long
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { timestampMs = value_ }
              
            override var isMine: Boolean
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { isMine = value_ }
              
            override var messageType: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { messageType = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var mediaUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { mediaUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var thumbnailUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { thumbnailUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var caption: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { caption = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var audioDurationMs: Long?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { audioDurationMs = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var replyToMessageId: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { replyToMessageId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,chatId=chatId,senderUserId=senderUserId,senderName=senderName,text=text,timestampMs=timestampMs,isMine=isMine,messageType=messageType,mediaUrl=mediaUrl,thumbnailUrl=thumbnailUrl,caption=caption,audioDurationMs=audioDurationMs,replyToMessageId=replyToMessageId,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val message_insert: MessageKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "SendMessage"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun SendMessageMutation.ref(
  
    id: String,chatId: String,senderUserId: String,senderName: String,text: String,timestampMs: Long,isMine: Boolean,

  
    block_: SendMessageMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    SendMessageMutation.Data,
    SendMessageMutation.Variables
  > =
  ref(
    
      SendMessageMutation.Variables.build(
        id=id,chatId=chatId,senderUserId=senderUserId,senderName=senderName,text=text,timestampMs=timestampMs,isMine=isMine,
  
    block_
      )
    
  )

public suspend fun SendMessageMutation.execute(

  
    
      id: String,chatId: String,senderUserId: String,senderName: String,text: String,timestampMs: Long,isMine: Boolean,

  
    block_: SendMessageMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    SendMessageMutation.Data,
    SendMessageMutation.Variables
  > =
  ref(
    
      id=id,chatId=chatId,senderUserId=senderUserId,senderName=senderName,text=text,timestampMs=timestampMs,isMine=isMine,
  
    block_
    
  ).execute()


