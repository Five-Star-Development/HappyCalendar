package dev.fivestar.happycalender

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CalendarViewModel(val repo: CalendarRepository) : ViewModel() {

    data class UiState(
        val items: List<AdventCalendarItem>,
        val annoyUser: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState(items = emptyList()))
    val uiState = _uiState.asSharedFlow()

    init {
        Log.d("CalendarViewModel", "init")
        repo.getCalendarItems().observeForever { list ->
            _uiState.update { it.copy(items = list) }
        }
    }

    fun onDoorClicked(item: AdventCalendarItem) {
        if (validateItem(item)) {
            viewModelScope.launch(Dispatchers.IO) {
                repo.unlockItem(item)
            }
        } else {
            annoyUser()
        }
    }

    private fun annoyUser() {
        _uiState.update { it.copy(annoyUser = true) }
        Handler(Looper.getMainLooper()).postDelayed({
            _uiState.update { it.copy(annoyUser = false) }
        }, 3000)
    }

    private fun validateItem(item: AdventCalendarItem): Boolean {
        val today = LocalDate.now()
        return (item.day - 1) <= ChronoUnit.DAYS.between(repo.getStartDate(), today)
    }

}