
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



public interface UpsertUserPreferenceMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      UpsertUserPreferenceMutation.Data,
      UpsertUserPreferenceMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val userId: String,
  
    val isDarkMode: Boolean,
  
    val searchRadiusKm: Int,
  
    val pushNotificationsEnabled: Boolean,
  
    val searchIntent: String,
  
    val smokingHabit: String,
  
    val drinkingHabit: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val userPreference_upsert: UserPreferenceKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpsertUserPreference"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpsertUserPreferenceMutation.ref(
  
    userId: String,isDarkMode: Boolean,searchRadiusKm: Int,pushNotificationsEnabled: Boolean,searchIntent: String,smokingHabit: String,drinkingHabit: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    UpsertUserPreferenceMutation.Data,
    UpsertUserPreferenceMutation.Variables
  > =
  ref(
    
      UpsertUserPreferenceMutation.Variables(
        userId=userId,isDarkMode=isDarkMode,searchRadiusKm=searchRadiusKm,pushNotificationsEnabled=pushNotificationsEnabled,searchIntent=searchIntent,smokingHabit=smokingHabit,drinkingHabit=drinkingHabit,
  
      )
    
  )

public suspend fun UpsertUserPreferenceMutation.execute(

  
    
      userId: String,isDarkMode: Boolean,searchRadiusKm: Int,pushNotificationsEnabled: Boolean,searchIntent: String,smokingHabit: String,drinkingHabit: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    UpsertUserPreferenceMutation.Data,
    UpsertUserPreferenceMutation.Variables
  > =
  ref(
    
      userId=userId,isDarkMode=isDarkMode,searchRadiusKm=searchRadiusKm,pushNotificationsEnabled=pushNotificationsEnabled,searchIntent=searchIntent,smokingHabit=smokingHabit,drinkingHabit=drinkingHabit,
  
    
  ).execute()


