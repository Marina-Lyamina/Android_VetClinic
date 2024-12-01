package ru.marinalyamina.vetclinic.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.databinding.ActivityMainBinding
import ru.marinalyamina.vetclinic.fragments.AccountFragment
import ru.marinalyamina.vetclinic.fragments.HomeFragment
import ru.marinalyamina.vetclinic.fragments.ProceduresFragment
import ru.marinalyamina.vetclinic.fragments.VeterinariansFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val fragment = when (menuItem.itemId) {
                R.id.home -> HomeFragment()
                R.id.veterinarians -> VeterinariansFragment()
                R.id.procedures -> ProceduresFragment()
                R.id.account -> AccountFragment()
                else -> null
            }
            fragment?.let {
                replaceFragment(it)
                setGroupCheckable(true)
            }
            true
        }
    }

    private fun setGroupCheckable(enabled: Boolean) {
        binding.bottomNavigationView.menu.setGroupCheckable(0, enabled, true)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}