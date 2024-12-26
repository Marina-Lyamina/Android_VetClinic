package ru.marinalyamina.vetclinic.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.models.entities.Animal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AnimalsAdapter(
    private val context: Context,
    private val animalList: List<Animal>,
    private val onAnimalClick: (Animal) -> Unit
) : RecyclerView.Adapter<AnimalsAdapter.AnimalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_animal_card, parent, false)
        return AnimalViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = animalList[position]

        if (animal.mainImage?.content.isNullOrBlank()) {
            // изображение по умолчанию
            holder.imageViewAnimal.setImageResource(R.drawable.animal_default)
        } else {
            try {
                val byteArray = animal.mainImage?.content?.let { Base64.decode(it, Base64.DEFAULT) }

                if (byteArray != null) {
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                    if (bitmap != null) {
                        holder.imageViewAnimal.setImageBitmap(bitmap)

                    } else {
                        holder.imageViewAnimal.setImageResource(R.drawable.animal_default)
                    }
                } else {
                    holder.imageViewAnimal.setImageResource(R.drawable.animal_default)
                }
            } catch (e: Exception) {
                holder.imageViewAnimal.setImageResource(R.drawable.animal_default)
                Log.e("AnimalDetailsFragment", "Ошибка при загрузке изображения: ${e.message}")
            }
        }

        // имя питомца
        holder.textViewAnimalName.text = animal.name

        // Обработка предстоящих приемов
        holder.textViewUpcomingSchedulesTitle.visibility = View.VISIBLE
        holder.upcomingSchedulesContainer.removeAllViews()

        if (animal.schedules.isNotEmpty()) {
            // Если есть предстоящие приемы
            for (schedule in animal.schedules) {

                val scheduleView = TextView(context).apply {
                    val localDateTime = LocalDateTime.parse(schedule.date)
                    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                    text = "${formatter.format(localDateTime)} — ${schedule.employee?.user?.shortFullName}"
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    textSize = 16f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 4)
                    }
                }
                holder.upcomingSchedulesContainer.addView(scheduleView)
            }
        } else {
            // Если нет предстоящих приемов
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
            holder.upcomingSchedulesContainer.addView(noAppointmentsMessage)
        }

        // Подробнее
        holder.buttonViewDetails.setOnClickListener {
            onAnimalClick(animal)
        }
    }

    override fun getItemCount(): Int = animalList.size

    // ViewHolder для элементов Animal
    class AnimalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewAnimal: ImageView = view.findViewById(R.id.imageViewAnimal)
        val textViewAnimalName: TextView = view.findViewById(R.id.textViewAnimalName)
        val buttonViewDetails: TextView = view.findViewById(R.id.buttonViewDetails)
        val textViewUpcomingSchedulesTitle: TextView = view.findViewById(R.id.textViewUpcomingSchedulesTitle)
        val upcomingSchedulesContainer: LinearLayout = view.findViewById(R.id.upcomingSchedulesContainer)
    }
}
