
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



public interface UpsertUserMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UpsertUserMutation.Data,
      UpsertUserMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val username: String,
  
    val email: String,
  
    val isVerified: com.google.firebase.dataconnect.OptionalVariable<Boolean?>,
  
    val gender: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val age: com.google.firebase.dataconnect.OptionalVariable<Int?>,
  
    val hometown: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val profilePictureUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val bio: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val phoneNumber: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: String
        public var username: String
        public var email: String
        public var isVerified: Boolean?
        public var gender: String?
        public var age: Int?
        public var hometown: String?
        public var profilePictureUrl: String?
        public var bio: String?
        public var phoneNumber: String?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,username: String,email: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var username= username
            var email= email
            var isVerified: com.google.firebase.dataconnect.OptionalVariable<Boolean?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var gender: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var age: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var hometown: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var profilePictureUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var bio: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var phoneNumber: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var username: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { username = value_ }
              
            override var email: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { email = value_ }
              
            override var isVerified: Boolean?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { isVerified = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var gender: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { gender = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var age: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { age = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var hometown: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { hometown = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var profilePictureUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { profilePictureUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var bio: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { bio = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var phoneNumber: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { phoneNumber = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,username=username,email=email,isVerified=isVerified,gender=gender,age=age,hometown=hometown,profilePictureUrl=profilePictureUrl,bio=bio,phoneNumber=phoneNumber,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val user_upsert: UserKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpsertUser"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpsertUserMutation.ref(
  
    id: String,username: String,email: String,

  
    block_: UpsertUserMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    UpsertUserMutation.Data,
    UpsertUserMutation.Variables
  > =
  ref(
    
      UpsertUserMutation.Variables.build(
        id=id,username=username,email=email,
  
    block_
      )
    
  )

public suspend fun UpsertUserMutation.execute(

  
    
      id: String,username: String,email: String,

  
    block_: UpsertUserMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    UpsertUserMutation.Data,
    UpsertUserMutation.Variables
  > =
  ref(
    
      id=id,username=username,email=email,
  
    block_
    
  ).execute()


