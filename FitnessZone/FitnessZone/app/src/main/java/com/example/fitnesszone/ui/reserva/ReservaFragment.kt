package com.example.fitnesszone.ui.reserva

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnesszone.MainActivity
import com.example.fitnesszone.R
import com.example.fitnesszone.databinding.FragmentReservaBinding
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.log

class ReservaFragment : Fragment(),ReservaItemClickListener {

    private var _binding: FragmentReservaBinding? = null
    private val binding get() = _binding!!

    private lateinit var miReservaViewModel: ReservaViewModel
    private lateinit var reservaAdapter: ReservaAdapter

    private val apiServicios = APIservicios()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonReserva.setOnClickListener{
            findNavController().navigate(R.id.altaFragment)
        }

        miReservaViewModel = ViewModelProvider(requireActivity()).get(ReservaViewModel::class.java)
        reservaAdapter = ReservaAdapter(emptyList(),this)

        with(binding) {


            recyclerView2.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = reservaAdapter
            }


                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val response = apiServicios.get() // Hacer una solicitud GET para obtener todas las reservas
                        val jsonArray = Json.parseToJsonElement(response.bodyAsText()).jsonArray

                        val reservas = mutableListOf<Reserva>()

                        // Iterar sobre el array JSON para obtener cada reserva
                        for (jsonObject in jsonArray) {
                            val id = jsonObject.jsonObject["_id"]?.jsonPrimitive?.content
                            val nickname = jsonObject.jsonObject["nickname"]?.jsonPrimitive?.content
                            val numCarnet = jsonObject.jsonObject["numCarnet"]?.jsonPrimitive?.int
                            val telefono = jsonObject.jsonObject["telefono"]?.jsonPrimitive?.int
                            val email = jsonObject.jsonObject["email"]?.jsonPrimitive?.content
                            val sala = jsonObject.jsonObject["sala"]?.jsonPrimitive?.content
                            val fecha = jsonObject.jsonObject["fecha"]?.jsonPrimitive?.content
                            val hora = jsonObject.jsonObject["hora"]?.jsonPrimitive?.content
                            val numPers = jsonObject.jsonObject["numPers"]?.jsonPrimitive?.int
                            val comentario = jsonObject.jsonObject["comentario"]?.jsonPrimitive?.content

                            val reserva = Reserva(id ?: "", nickname ?: "", numCarnet ?: -1, telefono ?: -1,
                                email ?: "", sala ?: "", fecha ?: "", hora ?: "", numPers ?: -1, comentario ?: "")

                            reservas.add(reserva)
                        }



                        // Actualizar el adaptador con las reservas obtenidas
                        launch(Dispatchers.Main) {
                            reservaAdapter.updateReservas(reservas)
                        }

                    } catch (e: ClientRequestException) {
                        launch(Dispatchers.Main) {
                            // Manejar errores
                        }
                    } catch (e: Exception) {
                        launch(Dispatchers.Main) {
                            // Manejar errores
                        }
                    }
                }

        }
    }
//GET ID PARA GUARDAR LOS DATOS
    override fun onReservaItemClicked(reservaId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response2 = apiServicios.getReserva(reservaId)
                val json = Json.parseToJsonElement(response2.bodyAsText())
                val id = json.jsonObject.get("_id")?.jsonPrimitive?.content
                val nickname = json.jsonObject.get("nickname")?.jsonPrimitive?.content
                val numCarnet = json.jsonObject.get("numCarnet")?.jsonPrimitive?.int
                val telefono = json.jsonObject.get("telefono")?.jsonPrimitive?.int
                val email = json.jsonObject.get("email")?.jsonPrimitive?.content
                val sala = json.jsonObject.get("sala")?.jsonPrimitive?.content
                val fecha = json.jsonObject.get("fecha")?.jsonPrimitive?.content
                val hora = json.jsonObject.get("hora")?.jsonPrimitive?.content
                val numPers = json.jsonObject.get("numPers")?.jsonPrimitive?.int
                val comentario = json.jsonObject.get("comentario")?.jsonPrimitive?.content

                launch(Dispatchers.Main) {
                    miReservaViewModel.setDatos(id, nickname, numCarnet, telefono, email, sala, fecha, hora, numPers, comentario)

                }
            } catch (e: ClientRequestException) {
                // Handle error
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
