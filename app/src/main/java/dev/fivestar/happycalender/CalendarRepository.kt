package dev.fivestar.happycalender

import androidx.lifecycle.LiveData
import java.time.LocalDate

interface CalendarRepository {

    fun getCalendarItems(): LiveData<List<AdventCalendarItem>>

    fun getStartDate(): LocalDate

    fun unlockItem(item: AdventCalendarItem)

}