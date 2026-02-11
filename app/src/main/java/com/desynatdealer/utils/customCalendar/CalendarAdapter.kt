package com.desynatdealer.utils.customCalendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R

class CalendarAdapter(
    private val days: MutableList<CalendarDay>,
    private val calendarMonth: Int,
    private val calendarYear: Int,
    private val onDateClick: (CalendarDay) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    var selected = false

    inner class DayViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val ivCircle: ImageView = view.findViewById(R.id.ivCircle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]

        holder.ivCircle.isInvisible = true
        holder.tvDay.setTextColor(Color.BLACK)
        holder.itemView.isClickable = false
        holder.itemView.isEnabled = false

        if (day.day == 0) {
            holder.tvDay.text = ""
            holder.tvDay.isEnabled = false
            return
        }

        holder.tvDay.text = day.day.toString()
        holder.tvDay.isEnabled = day.isCurrentMonth
        holder.tvDay.alpha = if (day.isCurrentMonth) 1f else 0.3f

        if (day.isDisabled) {
            holder.tvDay.setTextColor(Color.parseColor("#C0BBBB"))
            holder.ivCircle.isInvisible = true
            holder.itemView.isClickable = false
            holder.itemView.isEnabled = false
            return
        }

        holder.itemView.isEnabled = true
        holder.itemView.isClickable = true

            if (day.isSelected) {
                    holder.ivCircle.isVisible = true
                    holder.tvDay.setTextColor(Color.parseColor("#FFFFFF"))
            }  else{
                holder.ivCircle.isInvisible = true
                holder.tvDay.setTextColor(Color.BLACK)
            }


        holder.itemView.setOnClickListener {
            selected = true
            days.forEachIndexed { i, d ->
                days[i] = d.copy(isSelected = false)
            }
            days[position] = day.copy(isSelected = true)
            notifyDataSetChanged()
            onDateClick(day)
        }
    }


    override fun getItemCount() = days.size
}
