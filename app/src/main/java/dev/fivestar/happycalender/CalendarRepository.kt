package dev.fivestar.happycalender

import java.time.LocalDate

interface CalendarRepository {

    fun getCalendarItems(): List<AdventCalendarItem>

    fun getStartDate(): LocalDate

}