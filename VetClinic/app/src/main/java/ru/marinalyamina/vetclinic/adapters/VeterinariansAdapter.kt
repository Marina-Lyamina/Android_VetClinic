package ru.marinalyamina.vetclinic.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.models.entities.Employee

class VeterinariansAdapter(
    private val context: Context,
    private val employeeList: List<Employee>,
    private val onEmployeeClick: (Employee) -> Unit
) : RecyclerView.Adapter<VeterinariansAdapter.VeterinarianViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VeterinarianViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false)
        return VeterinarianViewHolder(view)
    }

    override fun onBindViewHolder(holder: VeterinarianViewHolder, position: Int) {
        val employee = employeeList[position]

        holder.textViewEmployeeName.text = employee.user?.fullName

        holder.textViewEmployeePosition.text = employee.position?.name

        if (employee.mainImage?.content.isNullOrBlank()) {
            // изображение по умолчанию
            holder.imageViewEmployee.setImageResource(R.drawable.doc)
        } else {
            try {
                val byteArray = employee.mainImage?.content?.let { Base64.decode(it, Base64.DEFAULT) }

                if (byteArray != null) {
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                    if (bitmap != null) {
                        holder.imageViewEmployee.setImageBitmap(bitmap)

                    } else {
                        holder.imageViewEmployee.setImageResource(R.drawable.doc)
                    }
                } else {
                    holder.imageViewEmployee.setImageResource(R.drawable.doc)
                }
            } catch (e: Exception) {
                holder.imageViewEmployee.setImageResource(R.drawable.doc)
                Log.e("AnimalDetailsFragment", "Ошибка при загрузке изображения: ${e.message}")
            }
        }

        // подробнее
        holder.buttonViewDetails.setOnClickListener {
            onEmployeeClick(employee)
        }
    }

    override fun getItemCount(): Int = employeeList.size

    class VeterinarianViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewEmployee: ImageView = view.findViewById(R.id.imageViewEmployee)
        val textViewEmployeeName: TextView = view.findViewById(R.id.textViewEmployeeName)
        val buttonViewDetails: TextView = view.findViewById(R.id.buttonViewDetails)
        val textViewEmployeePosition: TextView = view.findViewById(R.id.textViewEmployeePosition)
    }
}
