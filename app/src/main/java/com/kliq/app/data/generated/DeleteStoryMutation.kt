
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



public interface DeleteStoryMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteStoryMutation.Data,
      DeleteStoryMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val story_delete: StoryKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteStory"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteStoryMutation.ref(
  
    id: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteStoryMutation.Data,
    DeleteStoryMutation.Variables
  > =
  ref(
    
      DeleteStoryMutation.Variables(
        id=id,
  
      )
    
  )

public suspend fun DeleteStoryMutation.execute(

  
    
      id: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteStoryMutation.Data,
    DeleteStoryMutation.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


