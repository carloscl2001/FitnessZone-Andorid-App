package com.example.fitnesszone.ui.reserva

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import com.example.fitnesszone.databinding.FragmentAltaBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class AltaFragment : Fragment() {

    // Binding para acceder a las vistas del fragmento de manera segura.
    private var _binding: FragmentAltaBinding? = null
    private val binding get() = _binding!!
    private var salaSeleccionada: String? = null
    // Instancia del servicio API para realizar llamadas de red.
    private val apiServicios = APIservicios()
    // Contador para asignar un ID único a cada reserva creada.
    private var idCounter: String = "Null"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el layout del fragmento usando View Binding para un acceso seguro a las vistas.
        _binding = FragmentAltaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            mostrarSalas()
            // Configurar el listener del botón para procesar el envío de datos.
            buttonPost.setOnClickListener {
                // Recoger los datos ingresados por el usuario.
                val nickname = editTextNickname.text.toString()
                val numCarnet = editTextNumCarnet.text.toString().toIntOrNull()
                val telefono = editTextTelefono.text.toString().toIntOrNull()
                val email = editTextEmail.text.toString()
                val salaSeleccionada = spinnerSala.selectedItem.toString()
                val fecha = buttonSeleccionarFecha.text.toString()
                val hora = buttonSeleccionarHora.text.toString()
                val numPers = editTextNumPers.text.toString().toIntOrNull()
                val comentario = editTextComentario.text.toString()

                // Validar que los campos no estén vacíos y que los valores numéricos sean válidos.
                if (nickname.isNotEmpty() && numCarnet != null && telefono != null && email.isNotEmpty() && numPers != null && comentario.isNotEmpty()) {

                    textViewResponse.text =""

                    // Validar el nickname utilizando una expresión regular.
                    val namePattern = Regex("[a-zA-Z0-9]+")
                    val emailPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

                    if (!nickname.matches(namePattern)) {
                        // Mensaje de error para el campo nickname
                        editTextNickname.error = "Introduce un nickname válido"
                    } else if (!email.matches(emailPattern)) {
                        editTextEmail.error = "Introduce un email válido"
                    } else {
                        // Iniciar una coroutine para realizar operaciones de red.
                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                // Realizar la solicitud de creación de persona y obtener la respuesta.
                                val response = apiServicios.createReserva(nickname, numCarnet, telefono, email, salaSeleccionada, fecha, hora, numPers, comentario)
                                launch(Dispatchers.Main) {
                                    // Mostrar un Snackbar con la respuesta del servidor.
                                    Snackbar.make(it, response.bodyAsText(), Snackbar.LENGTH_INDEFINITE)
                                        .setAction("OK") { }
                                        .show()

                                    // Limpiar los campos de texto después del envío.
                                    editTextNickname.setText("")
                                    editTextNumCarnet.setText("")
                                    editTextTelefono.setText("")
                                    editTextEmail.setText("")
                                    buttonSeleccionarFecha.setText("Seleccionar Fecha:")
                                    buttonSeleccionarHora.setText("Seleccionar Hora")
                                    editTextNumPers.setText("")
                                    editTextComentario.setText("")

                                    // Ocultar el teclado.
                                    activity?.currentFocus?.let { view ->
                                        ocultarTecladoExplicitamente(view)
                                    }
                                }
                            } catch (e: ClientRequestException) {
                                // Manejar errores específicos de la solicitud.
                                launch(Dispatchers.Main) {
                                    textViewResponse.text = "Error: ${e.response.status.description}"
                                }
                            } catch (e: Exception) {
                                // Manejar otros errores generales.
                                launch(Dispatchers.Main) {
                                    textViewResponse.text = "Error: ${e.message}"
                                }
                            }
                        }
                    }
                } else {
                    // Mostrar mensaje si los campos no están correctamente llenados.
                    textViewResponse.text = "Por favor, completa todos los campos correctamente"
                }
            }

            // Configurar el listener del botón para mostrar el diálogo de selección de fecha.
            buttonSeleccionarFecha.setOnClickListener {
                mostrarDatePickerDialog()
            }

            // Configurar el listener del botón para mostrar el diálogo de selección de hora.
            buttonSeleccionarHora.setOnClickListener {
                mostrarTimePickerDialog()
            }

        }
    }

    private fun mostrarSalas() {
        val opciones = arrayOf("Pilates", "TRX", "Yoga")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, opciones)
        binding.spinnerSala.adapter = adapter

        binding.spinnerSala.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val seleccion = opciones[position]
                Toast.makeText(requireContext(), "Seleccionaste: $seleccion", Toast.LENGTH_SHORT).show()
                salaSeleccionada = seleccion
                (parent.getChildAt(0) as? TextView)?.text = seleccion
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }


    // Función para ocultar el teclado.
    private fun ocultarTecladoExplicitamente(view: View) {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Función para mostrar el diálogo de selección de fecha
    private fun mostrarDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.buttonSeleccionarFecha.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    // Función para mostrar el diálogo de selección de hora
    private fun mostrarTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            binding.buttonSeleccionarHora.setText(selectedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    override fun onDestroyView() {
        // Limpiar el binding cuando la vista del fragmento está siendo destruida.
        super.onDestroyView()
        _binding = null
    }
}
