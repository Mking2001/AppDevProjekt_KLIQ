
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



public interface ArchiveChatMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      ArchiveChatMutation.Data,
      ArchiveChatMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val isArchived: Boolean,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val chat_update: ChatKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ArchiveChat"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ArchiveChatMutation.ref(
  
    id: String,isArchived: Boolean,

  
  
): com.google.firebase.dataconnect.MutationRef<
    ArchiveChatMutation.Data,
    ArchiveChatMutation.Variables
  > =
  ref(
    
      ArchiveChatMutation.Variables(
        id=id,isArchived=isArchived,
  
      )
    
  )

public suspend fun ArchiveChatMutation.execute(

  
    
      id: String,isArchived: Boolean,

  

  ): com.google.firebase.dataconnect.MutationResult<
    ArchiveChatMutation.Data,
    ArchiveChatMutation.Variables
  > =
  ref(
    
      id=id,isArchived=isArchived,
  
    
  ).execute()


