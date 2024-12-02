package ru.marinalyamina.vetclinic.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.adapters.ProcedureAdapter
import ru.marinalyamina.vetclinic.models.Procedure

class ProceduresFragment : Fragment(R.layout.fragment_procedures) {

    private lateinit var recyclerView: RecyclerView
    private val proceduresList = listOf(
        Procedure("Процедура 1 test test test test test test test test test", 500),
        Procedure("Процедура 2 test test test test", 1200),
        Procedure("Процедура 3", 800),
        Procedure("Процедура 4test test test", 450)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewProcedures)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = ProcedureAdapter(requireContext(), proceduresList)
    }
}
