package com.example.fitnesszone.ui.reserva

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.fitnesszone.R
import com.example.fitnesszone.databinding.FragmentVerBinding
import com.google.android.material.snackbar.Snackbar
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerFragment : Fragment() {

    private var _binding: FragmentVerBinding? = null
    private val binding get() = _binding!!
    private val apiServicios = APIservicios()
    private lateinit var miViewModelCompartido: ReservaViewModel

    var reservaId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ViewModel compartido
        miViewModelCompartido =
            ViewModelProvider(requireActivity()).get(ReservaViewModel::class.java)

        // Observadores para actualizar la interfaz de usuario cuando los datos cambian en el ViewModel
        miViewModelCompartido.id.observe(viewLifecycleOwner) { id ->
            //Guardamos en reservaId para el Cancelar
            reservaId = id
            // Actualiza la vista textViewId con el id proporcionado
            binding.verId.text = "ID: ${id.takeIf { it != null } ?: "N/A"}"
        }

        miViewModelCompartido.nickname.observe(viewLifecycleOwner) { nickname ->
            // Actualiza la vista textViewNickname con el nickname proporcionado
            binding.verNickname.text = "Nickname: $nickname"
        }

        miViewModelCompartido.numCarnet.observe(viewLifecycleOwner) { numCarnet ->
            // Actualiza la vista textViewNumCarnet con el numCarnet proporcionado
            binding.verNumCarnet.text = "Número de Carnet: $numCarnet"
        }

        miViewModelCompartido.telefono.observe(viewLifecycleOwner) { telefono ->
            // Actualiza la vista textViewTelefono con el telefono proporcionado
            binding.verTelefono.text = "Teléfono: $telefono"
        }

        miViewModelCompartido.email.observe(viewLifecycleOwner) { email ->
            // Actualiza la vista textViewEmail con el email proporcionado
            binding.verEmail.text = "Email: $email"
        }

        miViewModelCompartido.sala.observe(viewLifecycleOwner) { sala ->
            // Actualiza la vista textViewSala con la sala proporcionada
            binding.verSala.text = "Sala: $sala"
        }

        miViewModelCompartido.fecha.observe(viewLifecycleOwner) { fecha ->
            // Actualiza la vista textViewFecha con la fecha proporcionada
            binding.verFecha.text = "Fecha: $fecha"
        }

        miViewModelCompartido.hora.observe(viewLifecycleOwner) { hora ->
            // Actualiza la vista textViewHora con la hora proporcionada
            binding.verHora.text = "Hora: $hora"
        }

        miViewModelCompartido.numPers.observe(viewLifecycleOwner) { numPers ->
            // Actualiza la vista textViewNumPers con el numPers proporcionado
            binding.verNumPers.text = "Número de Personas: $numPers"
        }

        miViewModelCompartido.comentario.observe(viewLifecycleOwner) { comentario ->
            // Actualiza la vista textViewComentario con el comentario proporcionado
            binding.verComentario.text = "Comentario: $comentario"
        }

        // Manejadores de clics para los botones
        binding.buttonModificar.setOnClickListener {
            // Implementa la lógica para modificar los datos
            it.findNavController().navigate(R.id.modificarFragment)
        }

        binding.buttonCancelar.setOnClickListener {
            reservaId?.let { id ->

                // Implementa la lógica para cancelar la acción actual
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val response = apiServicios.deleteReserva(id)
                        launch(Dispatchers.Main) {
                            // Mostrar un Snackbar con la respuesta del servidor.
                            Snackbar.make(it, response.bodyAsText(), Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK") { }
                                .show()

                        }
                        findNavController().navigate(R.id.nav_reserva)
                    } catch (e: Exception) {


                    }

                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
