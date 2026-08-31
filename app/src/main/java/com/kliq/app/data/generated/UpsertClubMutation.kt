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

public interface UpsertClubMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UpsertClubMutation.Data,
      UpsertClubMutation.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val id: String,

    val name: String,

    val latitude: Double,

    val longitude: Double,

    val address: String,

    val category: String,

    val region: String,

    val city: String,

    val imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val postalCode: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val websiteUrl: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val phoneNumber: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val contactEmail: com.google.firebase.dataconnect.OptionalVariable<String?>,

    val instagramHandle: com.google.firebase.dataconnect.OptionalVariable<String?>,

  ) {

      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: String
        public var name: String
        public var latitude: Double
        public var longitude: Double
        public var address: String
        public var category: String
        public var region: String
        public var city: String
        public var imageUrl: String?
        public var postalCode: String?
        public var websiteUrl: String?
        public var phoneNumber: String?
        public var contactEmail: String?
        public var instagramHandle: String?

      }

      public companion object {

        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,name: String,latitude: Double,longitude: Double,address: String,category: String,region: String,city: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var name= name
            var latitude= latitude
            var longitude= longitude
            var address= address
            var category= category
            var region= region
            var city= city
            var imageUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var postalCode: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var websiteUrl: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var phoneNumber: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var contactEmail: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var instagramHandle: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }

            override var name: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { name = value_ }

            override var latitude: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { latitude = value_ }

            override var longitude: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { longitude = value_ }

            override var address: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { address = value_ }

            override var category: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { category = value_ }

            override var region: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { region = value_ }

            override var city: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { city = value_ }

            override var imageUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { imageUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var postalCode: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { postalCode = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var websiteUrl: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { websiteUrl = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var phoneNumber: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { phoneNumber = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var contactEmail: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { contactEmail = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

            override var instagramHandle: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { instagramHandle = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }

          }.apply(block_)
          .let {
            Variables(
              id=id,name=name,latitude=latitude,longitude=longitude,address=address,category=category,region=region,city=city,imageUrl=imageUrl,postalCode=postalCode,websiteUrl=websiteUrl,phoneNumber=phoneNumber,contactEmail=contactEmail,instagramHandle=instagramHandle,
            )
          }
        }
      }

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val club_upsert: ClubKey,

  ) {

  }

  public companion object {
    public val operationName: String = "UpsertClub"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpsertClubMutation.ref(

    id: String,name: String,latitude: Double,longitude: Double,address: String,category: String,region: String,city: String,

    block_: UpsertClubMutation.Variables.Builder.() -> Unit = {}

): com.google.firebase.dataconnect.MutationRef<
    UpsertClubMutation.Data,
    UpsertClubMutation.Variables
  > =
  ref(

      UpsertClubMutation.Variables.build(
        id=id,name=name,latitude=latitude,longitude=longitude,address=address,category=category,region=region,city=city,

    block_
      )

  )

public suspend fun UpsertClubMutation.execute(

      id: String,name: String,latitude: Double,longitude: Double,address: String,category: String,region: String,city: String,

    block_: UpsertClubMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    UpsertClubMutation.Data,
    UpsertClubMutation.Variables
  > =
  ref(

      id=id,name=name,latitude=latitude,longitude=longitude,address=address,category=category,region=region,city=city,

    block_

  ).execute()
