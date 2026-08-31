
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



public interface LogVisitMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      KliqConnectorConnector,
      LogVisitMutation.Data,
      LogVisitMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String,
  
    val userId: String,
  
    val clubId: String,
  
    val clubName: String,
  
    val visitedAtTimestamp: Long,
  
    val isVerifiedByGps: Boolean,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val visitedLog_insert: VisitedLogKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "LogVisit"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun LogVisitMutation.ref(
  
    id: String,userId: String,clubId: String,clubName: String,visitedAtTimestamp: Long,isVerifiedByGps: Boolean,

  
  
): com.google.firebase.dataconnect.MutationRef<
    LogVisitMutation.Data,
    LogVisitMutation.Variables
  > =
  ref(
    
      LogVisitMutation.Variables(
        id=id,userId=userId,clubId=clubId,clubName=clubName,visitedAtTimestamp=visitedAtTimestamp,isVerifiedByGps=isVerifiedByGps,
  
      )
    
  )

public suspend fun LogVisitMutation.execute(

  
    
      id: String,userId: String,clubId: String,clubName: String,visitedAtTimestamp: Long,isVerifiedByGps: Boolean,

  

  ): com.google.firebase.dataconnect.MutationResult<
    LogVisitMutation.Data,
    LogVisitMutation.Variables
  > =
  ref(
    
      id=id,userId=userId,clubId=clubId,clubName=clubName,visitedAtTimestamp=visitedAtTimestamp,isVerifiedByGps=isVerifiedByGps,
  
    
  ).execute()


