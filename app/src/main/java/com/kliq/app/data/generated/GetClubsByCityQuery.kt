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

public interface GetClubsByCityQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetClubsByCityQuery.Data,
      GetClubsByCityQuery.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val city: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val clubs: List<ClubsItem>,

  ) {

        @kotlinx.serialization.Serializable
  public data class ClubsItem(

    val id: String,

    val name: String,

    val latitude: Double,

    val longitude: Double,

    val address: String,

    val category: String,

    val rating: Double,

    val imageUrl: String,

    val region: String,

  ) {

  }

  }

  public companion object {
    public val operationName: String = "GetClubsByCity"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetClubsByCityQuery.ref(

    city: String,

): com.google.firebase.dataconnect.QueryRef<
    GetClubsByCityQuery.Data,
    GetClubsByCityQuery.Variables
  > =
  ref(

      GetClubsByCityQuery.Variables(
        city=city,

      )

  )

public suspend fun GetClubsByCityQuery.execute(

      city: String,

  ): com.google.firebase.dataconnect.QueryResult<
    GetClubsByCityQuery.Data,
    GetClubsByCityQuery.Variables
  > =
  ref(

      city=city,

  ).execute()

  public fun GetClubsByCityQuery.flow(

      city: String,

    ): kotlinx.coroutines.flow.Flow<GetClubsByCityQuery.Data> =
    ref(

          city=city,

      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }
