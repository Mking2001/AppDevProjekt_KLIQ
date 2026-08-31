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

public interface GetVisitedLogsByUserQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetVisitedLogsByUserQuery.Data,
      GetVisitedLogsByUserQuery.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val userId: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val visitedLogs: List<VisitedLogsItem>,

  ) {

        @kotlinx.serialization.Serializable
  public data class VisitedLogsItem(

    val id: String,

    val clubId: String,

    val clubName: String,

    val visitedAtTimestamp: Long,

    val isVerifiedByGps: Boolean,

  ) {

  }

  }

  public companion object {
    public val operationName: String = "GetVisitedLogsByUser"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetVisitedLogsByUserQuery.ref(

    userId: String,

): com.google.firebase.dataconnect.QueryRef<
    GetVisitedLogsByUserQuery.Data,
    GetVisitedLogsByUserQuery.Variables
  > =
  ref(

      GetVisitedLogsByUserQuery.Variables(
        userId=userId,

      )

  )

public suspend fun GetVisitedLogsByUserQuery.execute(

      userId: String,

  ): com.google.firebase.dataconnect.QueryResult<
    GetVisitedLogsByUserQuery.Data,
    GetVisitedLogsByUserQuery.Variables
  > =
  ref(

      userId=userId,

  ).execute()

  public fun GetVisitedLogsByUserQuery.flow(

      userId: String,

    ): kotlinx.coroutines.flow.Flow<GetVisitedLogsByUserQuery.Data> =
    ref(

          userId=userId,

      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }
