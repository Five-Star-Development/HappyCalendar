package dev.fivestar.happycalender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CalendarViewModel : ViewModel() {

    private val _openDoorEvent = MutableSharedFlow<AdventCalendarItem>()
    val openDoorEvent = _openDoorEvent.asSharedFlow()

    fun onDoorClicked(item: AdventCalendarItem) {
        if (validateItem(item)) {
            viewModelScope.launch {
                _openDoorEvent.emit(item)
            }
        }
    }

    private fun validateItem(item: AdventCalendarItem): Boolean {
        val startDate = LocalDate.of(2024, 12, 1)
        val today = LocalDate.now()
        return (item.day - 1) <= ChronoUnit.DAYS.between(startDate, today)
    }

}