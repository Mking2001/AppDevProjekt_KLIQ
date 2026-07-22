package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel providing common functionalities like Loading State
 * and unified Error/UI Event handling via SharedFlow.
 */
abstract class BaseViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    /**
     * Unified exception handler for Coroutines launched in this ViewModel.
     * Automatically emits a Snackbar event on failure.
     */
    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        _isLoading.value = false
        sendEvent(UiEvent.ShowSnackbar(exception.message ?: "Ein unbekannter Fehler ist aufgetreten."))
    }

    protected fun launchWithHandling(
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            _isLoading.value = true
            block()
            _isLoading.value = false
        }
    }

    protected fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvents.emit(event)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
