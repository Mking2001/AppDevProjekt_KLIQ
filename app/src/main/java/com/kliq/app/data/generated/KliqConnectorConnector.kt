
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

import com.google.firebase.dataconnect.getInstance as _fdcGetInstance
import kotlin.time.Duration.Companion.milliseconds as _milliseconds

public interface KliqConnectorConnector : com.google.firebase.dataconnect.generated.GeneratedConnector<KliqConnectorConnector> {
  override val dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect

  
    public val addClubHype: AddClubHypeMutation
  
    public val addFriend: AddFriendMutation
  
    public val addUserPhoto: AddUserPhotoMutation
  
    public val archiveChat: ArchiveChatMutation
  
    public val blockUser: BlockUserMutation
  
    public val cancelEvent: CancelEventMutation
  
    public val checkUsername: CheckUsernameQuery
  
    public val createChat: CreateChatMutation
  
    public val createClubOffer: CreateClubOfferMutation
  
    public val createEvent: CreateEventMutation
  
    public val createFeedComment: CreateFeedCommentMutation
  
    public val createFeedPost: CreateFeedPostMutation
  
    public val createReview: CreateReviewMutation
  
    public val createStory: CreateStoryMutation
  
    public val createUser: CreateUserMutation
  
    public val deleteFeedComment: DeleteFeedCommentMutation
  
    public val deleteFeedPost: DeleteFeedPostMutation
  
    public val deleteStory: DeleteStoryMutation
  
    public val deleteUser: DeleteUserMutation
  
    public val deleteUserPhoto: DeleteUserPhotoMutation
  
    public val getActiveStories: GetActiveStoriesQuery
  
    public val getAllChats: GetAllChatsQuery
  
    public val getAllClubs: GetAllClubsQuery
  
    public val getBlockedUsers: GetBlockedUsersQuery
  
    public val getClubById: GetClubByIdQuery
  
    public val getClubsByCity: GetClubsByCityQuery
  
    public val getClubsByRegion: GetClubsByRegionQuery
  
    public val getDirectMessages: GetDirectMessagesQuery
  
    public val getEventsByClub: GetEventsByClubQuery
  
    public val getFeedComments: GetFeedCommentsQuery
  
    public val getFeedPostLikes: GetFeedPostLikesQuery
  
    public val getFeedPosts: GetFeedPostsQuery
  
    public val getFriendsByUser: GetFriendsByUserQuery
  
    public val getMessagesByChat: GetMessagesByChatQuery
  
    public val getOffersByClub: GetOffersByClubQuery
  
    public val getReviewsByClub: GetReviewsByClubQuery
  
    public val getReviewsByUser: GetReviewsByUserQuery
  
    public val getReviewsForTargetUser: GetReviewsForTargetUserQuery
  
    public val getUserById: GetUserByIdQuery
  
    public val getUserHypesForDate: GetUserHypesForDateQuery
  
    public val getUserPhotos: GetUserPhotosQuery
  
    public val getUserPreferences: GetUserPreferencesQuery
  
    public val getVisitedLogsByUser: GetVisitedLogsByUserQuery
  
    public val likeFeedPost: LikeFeedPostMutation
  
    public val listUsers: ListUsersQuery
  
    public val logVisit: LogVisitMutation
  
    public val removeClubHype: RemoveClubHypeMutation
  
    public val removeFriend: RemoveFriendMutation
  
    public val sendDirectMessage: SendDirectMessageMutation
  
    public val sendMessage: SendMessageMutation
  
    public val toggleChatMute: ToggleChatMuteMutation
  
    public val toggleChatPin: ToggleChatPinMutation
  
    public val toggleClubFavorite: ToggleClubFavoriteMutation
  
    public val trackUserLocation: TrackUserLocationMutation
  
    public val unblockUser: UnblockUserMutation
  
    public val unlikeFeedPost: UnlikeFeedPostMutation
  
    public val updateChatLastMessage: UpdateChatLastMessageMutation
  
    public val updateClubFlames: UpdateClubFlamesMutation
  
    public val updateClubLiveStats: UpdateClubLiveStatsMutation
  
    public val updateDirectMessageStatus: UpdateDirectMessageStatusMutation
  
    public val updateFeedPost: UpdateFeedPostMutation
  
    public val updateFriendStatus: UpdateFriendStatusMutation
  
    public val updateMessageStatus: UpdateMessageStatusMutation
  
    public val updateUserProfile: UpdateUserProfileMutation
  
    public val upsertClub: UpsertClubMutation
  
    public val upsertUserPreference: UpsertUserPreferenceMutation
  
    public val voteReviewHelpful: VoteReviewHelpfulMutation
  

  public companion object {
    @Suppress("MemberVisibilityCanBePrivate")
    public val config: com.google.firebase.dataconnect.ConnectorConfig = com.google.firebase.dataconnect.ConnectorConfig(
      connector = "kliq-connector",
      location = "europe-west3",
      serviceId = "kliq-app-d215a-service",
    )

    public fun getInstance(
      dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect
    ):KliqConnectorConnector = synchronized(instances) {
      instances.getOrPut(dataConnect) {
        KliqConnectorConnectorImpl(dataConnect)
      }
    }

    private val instances = java.util.WeakHashMap<com.google.firebase.dataconnect.FirebaseDataConnect, KliqConnectorConnectorImpl>()

    
  }
}

public val KliqConnectorConnector.Companion.instance:KliqConnectorConnector
  get() = getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(
    config
  ))

public fun KliqConnectorConnector.Companion.getInstance(
  settings: com.google.firebase.dataconnect.DataConnectSettings = com.google.firebase.dataconnect.DataConnectSettings()
):KliqConnectorConnector =
  getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(config, settings))

public fun KliqConnectorConnector.Companion.getInstance(
  app: com.google.firebase.FirebaseApp,
  settings: com.google.firebase.dataconnect.DataConnectSettings = com.google.firebase.dataconnect.DataConnectSettings()
):KliqConnectorConnector =
  getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(app, config, settings))

private class KliqConnectorConnectorImpl(
  override val dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect
) : KliqConnectorConnector {
  
    override val addClubHype by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AddClubHypeMutationImpl(this)
    }
  
    override val addFriend by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AddFriendMutationImpl(this)
    }
  
    override val addUserPhoto by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AddUserPhotoMutationImpl(this)
    }
  
    override val archiveChat by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ArchiveChatMutationImpl(this)
    }
  
    override val blockUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      BlockUserMutationImpl(this)
    }
  
    override val cancelEvent by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CancelEventMutationImpl(this)
    }
  
    override val checkUsername by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CheckUsernameQueryImpl(this)
    }
  
    override val createChat by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateChatMutationImpl(this)
    }
  
    override val createClubOffer by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateClubOfferMutationImpl(this)
    }
  
    override val createEvent by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateEventMutationImpl(this)
    }
  
    override val createFeedComment by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateFeedCommentMutationImpl(this)
    }
  
    override val createFeedPost by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateFeedPostMutationImpl(this)
    }
  
    override val createReview by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateReviewMutationImpl(this)
    }
  
    override val createStory by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateStoryMutationImpl(this)
    }
  
    override val createUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateUserMutationImpl(this)
    }
  
    override val deleteFeedComment by lazy(LazyThreadSafetyMode.PUBLICATION) {
      DeleteFeedCommentMutationImpl(this)
    }
  
    override val deleteFeedPost by lazy(LazyThreadSafetyMode.PUBLICATION) {
      DeleteFeedPostMutationImpl(this)
    }
  
    override val deleteStory by lazy(LazyThreadSafetyMode.PUBLICATION) {
      DeleteStoryMutationImpl(this)
    }
  
    override val deleteUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      DeleteUserMutationImpl(this)
    }
  
    override val deleteUserPhoto by lazy(LazyThreadSafetyMode.PUBLICATION) {
      DeleteUserPhotoMutationImpl(this)
    }
  
    override val getActiveStories by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetActiveStoriesQueryImpl(this)
    }
  
    override val getAllChats by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetAllChatsQueryImpl(this)
    }
  
    override val getAllClubs by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetAllClubsQueryImpl(this)
    }
  
    override val getBlockedUsers by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetBlockedUsersQueryImpl(this)
    }
  
    override val getClubById by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetClubByIdQueryImpl(this)
    }
  
    override val getClubsByCity by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetClubsByCityQueryImpl(this)
    }
  
    override val getClubsByRegion by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetClubsByRegionQueryImpl(this)
    }
  
    override val getDirectMessages by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetDirectMessagesQueryImpl(this)
    }
  
    override val getEventsByClub by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetEventsByClubQueryImpl(this)
    }
  
    override val getFeedComments by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetFeedCommentsQueryImpl(this)
    }
  
    override val getFeedPostLikes by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetFeedPostLikesQueryImpl(this)
    }
  
    override val getFeedPosts by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetFeedPostsQueryImpl(this)
    }
  
    override val getFriendsByUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetFriendsByUserQueryImpl(this)
    }
  
    override val getMessagesByChat by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetMessagesByChatQueryImpl(this)
    }
  
    override val getOffersByClub by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetOffersByClubQueryImpl(this)
    }
  
    override val getReviewsByClub by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetReviewsByClubQueryImpl(this)
    }
  
    override val getReviewsByUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetReviewsByUserQueryImpl(this)
    }
  
    override val getReviewsForTargetUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetReviewsForTargetUserQueryImpl(this)
    }
  
    override val getUserById by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserByIdQueryImpl(this)
    }
  
    override val getUserHypesForDate by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserHypesForDateQueryImpl(this)
    }
  
    override val getUserPhotos by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserPhotosQueryImpl(this)
    }
  
    override val getUserPreferences by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserPreferencesQueryImpl(this)
    }
  
    override val getVisitedLogsByUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetVisitedLogsByUserQueryImpl(this)
    }
  
    override val likeFeedPost by lazy(LazyThreadSafetyMode.PUBLICATION) {
      LikeFeedPostMutationImpl(this)
    }
  
    override val listUsers by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListUsersQueryImpl(this)
    }
  
    override val logVisit by lazy(LazyThreadSafetyMode.PUBLICATION) {
      LogVisitMutationImpl(this)
    }
  
    override val removeClubHype by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RemoveClubHypeMutationImpl(this)
    }
  
    override val removeFriend by lazy(LazyThreadSafetyMode.PUBLICATION) {
      RemoveFriendMutationImpl(this)
    }
  
    override val sendDirectMessage by lazy(LazyThreadSafetyMode.PUBLICATION) {
      SendDirectMessageMutationImpl(this)
    }
  
    override val sendMessage by lazy(LazyThreadSafetyMode.PUBLICATION) {
      SendMessageMutationImpl(this)
    }
  
    override val toggleChatMute by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ToggleChatMuteMutationImpl(this)
    }
  
    override val toggleChatPin by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ToggleChatPinMutationImpl(this)
    }
  
    override val toggleClubFavorite by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ToggleClubFavoriteMutationImpl(this)
    }
  
    override val trackUserLocation by lazy(LazyThreadSafetyMode.PUBLICATION) {
      TrackUserLocationMutationImpl(this)
    }
  
    override val unblockUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UnblockUserMutationImpl(this)
    }
  
    override val unlikeFeedPost by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UnlikeFeedPostMutationImpl(this)
    }
  
    override val updateChatLastMessage by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpdateChatLastMessageMutationImpl(this)
    }
  
    override val updateClubFlames by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpdateClubFlamesMutationImpl(this)
    }
  
    override val updateClubLiveStats by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpdateClubLiveStatsMutationImpl(this)
    }
  
    override val updateDirectMessageStatus by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpdateDirectMessageStatusMutationImpl(this)
    }
  
    override val updateFeedPost by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpdateFeedPostMutationImpl(this)
    }
  
    override val updateFriendStatus by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpdateFriendStatusMutationImpl(this)
    }
  
    override val updateMessageStatus by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpdateMessageStatusMutationImpl(this)
    }
  
    override val updateUserProfile by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpdateUserProfileMutationImpl(this)
    }
  
    override val upsertClub by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpsertClubMutationImpl(this)
    }
  
    override val upsertUserPreference by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpsertUserPreferenceMutationImpl(this)
    }
  
    override val voteReviewHelpful by lazy(LazyThreadSafetyMode.PUBLICATION) {
      VoteReviewHelpfulMutationImpl(this)
    }
  

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun operations(): List<com.google.firebase.dataconnect.generated.GeneratedOperation<KliqConnectorConnector, *, *>> =
    queries() + mutations()

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun mutations(): List<com.google.firebase.dataconnect.generated.GeneratedMutation<KliqConnectorConnector, *, *>> =
    listOf(
      addClubHype,
        addFriend,
        addUserPhoto,
        archiveChat,
        blockUser,
        cancelEvent,
        createChat,
        createClubOffer,
        createEvent,
        createFeedComment,
        createFeedPost,
        createReview,
        createStory,
        createUser,
        deleteFeedComment,
        deleteFeedPost,
        deleteStory,
        deleteUser,
        deleteUserPhoto,
        likeFeedPost,
        logVisit,
        removeClubHype,
        removeFriend,
        sendDirectMessage,
        sendMessage,
        toggleChatMute,
        toggleChatPin,
        toggleClubFavorite,
        trackUserLocation,
        unblockUser,
        unlikeFeedPost,
        updateChatLastMessage,
        updateClubFlames,
        updateClubLiveStats,
        updateDirectMessageStatus,
        updateFeedPost,
        updateFriendStatus,
        updateMessageStatus,
        updateUserProfile,
        upsertClub,
        upsertUserPreference,
        voteReviewHelpful,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun queries(): List<com.google.firebase.dataconnect.generated.GeneratedQuery<KliqConnectorConnector, *, *>> =
    listOf(
      checkUsername,
        getActiveStories,
        getAllChats,
        getAllClubs,
        getBlockedUsers,
        getClubById,
        getClubsByCity,
        getClubsByRegion,
        getDirectMessages,
        getEventsByClub,
        getFeedComments,
        getFeedPostLikes,
        getFeedPosts,
        getFriendsByUser,
        getMessagesByChat,
        getOffersByClub,
        getReviewsByClub,
        getReviewsByUser,
        getReviewsForTargetUser,
        getUserById,
        getUserHypesForDate,
        getUserPhotos,
        getUserPreferences,
        getVisitedLogsByUser,
        listUsers,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect) =
    KliqConnectorConnectorImpl(dataConnect)

  override fun equals(other: Any?): Boolean =
    other is KliqConnectorConnectorImpl &&
    other.dataConnect == dataConnect

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "KliqConnectorConnectorImpl",
      dataConnect,
    )

  override fun toString(): String =
    "KliqConnectorConnectorImpl(dataConnect=$dataConnect)"
}



