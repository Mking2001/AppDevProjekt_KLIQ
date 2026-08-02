package com.kliq.app.data.model

enum class OfferType(val label: String) {
    SPECIAL_DEAL("Special Deal"),
    VIP_ACTION("VIP Aktion"),
    DRINK_SPECIAL("Getränke-Special"),
    ENTRY_DISCOUNT("Eintrittsrabatt")
}

enum class EventCategory(val label: String) {
    PARTY("Party"),
    LIVE_SHOW("Live Show"),
    DJ_SET("DJ Set"),
    FESTIVAL("Festival")
}

data class ClubOffer(
    val id: String,
    val clubId: String,
    val title: String,
    val description: String,
    val offerType: OfferType,
    val discountCode: String? = null,
    val discountPercentage: Int? = null,
    val validUntil: Long? = null,
    val imageUrl: String? = null,
    val termsAndConditions: String? = null,
    val isExclusive: Boolean = false
)

data class ClubEvent(
    val id: String,
    val clubId: String,
    val title: String,
    val description: String,
    val category: EventCategory,
    val startTime: Long,
    val endTime: Long,
    val price: String,
    val imageUrl: String? = null,
    val isVipEvent: Boolean = false
)
