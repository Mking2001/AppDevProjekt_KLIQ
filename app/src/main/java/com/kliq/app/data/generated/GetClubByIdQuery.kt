
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


public interface GetClubByIdQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      KliqConnectorConnector,
      GetClubByIdQuery.Data,
      GetClubByIdQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val club: Club?,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Club(
  
    val id: String,
  
    val name: String,
  
    val latitude: Double,
  
    val longitude: Double,
  
    val address: String,
  
    val geofenceRadiusMeters: Double,
  
    val averageRating: Double,
  
    val openingHoursJson: String,
  
    val category: String,
  
    val rating: Double,
  
    val imageUrl: String,
  
    val region: String,
  
    val city: String,
  
    val postalCode: String,
  
    val websiteUrl: String?,
  
    val phoneNumber: String?,
  
    val contactEmail: String?,
  
    val instagramHandle: String?,
  
    val currentCapacityPercent: Int,
  
    val malePercentage: Int,
  
    val femalePercentage: Int,
  
    val totalLiveVisitors: Int,
  
    val externalSearchTags: String,
  
    val isFavorite: Boolean,
  
    val isPromoted: Boolean,
  
    val flameCount: Int,
  
    val flameDate: String?,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetClubById"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetClubByIdQuery.ref(
  
    id: String,

  
  
): com.google.firebase.dataconnect.QueryRef<
    GetClubByIdQuery.Data,
    GetClubByIdQuery.Variables
  > =
  ref(
    
      GetClubByIdQuery.Variables(
        id=id,
  
      )
    
  )

public suspend fun GetClubByIdQuery.execute(

  
    
      id: String,

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetClubByIdQuery.Data,
    GetClubByIdQuery.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


  public fun GetClubByIdQuery.flow(
    
      id: String,

  
    
    ): kotlinx.coroutines.flow.Flow<GetClubByIdQuery.Data> =
    ref(
        
          id=id,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

