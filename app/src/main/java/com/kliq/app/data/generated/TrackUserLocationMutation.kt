
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



public interface TrackUserLocationMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      TrackUserLocationMutation.Data,
      TrackUserLocationMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val latitude: Double,
  
    val longitude: Double,
  
    val accuracy: Double,
  
    val timestampMs: Long,
  
    val speed: com.google.firebase.dataconnect.OptionalVariable<Double?>,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      
      @BuilderDsl
      public interface Builder {
        public var id: String
        public var latitude: Double
        public var longitude: Double
        public var accuracy: Double
        public var timestampMs: Long
        public var speed: Double?
        
      }

      public companion object {
        
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: String,latitude: Double,longitude: Double,accuracy: Double,timestampMs: Long,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var latitude= latitude
            var longitude= longitude
            var accuracy= accuracy
            var timestampMs= timestampMs
            var speed: com.google.firebase.dataconnect.OptionalVariable<Double?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            

          return object : Builder {
            override var id: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var latitude: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { latitude = value_ }
              
            override var longitude: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { longitude = value_ }
              
            override var accuracy: Double
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { accuracy = value_ }
              
            override var timestampMs: Long
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { timestampMs = value_ }
              
            override var speed: Double?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { speed = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,latitude=latitude,longitude=longitude,accuracy=accuracy,timestampMs=timestampMs,speed=speed,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val userLocation_insert: UserLocationKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "TrackUserLocation"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun TrackUserLocationMutation.ref(
  
    id: String,latitude: Double,longitude: Double,accuracy: Double,timestampMs: Long,

  
    block_: TrackUserLocationMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    TrackUserLocationMutation.Data,
    TrackUserLocationMutation.Variables
  > =
  ref(
    
      TrackUserLocationMutation.Variables.build(
        id=id,latitude=latitude,longitude=longitude,accuracy=accuracy,timestampMs=timestampMs,
  
    block_
      )
    
  )

public suspend fun TrackUserLocationMutation.execute(

  
    
      id: String,latitude: Double,longitude: Double,accuracy: Double,timestampMs: Long,

  
    block_: TrackUserLocationMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    TrackUserLocationMutation.Data,
    TrackUserLocationMutation.Variables
  > =
  ref(
    
      id=id,latitude=latitude,longitude=longitude,accuracy=accuracy,timestampMs=timestampMs,
  
    block_
    
  ).execute()


