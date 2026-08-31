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

public interface CheckUsernameQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      CheckUsernameQuery.Data,
      CheckUsernameQuery.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val username: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val users: List<UsersItem>,

  ) {

        @kotlinx.serialization.Serializable
  public data class UsersItem(

    val id: String,

    val username: String,

    val email: String,

    val phoneNumber: String?,

    val password: String?,

  ) {

  }

  }

  public companion object {
    public val operationName: String = "CheckUsername"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CheckUsernameQuery.ref(

    username: String,

): com.google.firebase.dataconnect.QueryRef<
    CheckUsernameQuery.Data,
    CheckUsernameQuery.Variables
  > =
  ref(

      CheckUsernameQuery.Variables(
        username=username,

      )

  )

public suspend fun CheckUsernameQuery.execute(

      username: String,

  ): com.google.firebase.dataconnect.QueryResult<
    CheckUsernameQuery.Data,
    CheckUsernameQuery.Variables
  > =
  ref(

      username=username,

  ).execute()

  public fun CheckUsernameQuery.flow(

      username: String,

    ): kotlinx.coroutines.flow.Flow<CheckUsernameQuery.Data> =
    ref(

          username=username,

      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }
