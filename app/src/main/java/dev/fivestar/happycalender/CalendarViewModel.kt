package dev.fivestar.happycalender

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

    private val _uiState = MutableStateFlow(UiState(items = repo.getCalendarItems()))
    val uiState = _uiState.asSharedFlow()

    fun onDoorClicked(item: AdventCalendarItem) {
        if (validateItem(item)) {
            val updatedItems = _uiState.value.items.map {
                if (it.day == item.day) it.copy(isUnlocked = true) else it
            }
            _uiState.update{ it.copy(items = updatedItems) }
        }
    }

    private fun validateItem(item: AdventCalendarItem): Boolean {
        val today = LocalDate.now()
        return (item.day - 1) <= ChronoUnit.DAYS.between(repo.getStartDate(), today)
    }

}