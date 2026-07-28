package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.local.security.SessionStorage
import com.kliq.app.data.model.VisitedLog
import com.kliq.app.data.repository.VisitedLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(
        val logs: List<VisitedLog>,
        val totalVisitsCount: Int,
        val verifiedVisitsCount: Int
    ) : HistoryUiState
    data object Empty : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val visitedLogRepository: VisitedLogRepository,
    private val sessionStorage: SessionStorage? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun loadHistory(userId: String) {
        _uiState.value = HistoryUiState.Loading
        viewModelScope.launch {
            visitedLogRepository.getVisitedLogsForUser(userId)
                .catch { throwable ->
                    _uiState.value = HistoryUiState.Error(
                        throwable.localizedMessage ?: "Fehler beim Laden der Historie."
                    )
                }
                .collect { logs ->
                    if (logs.isEmpty()) {
                        _uiState.value = HistoryUiState.Empty
                    } else {
                        val verifiedCount = logs.count { it.isVerifiedByGps }
                        _uiState.value = HistoryUiState.Success(
                            logs = logs,
                            totalVisitsCount = logs.size,
                            verifiedVisitsCount = verifiedCount
                        )
                    }
                }
        }
    }

    fun addVisitedLog(
        userId: String,
        clubId: String,
        clubName: String,
        timestamp: Long = System.currentTimeMillis(),
        isVerifiedByGps: Boolean
    ) {
        viewModelScope.launch {
            val result = visitedLogRepository.addVisitedLog(
                userId = userId,
                clubId = clubId,
                clubName = clubName,
                visitedAtTimestamp = timestamp,
                isVerifiedByGps = isVerifiedByGps
            )
            result.onFailure { error ->
                _uiState.value = HistoryUiState.Error(
                    error.localizedMessage ?: "Eintrag konnte nicht gespeichert werden."
                )
            }
        }
    }

    fun deleteVisitedLog(logId: String, userId: String) {
        viewModelScope.launch {
            val result = visitedLogRepository.deleteVisitedLog(logId)
            result.onFailure { error ->
                _uiState.value = HistoryUiState.Error(
                    error.localizedMessage ?: "Eintrag konnte nicht gelöscht werden."
                )
            }
        }
    }

    fun clearHistory(userId: String) {
        viewModelScope.launch {
            val result = visitedLogRepository.clearVisitedLogs(userId)
            result.onSuccess {
                _uiState.value = HistoryUiState.Empty
            }.onFailure { error ->
                _uiState.value = HistoryUiState.Error(
                    error.localizedMessage ?: "Historie konnte nicht geleert werden."
                )
            }
        }
    }
}
