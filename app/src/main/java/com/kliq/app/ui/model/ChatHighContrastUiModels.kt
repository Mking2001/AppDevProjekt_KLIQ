package com.kliq.app.ui.model

import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.MessageStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ChatHighContrastPalette {
    const val PrimaryVioletAccent = "#BB86FC"
    const val DeepVioletOwnBubble = "#3B166A"
    const val DarkSurfaceReceivedBubble = "#232135"
    const val HighContrastTextPrimary = "#FFFFFF"
    const val StatusReadViolet = "#BB86FC"
    const val StatusDeliveredGreen = "#00E676"
    const val StatusSentGray = "#9E9E9E"
    const val CityTagBadgeColor = "#03DAC6"
}

data class MessageHighContrastBubbleState(
    val id: String,
    val chatId: String,
    val senderUserId: String,
    val senderName: String,
    val text: String,
    val mediaUrl: String?,
    val hasMedia: Boolean,
    val formattedTime: String,
    val timestampIso: String,
    val isMine: Boolean,
    val alignmentIsRight: Boolean,
    val bubbleBackgroundColorHex: String,
    val textColorHex: String = ChatHighContrastPalette.HighContrastTextPrimary,
    val statusIconText: String,
    val statusIconColorHex: String,
    val showSenderHeader: Boolean
)

data class ChatHighContrastHeaderState(
    val id: String,
    val title: String,
    val cityRegion: String?,
    val isPublicCityChat: Boolean,
    val avatarInitial: String,
    val avatarUrl: String?,
    val isOnline: Boolean,
    val statusSubtitle: String
)

fun ChatMessage.toHighContrastBubbleState(isGroupChat: Boolean = false): MessageHighContrastBubbleState {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeStr = timeFormat.format(Date(timestampMs))

    val (statusText, statusColor) = when (status) {
        MessageStatus.READ -> Pair("✓✓ Gelesen", ChatHighContrastPalette.StatusReadViolet)
        MessageStatus.DELIVERED -> Pair("✓✓ Empfangen", ChatHighContrastPalette.StatusDeliveredGreen)
        MessageStatus.SENT -> Pair("✓ Gesendet", ChatHighContrastPalette.StatusSentGray)
    }

    val bubbleBg = if (isMine) ChatHighContrastPalette.DeepVioletOwnBubble else ChatHighContrastPalette.DarkSurfaceReceivedBubble

    return MessageHighContrastBubbleState(
        id = id,
        chatId = chatId,
        senderUserId = senderUserId,
        senderName = senderName,
        text = text,
        mediaUrl = mediaUrl,
        hasMedia = !mediaUrl.isNullOrBlank(),
        formattedTime = timeStr,
        timestampIso = timestampIso,
        isMine = isMine,
        alignmentIsRight = isMine,
        bubbleBackgroundColorHex = bubbleBg,
        statusIconText = statusText,
        statusIconColorHex = statusColor,
        showSenderHeader = isGroupChat && !isMine
    )
}
