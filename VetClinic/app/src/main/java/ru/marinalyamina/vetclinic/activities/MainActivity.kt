package ru.marinalyamina.vetclinic.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import ru.marinalyamina.vetclinic.R
import ru.marinalyamina.vetclinic.databinding.ActivityMainBinding
import ru.marinalyamina.vetclinic.fragments.AccountFragment
import androidx.core.content.ContextCompat
import ru.marinalyamina.vetclinic.fragments.HomeFragment
import ru.marinalyamina.vetclinic.fragments.ProceduresFragment
import ru.marinalyamina.vetclinic.fragments.VeterinariansFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

//        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            updateToolbarTitle(R.string.title_home)
        }

        setupBottomNavigation()

//        binding.buttonGoToOtherActivity.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val fragment = when (menuItem.itemId) {
                R.id.home -> {
                    updateToolbarTitle(R.string.title_home)
                    HomeFragment()
                }
                R.id.veterinarians -> {
                    updateToolbarTitle(R.string.title_veterinarians)
                    VeterinariansFragment()
                }
                R.id.procedures -> {
                    updateToolbarTitle(R.string.title_procedures)
                    ProceduresFragment()
                }
                R.id.account -> {
                    updateToolbarTitle(R.string.title_account)
                    AccountFragment()
                }
                else -> null
            }
            fragment?.let {
                replaceFragment(it)
            }
            true
        }
    }

    private fun updateToolbarTitle(titleResId: Int) {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = getString(titleResId)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commitAllowingStateLoss()
    }
}
