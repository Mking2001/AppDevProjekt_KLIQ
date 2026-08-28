
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



public interface CreateChatMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      CreateChatMutation.Data,
      CreateChatMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val name: String,
  
    val chatType: String,
  
    val avatarInitial: String,
  
    val lastMessageText: String,
  
    val lastMessageTimestampMs: Long,
  
    val cityRegion: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val avatarUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: String
        public var name: String
        public var chatType: String
        public var avatarInitial: String
        public var lastMessageText: String
        public var lastMessageTimestampMs: Long
        public var cityRegion: String?
        public var avatarUrl: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,name: String,chatType: String,avatarInitial: String,lastMessageText: String,lastMessageTimestampMs: Long,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var name= name
            var chatType= chatType
            var avatarInitial= avatarInitial
            var lastMessageText= lastMessageText
            var lastMessageTimestampMs= lastMessageTimestampMs
            var cityRegion: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var avatarUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var name: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { name = value_ }
              
            override var chatType: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { chatType = value_ }
              
            override var avatarInitial: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { avatarInitial = value_ }
              
            override var lastMessageText: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { lastMessageText = value_ }
              
            override var lastMessageTimestampMs: Long
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { lastMessageTimestampMs = value_ }
              
            override var cityRegion: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { cityRegion = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var avatarUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { avatarUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,name=name,chatType=chatType,avatarInitial=avatarInitial,lastMessageText=lastMessageText,lastMessageTimestampMs=lastMessageTimestampMs,cityRegion=cityRegion,avatarUrl=avatarUrl,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val chat_insert: ChatKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateChat"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateChatMutation.ref(
  
    id: String,name: String,chatType: String,avatarInitial: String,lastMessageText: String,lastMessageTimestampMs: Long,

  
    block_: CreateChatMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateChatMutation.Data,
    CreateChatMutation.Variables
  > =
  ref(
    
      CreateChatMutation.Variables.build(
        id=id,name=name,chatType=chatType,avatarInitial=avatarInitial,lastMessageText=lastMessageText,lastMessageTimestampMs=lastMessageTimestampMs,
  
    block_
      )
    
  )

public suspend fun CreateChatMutation.execute(

  
    
      id: String,name: String,chatType: String,avatarInitial: String,lastMessageText: String,lastMessageTimestampMs: Long,

  
    block_: CreateChatMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateChatMutation.Data,
    CreateChatMutation.Variables
  > =
  ref(
    
      id=id,name=name,chatType=chatType,avatarInitial=avatarInitial,lastMessageText=lastMessageText,lastMessageTimestampMs=lastMessageTimestampMs,
  
    block_
    
  ).execute()


