
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



public interface CreateClubOfferMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      CreateClubOfferMutation.Data,
      CreateClubOfferMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val clubId: String,
  
    val title: String,
  
    val description: String,
  
    val offerType: String,
  
    val discountPercentage: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val validUntil: com.google.firebase.dataconnect.OptionalVariable<Long?>,
  
    val isExclusive: com.google.firebase.dataconnect.OptionalVariable<Boolean?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: String
        public var clubId: String
        public var title: String
        public var description: String
        public var offerType: String
        public var discountPercentage: Int?
        public var validUntil: Long?
        public var isExclusive: Boolean?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,clubId: String,title: String,description: String,offerType: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var clubId= clubId
            var title= title
            var description= description
            var offerType= offerType
            var discountPercentage: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var validUntil: com.google.firebase.dataconnect.OptionalVariable<Long?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var isExclusive: com.google.firebase.dataconnect.OptionalVariable<Boolean?> =
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
              
            override var offerType: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { offerType = value_ }
              
            override var discountPercentage: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { discountPercentage = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var validUntil: Long?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { validUntil = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var isExclusive: Boolean?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { isExclusive = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,clubId=clubId,title=title,description=description,offerType=offerType,discountPercentage=discountPercentage,validUntil=validUntil,isExclusive=isExclusive,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val clubOffer_insert: ClubOfferKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateClubOffer"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateClubOfferMutation.ref(
  
    id: String,clubId: String,title: String,description: String,offerType: String,

  
    block_: CreateClubOfferMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateClubOfferMutation.Data,
    CreateClubOfferMutation.Variables
  > =
  ref(
    
      CreateClubOfferMutation.Variables.build(
        id=id,clubId=clubId,title=title,description=description,offerType=offerType,
  
    block_
      )
    
  )

public suspend fun CreateClubOfferMutation.execute(

  
    
      id: String,clubId: String,title: String,description: String,offerType: String,

  
    block_: CreateClubOfferMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    CreateClubOfferMutation.Data,
    CreateClubOfferMutation.Variables
  > =
  ref(
    
      id=id,clubId=clubId,title=title,description=description,offerType=offerType,
  
    block_
    
  ).execute()


