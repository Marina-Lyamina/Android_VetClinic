package ru.marinalyamina.vetclinic.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.activities.LoginActivity
import ru.marinalyamina.vetclinic.databinding.FragmentAccountBinding

class AccountFragment : Fragment(R.layout.fragment_account) {

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        /*binding.buttonGoToOtherActivity.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }*/

        return binding.root
    }
}