private open class KliqConnectorConnectorGeneratedQueryImpl<Data, Variables>(
  override val connector: KliqConnectorConnector,
  override val operationName: String,
  override val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
  override val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
) : com.google.firebase.dataconnect.generated.GeneratedQuery<KliqConnectorConnector, Data, Variables> {

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(
    connector: KliqConnectorConnector,
    operationName: String,
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
    variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
  ) =
    KliqConnectorConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewVariables> withVariablesSerializer(
    variablesSerializer: kotlinx.serialization.SerializationStrategy<NewVariables>
  ) =
    KliqConnectorConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewData> withDataDeserializer(
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<NewData>
  ) =
    KliqConnectorConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun equals(other: Any?): Boolean =
    other is KliqConnectorConnectorGeneratedQueryImpl<*,*> &&
    other.connector == connector &&
    other.operationName == operationName &&
    other.dataDeserializer == dataDeserializer &&
    other.variablesSerializer == variablesSerializer

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "KliqConnectorConnectorGeneratedQueryImpl",
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun toString(): String =
    "KliqConnectorConnectorGeneratedQueryImpl(" +
    "operationName=$operationName, " +
    "dataDeserializer=$dataDeserializer, " +
    "variablesSerializer=$variablesSerializer, " +
    "connector=$connector)"
}

private open class KliqConnectorConnectorGeneratedMutationImpl<Data, Variables>(
  override val connector: KliqConnectorConnector,
  override val operationName: String,
  override val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
  override val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
) : com.google.firebase.dataconnect.generated.GeneratedMutation<KliqConnectorConnector, Data, Variables> {

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(
    connector: KliqConnectorConnector,
    operationName: String,
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
    variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
  ) =
    KliqConnectorConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewVariables> withVariablesSerializer(
    variablesSerializer: kotlinx.serialization.SerializationStrategy<NewVariables>
  ) =
    KliqConnectorConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewData> withDataDeserializer(
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<NewData>
  ) =
    KliqConnectorConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun equals(other: Any?): Boolean =
    other is KliqConnectorConnectorGeneratedMutationImpl<*,*> &&
    other.connector == connector &&
    other.operationName == operationName &&
    other.dataDeserializer == dataDeserializer &&
    other.variablesSerializer == variablesSerializer

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "KliqConnectorConnectorGeneratedMutationImpl",
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun toString(): String =
    "KliqConnectorConnectorGeneratedMutationImpl(" +
    "operationName=$operationName, " +
    "dataDeserializer=$dataDeserializer, " +
    "variablesSerializer=$variablesSerializer, " +
    "connector=$connector)"
}



