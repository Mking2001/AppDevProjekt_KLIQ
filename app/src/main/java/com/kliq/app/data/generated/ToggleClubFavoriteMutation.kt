
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



public interface ToggleClubFavoriteMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      ToggleClubFavoriteMutation.Data,
      ToggleClubFavoriteMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val isFavorite: Boolean,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val club_update: ClubKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ToggleClubFavorite"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ToggleClubFavoriteMutation.ref(
  
    id: String,isFavorite: Boolean,

  
  
): com.google.firebase.dataconnect.MutationRef<
    ToggleClubFavoriteMutation.Data,
    ToggleClubFavoriteMutation.Variables
  > =
  ref(
    
      ToggleClubFavoriteMutation.Variables(
        id=id,isFavorite=isFavorite,
  
      )
    
  )

public suspend fun ToggleClubFavoriteMutation.execute(

  
    
      id: String,isFavorite: Boolean,

  

  ): com.google.firebase.dataconnect.MutationResult<
    ToggleClubFavoriteMutation.Data,
    ToggleClubFavoriteMutation.Variables
  > =
  ref(
    
      id=id,isFavorite=isFavorite,
  
    
  ).execute()


