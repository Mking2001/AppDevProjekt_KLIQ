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

public interface GetOffersByClubQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetOffersByClubQuery.Data,
      GetOffersByClubQuery.Variables
    >
{

    @kotlinx.serialization.Serializable
  public data class Variables(

    val clubId: String,

  ) {

  }

    @kotlinx.serialization.Serializable
  public data class Data(

    val clubOffers: List<ClubOffersItem>,

  ) {

        @kotlinx.serialization.Serializable
  public data class ClubOffersItem(

    val id: String,

    val title: String,

    val description: String,

    val offerType: String,

    val discountCode: String?,

    val discountPercentage: Int?,

    val validUntil: Long?,

    val imageUrl: String?,

    val isExclusive: Boolean,

  ) {

  }

  }

  public companion object {
    public val operationName: String = "GetOffersByClub"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetOffersByClubQuery.ref(

    clubId: String,

): com.google.firebase.dataconnect.QueryRef<
    GetOffersByClubQuery.Data,
    GetOffersByClubQuery.Variables
  > =
  ref(

      GetOffersByClubQuery.Variables(
        clubId=clubId,

      )

  )

public suspend fun GetOffersByClubQuery.execute(

      clubId: String,

  ): com.google.firebase.dataconnect.QueryResult<
    GetOffersByClubQuery.Data,
    GetOffersByClubQuery.Variables
  > =
  ref(

      clubId=clubId,

  ).execute()

  public fun GetOffersByClubQuery.flow(

      clubId: String,

    ): kotlinx.coroutines.flow.Flow<GetOffersByClubQuery.Data> =
    ref(

          clubId=clubId,

      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }
