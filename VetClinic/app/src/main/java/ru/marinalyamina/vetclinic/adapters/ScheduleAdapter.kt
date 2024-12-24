package ru.marinalyamina.vetclinic.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.models.dtos.ScheduleDTO
import java.time.LocalDateTime

class ScheduleAdapter(
    private var schedules: List<ScheduleDTO>,
    private val onScheduleClick: (ScheduleDTO) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]
        val localDateTime = LocalDateTime.parse(schedule.date)
        val time = String.format("%02d:%02d", localDateTime.hour, localDateTime.minute)
        holder.buttonSchedule.text = time

        holder.buttonSchedule.setOnClickListener {
            onScheduleClick(schedule)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSchedules(newSchedules: List<ScheduleDTO>) {
        schedules = newSchedules
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = schedules.size

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buttonSchedule: Button = itemView.findViewById(R.id.buttonSchedule)
    }
}

