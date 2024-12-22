package ru.marinalyamina.vetclinic.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.models.entities.Animal
import ru.marinalyamina.vetclinic.models.entities.Employee

class VeterinariansAdapter(
    private val context: Context,
    private val employeeList: List<Employee>,
    private val onEmployeeClick: (Employee) -> Unit
) : RecyclerView.Adapter<VeterinariansAdapter.VeterinarianViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VeterinarianViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_veterinarian, parent, false)
        return VeterinarianViewHolder(view)
    }

    override fun onBindViewHolder(holder: VeterinarianViewHolder, position: Int) {
        val employee = employeeList[position]

        // ФИО
        holder.textViewEmployeeName.text = employee.user?.fullName

        // должность
        holder.textViewEmployeePosition.text = employee.position?.name

        // изображение по умолчанию
        holder.imageViewEmployee.setImageResource(R.drawable.doc)

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
