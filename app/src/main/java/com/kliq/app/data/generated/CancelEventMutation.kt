
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



public interface CancelEventMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      CancelEventMutation.Data,
      CancelEventMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val event_update: EventKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CancelEvent"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CancelEventMutation.ref(
  
    id: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    CancelEventMutation.Data,
    CancelEventMutation.Variables
  > =
  ref(
    
      CancelEventMutation.Variables(
        id=id,
  
      )
    
  )

public suspend fun CancelEventMutation.execute(

  
    
      id: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    CancelEventMutation.Data,
    CancelEventMutation.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


