package ru.marinalyamina.vetclinic.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.models.entities.Animal
import java.text.SimpleDateFormat
import java.util.Locale


class AnimalAdapter(
    private val context: Context,
    private val animalList: List<Animal>
) : RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_animal_card, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = animalList[position]

//        if (animal.mainImage != null) {
//            Picasso.get().load(animal.mainImage).into(holder.imageViewAnimal)
//        } else {
//            holder.imageViewAnimal.setImageResource(R.drawable.cat) // изображение по умолчанию
//        }

        holder.imageViewAnimal.setImageResource(R.drawable.cat) // изображение по умолчанию

        // имя питомца
        holder.textViewAnimalName.text = animal.name

        // список предстоящих приемов
        // если есть предстоящие приемы
        if (animal.appointments.isNotEmpty()) {
            holder.textViewUpcomingAppointmentsTitle.visibility = View.VISIBLE
            holder.upcomingAppointmentsContainer.removeAllViews()

            for (appointment in animal.appointments) {
                val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                val formattedDate = dateFormatter.format(appointment.date)

                val employeeNames = appointment.employees.joinToString(", ") { it.user?.fullName ?: "Неизвестен" }
                val appointmentView = TextView(context).apply {
                    text = "${appointment.date} - $employeeNames"
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    textSize = 16f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 4)
                    }
                }
                holder.upcomingAppointmentsContainer.addView(appointmentView)
            }
        // если нет предстоящих приемов
        } else {
            holder.textViewUpcomingAppointmentsTitle.visibility = View.VISIBLE
            val noAppointmentsMessage = TextView(context).apply {
                text = "Нет предстоящих приемов"
                setTextColor(ContextCompat.getColor(context, R.color.black))
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 4)
                }
            }
            holder.upcomingAppointmentsContainer.removeAllViews()
            holder.upcomingAppointmentsContainer.addView(noAppointmentsMessage)
        }

        holder.buttonViewDetails.setOnClickListener {
            // переход к деталям питомца
        }
    }

    override fun getItemCount(): Int = animalList.size

    // ViewHolder для элементов Animal
    class AnimalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewAnimal: ImageView = view.findViewById(R.id.imageViewAnimal)
        val textViewAnimalName: TextView = view.findViewById(R.id.textViewAnimalName)
        val buttonViewDetails: TextView = view.findViewById(R.id.buttonViewDetails)
        val textViewUpcomingAppointmentsTitle: TextView = view.findViewById(R.id.textViewUpcomingAppointmentsTitle)
        val upcomingAppointmentsContainer: LinearLayout = view.findViewById(R.id.upcomingAppointmentsContainer)
    }
}




