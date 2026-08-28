
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



public interface CreateEventMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      CreateEventMutation.Data,
      CreateEventMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val clubId: String,
  
    val title: String,
  
    val description: String,
  
    val startTime: Long,
  
    val endTime: Long,
  
    val price: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val category: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: String
        public var clubId: String
        public var title: String
        public var description: String
        public var startTime: Long
        public var endTime: Long
        public var price: String?
        public var imageUrl: String?
        public var category: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,clubId: String,title: String,description: String,startTime: Long,endTime: Long,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var clubId= clubId
            var title= title
            var description= description
            var startTime= startTime
            var endTime= endTime
            var price: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var category: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var clubId: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { clubId = value_ }
              
            override var title: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { title = value_ }
              
            override var description: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { description = value_ }
              
            override var startTime: Long
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { startTime = value_ }
              
            override var endTime: Long
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { endTime = value_ }
              
            override var price: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { price = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var imageUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { imageUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var category: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { category = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,clubId=clubId,title=title,description=description,startTime=startTime,endTime=endTime,price=price,imageUrl=imageUrl,category=category,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val event_insert: EventKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateEvent"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateEventMutation.ref(
  
    id: String,clubId: String,title: String,description: String,startTime: Long,endTime: Long,

  
    block_: CreateEventMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateEventMutation.Data,
    CreateEventMutation.Variables
  > =
  ref(
    
      CreateEventMutation.Variables.build(
        id=id,clubId=clubId,title=title,description=description,startTime=startTime,endTime=endTime,
  
    block_
      )
    
  )

public suspend fun CreateEventMutation.execute(

  
    
      id: String,clubId: String,title: String,description: String,startTime: Long,endTime: Long,

  
    block_: CreateEventMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateEventMutation.Data,
    CreateEventMutation.Variables
  > =
  ref(
    
      id=id,clubId=clubId,title=title,description=description,startTime=startTime,endTime=endTime,
  
    block_
    
  ).execute()