private class AddClubHypeMutationImpl(
  connector: KliqConnectorConnector
):
  AddClubHypeMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      AddClubHypeMutation.Data,
      AddClubHypeMutation.Variables
  >(
    connector,
    AddClubHypeMutation.Companion.operationName,
    AddClubHypeMutation.Companion.dataDeserializer,
    AddClubHypeMutation.Companion.variablesSerializer,
  )


private class AddFriendMutationImpl(
  connector: KliqConnectorConnector
):
  AddFriendMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      AddFriendMutation.Data,
      AddFriendMutation.Variables
  >(
    connector,
    AddFriendMutation.Companion.operationName,
    AddFriendMutation.Companion.dataDeserializer,
    AddFriendMutation.Companion.variablesSerializer,
  )


private class AddUserPhotoMutationImpl(
  connector: KliqConnectorConnector
):
  AddUserPhotoMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      AddUserPhotoMutation.Data,
      AddUserPhotoMutation.Variables
  >(
    connector,
    AddUserPhotoMutation.Companion.operationName,
    AddUserPhotoMutation.Companion.dataDeserializer,
    AddUserPhotoMutation.Companion.variablesSerializer,
  )


private class ArchiveChatMutationImpl(
  connector: KliqConnectorConnector
):
  ArchiveChatMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      ArchiveChatMutation.Data,
      ArchiveChatMutation.Variables
  >(
    connector,
    ArchiveChatMutation.Companion.operationName,
    ArchiveChatMutation.Companion.dataDeserializer,
    ArchiveChatMutation.Companion.variablesSerializer,
  )


private class BlockUserMutationImpl(
  connector: KliqConnectorConnector
):
  BlockUserMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      BlockUserMutation.Data,
      BlockUserMutation.Variables
  >(
    connector,
    BlockUserMutation.Companion.operationName,
    BlockUserMutation.Companion.dataDeserializer,
    BlockUserMutation.Companion.variablesSerializer,
  )


private class CancelEventMutationImpl(
  connector: KliqConnectorConnector
):
  CancelEventMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      CancelEventMutation.Data,
      CancelEventMutation.Variables
  >(
    connector,
    CancelEventMutation.Companion.operationName,
    CancelEventMutation.Companion.dataDeserializer,
    CancelEventMutation.Companion.variablesSerializer,
  )


private class CheckUsernameQueryImpl(
  connector: KliqConnectorConnector
):
  CheckUsernameQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      CheckUsernameQuery.Data,
      CheckUsernameQuery.Variables
  >(
    connector,
    CheckUsernameQuery.Companion.operationName,
    CheckUsernameQuery.Companion.dataDeserializer,
    CheckUsernameQuery.Companion.variablesSerializer,
  )


private class CreateChatMutationImpl(
  connector: KliqConnectorConnector
):
  CreateChatMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      CreateChatMutation.Data,
      CreateChatMutation.Variables
  >(
    connector,
    CreateChatMutation.Companion.operationName,
    CreateChatMutation.Companion.dataDeserializer,
    CreateChatMutation.Companion.variablesSerializer,
  )


private class CreateClubOfferMutationImpl(
  connector: KliqConnectorConnector
):
  CreateClubOfferMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      CreateClubOfferMutation.Data,
      CreateClubOfferMutation.Variables
  >(
    connector,
    CreateClubOfferMutation.Companion.operationName,
    CreateClubOfferMutation.Companion.dataDeserializer,
    CreateClubOfferMutation.Companion.variablesSerializer,
  )


private class CreateEventMutationImpl(
  connector: KliqConnectorConnector
):
  CreateEventMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      CreateEventMutation.Data,
      CreateEventMutation.Variables
  >(
    connector,
    CreateEventMutation.Companion.operationName,
    CreateEventMutation.Companion.dataDeserializer,
    CreateEventMutation.Companion.variablesSerializer,
  )


private class CreateFeedCommentMutationImpl(
  connector: KliqConnectorConnector
):
  CreateFeedCommentMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      CreateFeedCommentMutation.Data,
      CreateFeedCommentMutation.Variables
  >(
    connector,
    CreateFeedCommentMutation.Companion.operationName,
    CreateFeedCommentMutation.Companion.dataDeserializer,
    CreateFeedCommentMutation.Companion.variablesSerializer,
  )


private class CreateFeedPostMutationImpl(
  connector: KliqConnectorConnector
):
  CreateFeedPostMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      CreateFeedPostMutation.Data,
      CreateFeedPostMutation.Variables
  >(
    connector,
    CreateFeedPostMutation.Companion.operationName,
    CreateFeedPostMutation.Companion.dataDeserializer,
    CreateFeedPostMutation.Companion.variablesSerializer,
  )


private class CreateReviewMutationImpl(
  connector: KliqConnectorConnector
):
  CreateReviewMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      CreateReviewMutation.Data,
      CreateReviewMutation.Variables
  >(
    connector,
    CreateReviewMutation.Companion.operationName,
    CreateReviewMutation.Companion.dataDeserializer,
    CreateReviewMutation.Companion.variablesSerializer,
  )


