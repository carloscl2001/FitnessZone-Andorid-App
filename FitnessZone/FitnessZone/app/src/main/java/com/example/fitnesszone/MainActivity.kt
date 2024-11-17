package com.example.fitnesszone

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.fitnesszone.databinding.ActivityMainBinding
import com.example.fitnesszone.ui.reserva.ReservaFragment
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_salas, R.id.nav_reserva,R.id.nav_localizacion
            ), drawerLayout
        )

        // Verifica si se ha pasado un nombre de fragmento como extra en el intento
        val fragmento = intent.getStringExtra("fragmento")
        if (fragmento != null && fragmento == "ReservaFragment") {
            // Abre el fragmento de reserva
            navController.navigate(R.id.nav_reserva)
        }

        //Actualiza el texto cada del widget cada minuto
        val updateRequest = PeriodicWorkRequest.Builder(WorkerActualizarWidget::class.java, 15, TimeUnit.MINUTES)
            .setInitialDelay(15, TimeUnit.MINUTES)  // El intervalo de tiempo mínimo para WorkManager es de 15 minutos.
            .build()
        WorkManager.getInstance(this).enqueue(updateRequest)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}