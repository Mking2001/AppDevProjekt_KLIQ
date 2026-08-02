package com.kliq.app.viewmodel

import com.kliq.app.data.model.ClubContactInfo
import com.kliq.app.data.model.LiveOpeningStatus
import com.kliq.app.data.model.OperatingHours

data class ClubExternalInfoUiState(
    val isLoading: Boolean = false,
    val clubId: String = "",
    val clubName: String = "",
    val address: String = "",
    val websiteUrl: String? = null,
    val phoneNumber: String? = null,
    val contactEmail: String? = null,
    val operatingHours: OperatingHours = OperatingHours(),
    val liveStatus: LiveOpeningStatus = LiveOpeningStatus.CLOSED,
    val contactInfo: ClubContactInfo = ClubContactInfo(),
    val errorMessage: String? = null
)
