package dev.fivestar.happycalender

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CalendarViewModel(val repo: CalendarRepository) : ViewModel() {

    data class UiState(
        val items: List<AdventCalendarItem>
    )

    private val _uiState = MutableStateFlow(UiState(items = emptyList()))
    val uiState = _uiState.asSharedFlow()

    init {
        Log.d("CalendarViewModel", "init")
        repo.getCalendarItems().observeForever { list ->
            _uiState.update{ it.copy(items = list) }
        }
    }

    fun onDoorClicked(item: AdventCalendarItem) {
        if (validateItem(item)) {
            repo.unlockItem(item)
        }
    }

    private fun validateItem(item: AdventCalendarItem): Boolean {
        val today = LocalDate.now()
        return (item.day - 1) <= ChronoUnit.DAYS.between(repo.getStartDate(), today)
    }

}