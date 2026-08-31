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

public interface UpdateUserProfileMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UpdateUserProfileMutation.Data,
      UpdateUserProfileMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val id: String,

    val username: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val firstName: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val lastName: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val birthDateMs: com.google.firebase.dataconnect.OptionalVariable<Long?>,

    val age: com.google.firebase.dataconnect.OptionalVariable<Int?>,

    val gender: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val hometown: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val countryCode: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val phoneNumber: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val profilePictureUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val bio: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val password: com.google.firebase.dataconnect.OptionalVariable<String?>,

  ) {

      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: String
        public var username: String?
        public var firstName: String?
        public var lastName: String?
        public var birthDateMs: Long?
        public var age: Int?
        public var gender: String?
        public var hometown: String?
        public var countryCode: String?
        public var phoneNumber: String?
        public var profilePictureUrl: String?
        public var bio: String?
        public var password: String?

      }

      public companion object {

        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var username: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var firstName: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var lastName: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var birthDateMs: com.google.firebase.dataconnect.OptionalVariable<Long?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var age: com.google.firebase.dataconnect.OptionalVariable<Int?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var gender: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var hometown: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var countryCode: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var phoneNumber: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var profilePictureUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var bio: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var password: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }

            override var username: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { username = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var firstName: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { firstName = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var lastName: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { lastName = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var birthDateMs: Long?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { birthDateMs = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var age: Int?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { age = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var gender: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { gender = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var hometown: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { hometown = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var countryCode: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { countryCode = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var phoneNumber: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { phoneNumber = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var profilePictureUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { profilePictureUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var bio: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { bio = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var password: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { password = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

          }.apply(block_)
          .let {
            Variables(
              id=id,username=username,firstName=firstName,lastName=lastName,birthDateMs=birthDateMs,age=age,gender=gender,hometown=hometown,countryCode=countryCode,phoneNumber=phoneNumber,profilePictureUrl=profilePictureUrl,bio=bio,password=password,
            )
          }
        }
      }

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val user_update: UserKey?,

  ) {

  }

  public companion object {
    public val operationName: String = "UpdateUserProfile"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateUserProfileMutation.ref(

    id: String,

    block_: UpdateUserProfileMutation.Variables.Builder.() -> Unit = {}

): com.google.firebase.dataconnect.MutationRef<
    UpdateUserProfileMutation.Data,
    UpdateUserProfileMutation.Variables
  > =
  ref(

      UpdateUserProfileMutation.Variables.build(
        id=id,

    block_
      )

  )

public suspend fun UpdateUserProfileMutation.execute(

      id: String,

    block_: UpdateUserProfileMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    UpdateUserProfileMutation.Data,
    UpdateUserProfileMutation.Variables
  > =
  ref(

      id=id,

    block_

  ).execute()
