package com.example.fitnesszone.ui.localizacion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitnesszone.R
import com.example.fitnesszone.databinding.FragmentLocalizacionBinding
import com.example.fitnesszone.ui.salas.LocalizacionViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocalizacionFragment : Fragment() {

    private var _binding: FragmentLocalizacionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalizacionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        val buttonBus: Button = root.findViewById(R.id.button_bus)
        val buttonTren: Button = root.findViewById(R.id.button_tren)

        val localizacionViewModel =
            ViewModelProvider(this).get(LocalizacionViewModel::class.java)

        localizacionViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        buttonBus.setOnClickListener {
            abrirWeb("https://siu.cmtbc.es/es/horarios_lineas_tabla.php?linea=23")
        }

        buttonTren.setOnClickListener {
            abrirWeb("https://www.renfe.com/es/es/cercanias/cercanias-cadiz/horarios")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun abrirWeb(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(36.537124, -6.201352)
        googleMap.addMarker(MarkerOptions().position(sydney).title("FitnessZone"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}