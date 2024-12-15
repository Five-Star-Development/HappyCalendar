package dev.fivestar.happycalender

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

class CalendarRepositoryLocal(val sharedPreferences: SharedPreferences) : CalendarRepository {

    private val gson = Gson()

    private val items = mutableListOf(
        AdventCalendarItem(day = 1, imageResId = R.drawable.day1, isUnlocked = false),
        AdventCalendarItem(day = 2, imageResId = R.drawable.day2, isUnlocked = false),
        AdventCalendarItem(day = 3, imageResId = R.drawable.day3, isUnlocked = false),
        AdventCalendarItem(day = 4, imageResId = R.drawable.day4, isUnlocked = false),
        AdventCalendarItem(day = 5, imageResId = R.drawable.day5, isUnlocked = false),
        AdventCalendarItem(day = 6, imageResId = R.drawable.day6, isUnlocked = false),
        AdventCalendarItem(day = 7, imageResId = R.drawable.day7, isUnlocked = false),
        AdventCalendarItem(day = 8, imageResId = R.drawable.day8, isUnlocked = false),
        AdventCalendarItem(day = 9, imageResId = R.drawable.day9, isUnlocked = false),
        AdventCalendarItem(day = 10, imageResId = R.drawable.day10, isUnlocked = false),
        AdventCalendarItem(day = 11, imageResId = R.drawable.day11, isUnlocked = false),
        AdventCalendarItem(day = 12, imageResId = R.drawable.day12, isUnlocked = false),
        AdventCalendarItem(day = 13, imageResId = R.drawable.day13, isUnlocked = false),
        AdventCalendarItem(day = 14, imageResId = R.drawable.day14, isUnlocked = false),
        AdventCalendarItem(day = 15, imageResId = R.drawable.day15, isUnlocked = false),
        AdventCalendarItem(day = 16, imageResId = R.drawable.day1, isUnlocked = false),
        AdventCalendarItem(day = 17, imageResId = R.drawable.day1, isUnlocked = false),
        AdventCalendarItem(day = 18, imageResId = R.drawable.day1, isUnlocked = false),
        AdventCalendarItem(day = 19, imageResId = R.drawable.day1, isUnlocked = false),
        AdventCalendarItem(day = 20, imageResId = R.drawable.day1, isUnlocked = false),
        AdventCalendarItem(day = 21, imageResId = R.drawable.day1, isUnlocked = false),
        AdventCalendarItem(day = 22, imageResId = R.drawable.day1, isUnlocked = false),
        AdventCalendarItem(day = 23, imageResId = R.drawable.day1, isUnlocked = false),
        AdventCalendarItem(day = 24, imageResId = R.drawable.day1, isUnlocked = false)
    )

    private val _itemList = MutableLiveData<List<AdventCalendarItem>>(items.shuffled())

    override fun getCalendarItems(): LiveData<List<AdventCalendarItem>> {
        if (getStoredList().isNotEmpty()) {
            _itemList.postValue(getStoredList())
        }
        return _itemList
    }

    override fun getStartDate(): LocalDate = LocalDate.of(2024, 12, 1)

    override fun unlockItem(item: AdventCalendarItem) {
        _itemList.value?.map { listItem ->
            if (listItem.day == item.day) listItem.copy(isUnlocked = true) else listItem
        }?.let {
            items -> _itemList.postValue(items)
            storeList(items)
        }
    }

    private fun storeList(items: List<AdventCalendarItem>) {
        Log.d("CalendarRepositoryLocal", "storeList")
        val jsonList = gson.toJson(items)
        sharedPreferences.edit().putString("calendar_items", jsonList).apply()
    }

    private fun getStoredList(): List<AdventCalendarItem> {
        Log.d("CalendarRepositoryLocal", "getStoredList")
        sharedPreferences.getString("calendar_items", null)?.let { list ->
            val itemList: List<AdventCalendarItem> =
                gson.fromJson(list, object : TypeToken<List<AdventCalendarItem>>() {}.type)
            return itemList
        }
        return emptyList()
    }


}