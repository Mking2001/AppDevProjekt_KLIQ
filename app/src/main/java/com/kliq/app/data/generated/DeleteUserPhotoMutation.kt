
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



public interface DeleteUserPhotoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      DeleteUserPhotoMutation.Data,
      DeleteUserPhotoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val userPhoto_delete: UserPhotoKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteUserPhoto"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteUserPhotoMutation.ref(
  
    id: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteUserPhotoMutation.Data,
    DeleteUserPhotoMutation.Variables
  > =
  ref(
    
      DeleteUserPhotoMutation.Variables(
        id=id,
  
      )
    
  )

public suspend fun DeleteUserPhotoMutation.execute(

  
    
      id: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    DeleteUserPhotoMutation.Data,
    DeleteUserPhotoMutation.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


