package com.desynatdealer.utils.customCalendar

data class CalendarDay(
    val day: Int,
    val isCurrentMonth: Boolean,
    val isSelected: Boolean = false,
    val isDisabled: Boolean = false
)