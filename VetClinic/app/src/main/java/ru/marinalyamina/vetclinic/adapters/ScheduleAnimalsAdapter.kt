package ru.marinalyamina.vetclinic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.models.entities.Animal

class ScheduleAnimalsAdapter(
    private var animals: List<Animal>,
    private val onAnimalSelected: (Animal) -> Unit
) : RecyclerView.Adapter<ScheduleAnimalsAdapter.ScheduleAnimalsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleAnimalsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_animal, parent, false)
        return ScheduleAnimalsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleAnimalsViewHolder, position: Int) {
        val animal = animals[position]
        holder.bind(animal)
    }

    override fun getItemCount(): Int = animals.size


    inner class ScheduleAnimalsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val animalNameTextView: TextView = itemView.findViewById(R.id.textViewAnimalName)
        private val chooseButton: Button = itemView.findViewById(R.id.chooseButton)

        fun bind(animal: Animal) {
            animalNameTextView.text = animal.name
            chooseButton.setOnClickListener {
                onAnimalSelected(animal)
            }
        }
    }
}