private class CreateStoryMutationImpl(
  connector: KliqConnectorConnector
):
  CreateStoryMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      CreateStoryMutation.Data,
      CreateStoryMutation.Variables
  >(
    connector,
    CreateStoryMutation.Companion.operationName,
    CreateStoryMutation.Companion.dataDeserializer,
    CreateStoryMutation.Companion.variablesSerializer,
  )


private class CreateUserMutationImpl(
  connector: KliqConnectorConnector
):
  CreateUserMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      CreateUserMutation.Data,
      CreateUserMutation.Variables
  >(
    connector,
    CreateUserMutation.Companion.operationName,
    CreateUserMutation.Companion.dataDeserializer,
    CreateUserMutation.Companion.variablesSerializer,
  )


private class DeleteFeedCommentMutationImpl(
  connector: KliqConnectorConnector
):
  DeleteFeedCommentMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      DeleteFeedCommentMutation.Data,
      DeleteFeedCommentMutation.Variables
  >(
    connector,
    DeleteFeedCommentMutation.Companion.operationName,
    DeleteFeedCommentMutation.Companion.dataDeserializer,
    DeleteFeedCommentMutation.Companion.variablesSerializer,
  )


private class DeleteFeedPostMutationImpl(
  connector: KliqConnectorConnector
):
  DeleteFeedPostMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      DeleteFeedPostMutation.Data,
      DeleteFeedPostMutation.Variables
  >(
    connector,
    DeleteFeedPostMutation.Companion.operationName,
    DeleteFeedPostMutation.Companion.dataDeserializer,
    DeleteFeedPostMutation.Companion.variablesSerializer,
  )


private class DeleteStoryMutationImpl(
  connector: KliqConnectorConnector
):
  DeleteStoryMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      DeleteStoryMutation.Data,
      DeleteStoryMutation.Variables
  >(
    connector,
    DeleteStoryMutation.Companion.operationName,
    DeleteStoryMutation.Companion.dataDeserializer,
    DeleteStoryMutation.Companion.variablesSerializer,
  )


private class DeleteUserMutationImpl(
  connector: KliqConnectorConnector
):
  DeleteUserMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      DeleteUserMutation.Data,
      DeleteUserMutation.Variables
  >(
    connector,
    DeleteUserMutation.Companion.operationName,
    DeleteUserMutation.Companion.dataDeserializer,
    DeleteUserMutation.Companion.variablesSerializer,
  )


private class DeleteUserPhotoMutationImpl(
  connector: KliqConnectorConnector
):
  DeleteUserPhotoMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      DeleteUserPhotoMutation.Data,
      DeleteUserPhotoMutation.Variables
  >(
    connector,
    DeleteUserPhotoMutation.Companion.operationName,
    DeleteUserPhotoMutation.Companion.dataDeserializer,
    DeleteUserPhotoMutation.Companion.variablesSerializer,
  )


private class GetActiveStoriesQueryImpl(
  connector: KliqConnectorConnector
):
  GetActiveStoriesQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetActiveStoriesQuery.Data,
      Unit
  >(
    connector,
    GetActiveStoriesQuery.Companion.operationName,
    GetActiveStoriesQuery.Companion.dataDeserializer,
    GetActiveStoriesQuery.Companion.variablesSerializer,
  )


private class GetAllChatsQueryImpl(
  connector: KliqConnectorConnector
):
  GetAllChatsQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetAllChatsQuery.Data,
      Unit
  >(
    connector,
    GetAllChatsQuery.Companion.operationName,
    GetAllChatsQuery.Companion.dataDeserializer,
    GetAllChatsQuery.Companion.variablesSerializer,
  )


private class GetAllClubsQueryImpl(
  connector: KliqConnectorConnector
):
  GetAllClubsQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetAllClubsQuery.Data,
      Unit
  >(
    connector,
    GetAllClubsQuery.Companion.operationName,
    GetAllClubsQuery.Companion.dataDeserializer,
    GetAllClubsQuery.Companion.variablesSerializer,
  )


private class GetBlockedUsersQueryImpl(
  connector: KliqConnectorConnector
):
  GetBlockedUsersQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetBlockedUsersQuery.Data,
      GetBlockedUsersQuery.Variables
  >(
    connector,
    GetBlockedUsersQuery.Companion.operationName,
    GetBlockedUsersQuery.Companion.dataDeserializer,
    GetBlockedUsersQuery.Companion.variablesSerializer,
  )


private class GetClubByIdQueryImpl(
  connector: KliqConnectorConnector
):
  GetClubByIdQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetClubByIdQuery.Data,
      GetClubByIdQuery.Variables
  >(
    connector,
    GetClubByIdQuery.Companion.operationName,
    GetClubByIdQuery.Companion.dataDeserializer,
    GetClubByIdQuery.Companion.variablesSerializer,
  )


private class GetClubsByCityQueryImpl(
  connector: KliqConnectorConnector
):
  GetClubsByCityQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetClubsByCityQuery.Data,
      GetClubsByCityQuery.Variables
  >(
    connector,
    GetClubsByCityQuery.Companion.operationName,
    GetClubsByCityQuery.Companion.dataDeserializer,
    GetClubsByCityQuery.Companion.variablesSerializer,
  )


private class GetClubsByRegionQueryImpl(
  connector: KliqConnectorConnector
):
  GetClubsByRegionQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetClubsByRegionQuery.Data,
      GetClubsByRegionQuery.Variables
  >(
    connector,
    GetClubsByRegionQuery.Companion.operationName,
    GetClubsByRegionQuery.Companion.dataDeserializer,
    GetClubsByRegionQuery.Companion.variablesSerializer,
  )


private class GetDirectMessagesQueryImpl(
  connector: KliqConnectorConnector
):
  GetDirectMessagesQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetDirectMessagesQuery.Data,
      GetDirectMessagesQuery.Variables
  >(
    connector,
    GetDirectMessagesQuery.Companion.operationName,
    GetDirectMessagesQuery.Companion.dataDeserializer,
    GetDirectMessagesQuery.Companion.variablesSerializer,
  )


private class GetEventsByClubQueryImpl(
  connector: KliqConnectorConnector
):
  GetEventsByClubQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetEventsByClubQuery.Data,
      GetEventsByClubQuery.Variables
  >(
    connector,
    GetEventsByClubQuery.Companion.operationName,
    GetEventsByClubQuery.Companion.dataDeserializer,
    GetEventsByClubQuery.Companion.variablesSerializer,
  )


private class GetFeedCommentsQueryImpl(
  connector: KliqConnectorConnector
):
  GetFeedCommentsQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetFeedCommentsQuery.Data,
      GetFeedCommentsQuery.Variables
  >(
    connector,
    GetFeedCommentsQuery.Companion.operationName,
    GetFeedCommentsQuery.Companion.dataDeserializer,
    GetFeedCommentsQuery.Companion.variablesSerializer,
  )


private class GetFeedPostLikesQueryImpl(
  connector: KliqConnectorConnector
):
  GetFeedPostLikesQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetFeedPostLikesQuery.Data,
      GetFeedPostLikesQuery.Variables
  >(
    connector,
    GetFeedPostLikesQuery.Companion.operationName,
    GetFeedPostLikesQuery.Companion.dataDeserializer,
    GetFeedPostLikesQuery.Companion.variablesSerializer,
  )


private class GetFeedPostsQueryImpl(
  connector: KliqConnectorConnector
):
  GetFeedPostsQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetFeedPostsQuery.Data,
      Unit
  >(
    connector,
    GetFeedPostsQuery.Companion.operationName,
    GetFeedPostsQuery.Companion.dataDeserializer,
    GetFeedPostsQuery.Companion.variablesSerializer,
  )


private class GetFriendsByUserQueryImpl(
  connector: KliqConnectorConnector
):
  GetFriendsByUserQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetFriendsByUserQuery.Data,
      GetFriendsByUserQuery.Variables
  >(
    connector,
    GetFriendsByUserQuery.Companion.operationName,
    GetFriendsByUserQuery.Companion.dataDeserializer,
    GetFriendsByUserQuery.Companion.variablesSerializer,
  )


private class GetMessagesByChatQueryImpl(
  connector: KliqConnectorConnector
):
  GetMessagesByChatQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetMessagesByChatQuery.Data,
      GetMessagesByChatQuery.Variables
  >(
    connector,
    GetMessagesByChatQuery.Companion.operationName,
    GetMessagesByChatQuery.Companion.dataDeserializer,
    GetMessagesByChatQuery.Companion.variablesSerializer,
  )


private class GetOffersByClubQueryImpl(
  connector: KliqConnectorConnector
):
  GetOffersByClubQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetOffersByClubQuery.Data,
      GetOffersByClubQuery.Variables
  >(
    connector,
    GetOffersByClubQuery.Companion.operationName,
    GetOffersByClubQuery.Companion.dataDeserializer,
    GetOffersByClubQuery.Companion.variablesSerializer,
  )


private class GetReviewsByClubQueryImpl(
  connector: KliqConnectorConnector
):
  GetReviewsByClubQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetReviewsByClubQuery.Data,
      GetReviewsByClubQuery.Variables
  >(
    connector,
    GetReviewsByClubQuery.Companion.operationName,
    GetReviewsByClubQuery.Companion.dataDeserializer,
    GetReviewsByClubQuery.Companion.variablesSerializer,
  )


private class GetReviewsByUserQueryImpl(
  connector: KliqConnectorConnector
):
  GetReviewsByUserQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetReviewsByUserQuery.Data,
      GetReviewsByUserQuery.Variables
  >(
    connector,
    GetReviewsByUserQuery.Companion.operationName,
    GetReviewsByUserQuery.Companion.dataDeserializer,
    GetReviewsByUserQuery.Companion.variablesSerializer,
  )


private class GetReviewsForTargetUserQueryImpl(
  connector: KliqConnectorConnector
):
  GetReviewsForTargetUserQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetReviewsForTargetUserQuery.Data,
      GetReviewsForTargetUserQuery.Variables
  >(
    connector,
    GetReviewsForTargetUserQuery.Companion.operationName,
    GetReviewsForTargetUserQuery.Companion.dataDeserializer,
    GetReviewsForTargetUserQuery.Companion.variablesSerializer,
  )


private class GetUserByIdQueryImpl(
  connector: KliqConnectorConnector
):
  GetUserByIdQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetUserByIdQuery.Data,
      GetUserByIdQuery.Variables
  >(
    connector,
    GetUserByIdQuery.Companion.operationName,
    GetUserByIdQuery.Companion.dataDeserializer,
    GetUserByIdQuery.Companion.variablesSerializer,
  )


private class GetUserHypesForDateQueryImpl(
  connector: KliqConnectorConnector
):
  GetUserHypesForDateQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetUserHypesForDateQuery.Data,
      GetUserHypesForDateQuery.Variables
  >(
    connector,
    GetUserHypesForDateQuery.Companion.operationName,
    GetUserHypesForDateQuery.Companion.dataDeserializer,
    GetUserHypesForDateQuery.Companion.variablesSerializer,
  )


private class GetUserPhotosQueryImpl(
  connector: KliqConnectorConnector
):
  GetUserPhotosQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetUserPhotosQuery.Data,
      GetUserPhotosQuery.Variables
  >(
    connector,
    GetUserPhotosQuery.Companion.operationName,
    GetUserPhotosQuery.Companion.dataDeserializer,
    GetUserPhotosQuery.Companion.variablesSerializer,
  )


private class GetUserPreferencesQueryImpl(
  connector: KliqConnectorConnector
):
  GetUserPreferencesQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetUserPreferencesQuery.Data,
      GetUserPreferencesQuery.Variables
  >(
    connector,
    GetUserPreferencesQuery.Companion.operationName,
    GetUserPreferencesQuery.Companion.dataDeserializer,
    GetUserPreferencesQuery.Companion.variablesSerializer,
  )


private class GetVisitedLogsByUserQueryImpl(
  connector: KliqConnectorConnector
):
  GetVisitedLogsByUserQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      GetVisitedLogsByUserQuery.Data,
      GetVisitedLogsByUserQuery.Variables
  >(
    connector,
    GetVisitedLogsByUserQuery.Companion.operationName,
    GetVisitedLogsByUserQuery.Companion.dataDeserializer,
    GetVisitedLogsByUserQuery.Companion.variablesSerializer,
  )


private class LikeFeedPostMutationImpl(
  connector: KliqConnectorConnector
):
  LikeFeedPostMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      LikeFeedPostMutation.Data,
      LikeFeedPostMutation.Variables
  >(
    connector,
    LikeFeedPostMutation.Companion.operationName,
    LikeFeedPostMutation.Companion.dataDeserializer,
    LikeFeedPostMutation.Companion.variablesSerializer,
  )


private class ListUsersQueryImpl(
  connector: KliqConnectorConnector
):
  ListUsersQuery,
  KliqConnectorConnectorGeneratedQueryImpl<
      ListUsersQuery.Data,
      Unit
  >(
    connector,
    ListUsersQuery.Companion.operationName,
    ListUsersQuery.Companion.dataDeserializer,
    ListUsersQuery.Companion.variablesSerializer,
  )


private class LogVisitMutationImpl(
  connector: KliqConnectorConnector
):
  LogVisitMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      LogVisitMutation.Data,
      LogVisitMutation.Variables
  >(
    connector,
    LogVisitMutation.Companion.operationName,
    LogVisitMutation.Companion.dataDeserializer,
    LogVisitMutation.Companion.variablesSerializer,
  )


private class RemoveClubHypeMutationImpl(
  connector: KliqConnectorConnector
):
  RemoveClubHypeMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      RemoveClubHypeMutation.Data,
      RemoveClubHypeMutation.Variables
  >(
    connector,
    RemoveClubHypeMutation.Companion.operationName,
    RemoveClubHypeMutation.Companion.dataDeserializer,
    RemoveClubHypeMutation.Companion.variablesSerializer,
  )


private class RemoveFriendMutationImpl(
  connector: KliqConnectorConnector
):
  RemoveFriendMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      RemoveFriendMutation.Data,
      RemoveFriendMutation.Variables
  >(
    connector,
    RemoveFriendMutation.Companion.operationName,
    RemoveFriendMutation.Companion.dataDeserializer,
    RemoveFriendMutation.Companion.variablesSerializer,
  )


private class SendDirectMessageMutationImpl(
  connector: KliqConnectorConnector
):
  SendDirectMessageMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      SendDirectMessageMutation.Data,
      SendDirectMessageMutation.Variables
  >(
    connector,
    SendDirectMessageMutation.Companion.operationName,
    SendDirectMessageMutation.Companion.dataDeserializer,
    SendDirectMessageMutation.Companion.variablesSerializer,
  )


private class SendMessageMutationImpl(
  connector: KliqConnectorConnector
):
  SendMessageMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      SendMessageMutation.Data,
      SendMessageMutation.Variables
  >(
    connector,
    SendMessageMutation.Companion.operationName,
    SendMessageMutation.Companion.dataDeserializer,
    SendMessageMutation.Companion.variablesSerializer,
  )


private class ToggleChatMuteMutationImpl(
  connector: KliqConnectorConnector
):
  ToggleChatMuteMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      ToggleChatMuteMutation.Data,
      ToggleChatMuteMutation.Variables
  >(
    connector,
    ToggleChatMuteMutation.Companion.operationName,
    ToggleChatMuteMutation.Companion.dataDeserializer,
    ToggleChatMuteMutation.Companion.variablesSerializer,
  )


private class ToggleChatPinMutationImpl(
  connector: KliqConnectorConnector
):
  ToggleChatPinMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      ToggleChatPinMutation.Data,
      ToggleChatPinMutation.Variables
  >(
    connector,
    ToggleChatPinMutation.Companion.operationName,
    ToggleChatPinMutation.Companion.dataDeserializer,
    ToggleChatPinMutation.Companion.variablesSerializer,
  )


private class ToggleClubFavoriteMutationImpl(
  connector: KliqConnectorConnector
):
  ToggleClubFavoriteMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      ToggleClubFavoriteMutation.Data,
      ToggleClubFavoriteMutation.Variables
  >(
    connector,
    ToggleClubFavoriteMutation.Companion.operationName,
    ToggleClubFavoriteMutation.Companion.dataDeserializer,
    ToggleClubFavoriteMutation.Companion.variablesSerializer,
  )


private class TrackUserLocationMutationImpl(
  connector: KliqConnectorConnector
):
  TrackUserLocationMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      TrackUserLocationMutation.Data,
      TrackUserLocationMutation.Variables
  >(
    connector,
    TrackUserLocationMutation.Companion.operationName,
    TrackUserLocationMutation.Companion.dataDeserializer,
    TrackUserLocationMutation.Companion.variablesSerializer,
  )


private class UnblockUserMutationImpl(
  connector: KliqConnectorConnector
):
  UnblockUserMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      UnblockUserMutation.Data,
      UnblockUserMutation.Variables
  >(
    connector,
    UnblockUserMutation.Companion.operationName,
    UnblockUserMutation.Companion.dataDeserializer,
    UnblockUserMutation.Companion.variablesSerializer,
  )


private class UnlikeFeedPostMutationImpl(
  connector: KliqConnectorConnector
):
  UnlikeFeedPostMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      UnlikeFeedPostMutation.Data,
      UnlikeFeedPostMutation.Variables
  >(
    connector,
    UnlikeFeedPostMutation.Companion.operationName,
    UnlikeFeedPostMutation.Companion.dataDeserializer,
    UnlikeFeedPostMutation.Companion.variablesSerializer,
  )


private class UpdateChatLastMessageMutationImpl(
  connector: KliqConnectorConnector
):
  UpdateChatLastMessageMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      UpdateChatLastMessageMutation.Data,
      UpdateChatLastMessageMutation.Variables
  >(
    connector,
    UpdateChatLastMessageMutation.Companion.operationName,
    UpdateChatLastMessageMutation.Companion.dataDeserializer,
    UpdateChatLastMessageMutation.Companion.variablesSerializer,
  )


private class UpdateClubFlamesMutationImpl(
  connector: KliqConnectorConnector
):
  UpdateClubFlamesMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      UpdateClubFlamesMutation.Data,
      UpdateClubFlamesMutation.Variables
  >(
    connector,
    UpdateClubFlamesMutation.Companion.operationName,
    UpdateClubFlamesMutation.Companion.dataDeserializer,
    UpdateClubFlamesMutation.Companion.variablesSerializer,
  )


private class UpdateClubLiveStatsMutationImpl(
  connector: KliqConnectorConnector
):
  UpdateClubLiveStatsMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      UpdateClubLiveStatsMutation.Data,
      UpdateClubLiveStatsMutation.Variables
  >(
    connector,
    UpdateClubLiveStatsMutation.Companion.operationName,
    UpdateClubLiveStatsMutation.Companion.dataDeserializer,
    UpdateClubLiveStatsMutation.Companion.variablesSerializer,
  )


private class UpdateDirectMessageStatusMutationImpl(
  connector: KliqConnectorConnector
):
  UpdateDirectMessageStatusMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      UpdateDirectMessageStatusMutation.Data,
      UpdateDirectMessageStatusMutation.Variables
  >(
    connector,
    UpdateDirectMessageStatusMutation.Companion.operationName,
    UpdateDirectMessageStatusMutation.Companion.dataDeserializer,
    UpdateDirectMessageStatusMutation.Companion.variablesSerializer,
  )


private class UpdateFeedPostMutationImpl(
  connector: KliqConnectorConnector
):
  UpdateFeedPostMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      UpdateFeedPostMutation.Data,
      UpdateFeedPostMutation.Variables
  >(
    connector,
    UpdateFeedPostMutation.Companion.operationName,
    UpdateFeedPostMutation.Companion.dataDeserializer,
    UpdateFeedPostMutation.Companion.variablesSerializer,
  )


private class UpdateFriendStatusMutationImpl(
  connector: KliqConnectorConnector
):
  UpdateFriendStatusMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      UpdateFriendStatusMutation.Data,
      UpdateFriendStatusMutation.Variables
  >(
    connector,
    UpdateFriendStatusMutation.Companion.operationName,
    UpdateFriendStatusMutation.Companion.dataDeserializer,
    UpdateFriendStatusMutation.Companion.variablesSerializer,
  )


private class UpdateMessageStatusMutationImpl(
  connector: KliqConnectorConnector
):
  UpdateMessageStatusMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      UpdateMessageStatusMutation.Data,
      UpdateMessageStatusMutation.Variables
  >(
    connector,
    UpdateMessageStatusMutation.Companion.operationName,
    UpdateMessageStatusMutation.Companion.dataDeserializer,
    UpdateMessageStatusMutation.Companion.variablesSerializer,
  )


private class UpdateUserProfileMutationImpl(
  connector: KliqConnectorConnector
):
  UpdateUserProfileMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      UpdateUserProfileMutation.Data,
      UpdateUserProfileMutation.Variables
  >(
    connector,
    UpdateUserProfileMutation.Companion.operationName,
    UpdateUserProfileMutation.Companion.dataDeserializer,
    UpdateUserProfileMutation.Companion.variablesSerializer,
  )


private class UpsertClubMutationImpl(
  connector: KliqConnectorConnector
):
  UpsertClubMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      UpsertClubMutation.Data,
      UpsertClubMutation.Variables
  >(
    connector,
    UpsertClubMutation.Companion.operationName,
    UpsertClubMutation.Companion.dataDeserializer,
    UpsertClubMutation.Companion.variablesSerializer,
  )


private class UpsertUserPreferenceMutationImpl(
  connector: KliqConnectorConnector
):
  UpsertUserPreferenceMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      UpsertUserPreferenceMutation.Data,
      UpsertUserPreferenceMutation.Variables
  >(
    connector,
    UpsertUserPreferenceMutation.Companion.operationName,
    UpsertUserPreferenceMutation.Companion.dataDeserializer,
    UpsertUserPreferenceMutation.Companion.variablesSerializer,
  )


private class VoteReviewHelpfulMutationImpl(
  connector: KliqConnectorConnector
):
  VoteReviewHelpfulMutation,
  KliqConnectorConnectorGeneratedMutationImpl<
      VoteReviewHelpfulMutation.Data,
      VoteReviewHelpfulMutation.Variables
  >(
    connector,
    VoteReviewHelpfulMutation.Companion.operationName,
    VoteReviewHelpfulMutation.Companion.dataDeserializer,
    VoteReviewHelpfulMutation.Companion.variablesSerializer,
  )


