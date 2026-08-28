package com.kliq.app.data.repository

import com.kliq.app.data.model.FeedComment
import com.kliq.app.data.model.FeedPost
import com.kliq.app.data.model.Story
import kotlinx.coroutines.flow.Flow

/**
 * Repository für den Home-Feed. Kapselt Beiträge, Kommentare und Storys
 * gegen die lokale Room-Datenbank.
 */
interface FeedRepository {

    /** Reaktiver Strom aller Feed-Beiträge, absteigend nach Erstellungszeit. */
    fun getFeedPosts(): Flow<List<FeedPost>>

    /** Reaktiver Strom aller Storys, ungesehene zuerst. */
    fun getStories(): Flow<List<Story>>

    /** Reaktiver Strom der Kommentare eines Beitrags. */
    fun getCommentsForPost(postId: String): Flow<List<FeedComment>>

    /**
     * Erstellt einen neuen Beitrag des aktuellen Nutzers.
     *
     * @return Der persistierte Beitrag oder ein Fehler, falls das Schreiben scheitert.
     */
    suspend fun createPost(
        authorUserId: String,
        authorName: String,
        contentText: String,
        clubId: String? = null,
        clubName: String? = null,
        imageUrl: String? = null
    ): Result<FeedPost>

    /**
     * Schaltet den Like-Zustand eines Beitrags um und aktualisiert den Zähler.
     *
     * @return Der neue Like-Zustand oder ein Fehler, falls der Beitrag nicht existiert.
     */
    suspend fun toggleLike(postId: String): Result<Boolean>

    /** Fügt einen Kommentar hinzu und aktualisiert den Kommentarzähler des Beitrags. */
    suspend fun addComment(
        postId: String,
        authorUserId: String,
        authorName: String,
        text: String
    ): Result<FeedComment>

    /** Markiert eine Story als gesehen. */
    suspend fun markStoryAsSeen(storyId: String)

    /** Entfernt einen Beitrag samt Kommentaren. */
    suspend fun deletePost(postId: String)
}
