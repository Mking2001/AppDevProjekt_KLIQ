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

import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map

public interface GetUserPreferencesQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetUserPreferencesQuery.Data,
      GetUserPreferencesQuery.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val userId: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val userPreferences: List<UserPreferencesItem>,

  ) {

        @kotlinx.serialization.Serializable
  public data class UserPreferencesItem(

    val userId: String,

    val isDarkMode: Boolean,

    val searchRadiusKm: Int,

    val pushNotificationsEnabled: Boolean,

    val searchIntent: String,

    val smokingHabit: String,

    val drinkingHabit: String,

  ) {

  }

  }

  public companion object {
    public val operationName: String = "GetUserPreferences"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetUserPreferencesQuery.ref(

    userId: String,

): com.google.firebase.dataconnect.QueryRef<
    GetUserPreferencesQuery.Data,
    GetUserPreferencesQuery.Variables
  > =
  ref(

      GetUserPreferencesQuery.Variables(
        userId=userId,

      )

  )

public suspend fun GetUserPreferencesQuery.execute(

      userId: String,

  ): com.google.firebase.dataconnect.QueryResult<
    GetUserPreferencesQuery.Data,
    GetUserPreferencesQuery.Variables
  > =
  ref(

      userId=userId,

  ).execute()

  public fun GetUserPreferencesQuery.flow(

      userId: String,

    ): kotlinx.coroutines.flow.Flow<GetUserPreferencesQuery.Data> =
    ref(

          userId=userId,

      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }
