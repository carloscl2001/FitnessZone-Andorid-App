package com.example.fitnesszone.ui.reserva

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.fitnesszone.MainActivity
import com.example.fitnesszone.databinding.FragmentModificarBinding
import com.google.android.material.snackbar.Snackbar
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale


class ModificarFragment : Fragment() {
    private var _binding: FragmentModificarBinding? = null
    private val binding get() = _binding!!
    private val apiServicios = APIservicios()
    private lateinit var miViewModelCompartido: ReservaViewModel
    private var salaSeleccionada: String? = null
    private val CANAL_NOTIFICACION_ID = "canal"

    // Identificador único para la notificación
    private val ID_NOTIFICACION = 1
    var reservaId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModificarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ViewModel compartido
        miViewModelCompartido =
            ViewModelProvider(requireActivity()).get(ReservaViewModel::class.java)

        // Observadores para actualizar la interfaz de usuario cuando los datos cambian en el ViewModel
        miViewModelCompartido.id.observe(viewLifecycleOwner) { id ->
            reservaId = id
        }

        miViewModelCompartido.nickname.observe(viewLifecycleOwner) { nickname ->
            // Actualiza el EditText con el nickname proporcionado
            binding.modificarNickname.setText(nickname ?: "")
        }

        miViewModelCompartido.numCarnet.observe(viewLifecycleOwner) { numCarnet ->
            // Actualiza el EditText con el numCarnet proporcionado
            binding.modificarNumCarnet.setText(numCarnet?.toString() ?: "")
        }

        miViewModelCompartido.telefono.observe(viewLifecycleOwner) { telefono ->
            // Actualiza el EditText con el telefono proporcionado
            binding.modificarTelefono.setText(telefono?.toString() ?: "")
        }

        miViewModelCompartido.email.observe(viewLifecycleOwner) { email ->
            // Actualiza el EditText con el email proporcionado
            binding.modificarEmail.setText(email ?: "")
        }

        miViewModelCompartido.sala.observe(viewLifecycleOwner) { sala ->
            // Actualiza el Spinner con la sala proporcionada
            val opciones = arrayOf("Pilates", "TRX", "Yoga")
            // Primero, obtén el índice de la opción en el Spinner
            val index = opciones.indexOf(sala)
            // Luego, selecciona esa opción si se encontró
                binding.spinnerSala.setSelection(index)
        }


        miViewModelCompartido.fecha.observe(viewLifecycleOwner) { fecha ->
            // Actualiza el Button con la fecha proporcionada
            binding.buttonSeleccionarFecha.text = fecha
        }

        miViewModelCompartido.hora.observe(viewLifecycleOwner) { hora ->
            // Actualiza el Button con la hora proporcionada
            binding.buttonSeleccionarHora.text = hora
        }

        with(binding) {

            mostrarSalas()
            // Configurar el listener del botón para procesar el envío de datos.
            buttonPost.setOnClickListener {
                // Recoger los datos ingresados por el usuario.
                val nickname = modificarNickname.text.toString()
                val numCarnet = modificarNumCarnet.text.toString().toIntOrNull()
                val telefono = modificarTelefono.text.toString().toIntOrNull()
                val email = modificarEmail.text.toString()
                val salaSeleccionada = spinnerSala.selectedItem.toString()
                val fecha = buttonSeleccionarFecha.text.toString()
                val hora = buttonSeleccionarHora.text.toString()
                val numPers = modificarNumPers.text.toString().toIntOrNull()
                val comentario = modificarComentario.text.toString()

                // Validar que los campos no estén vacíos y que los valores numéricos sean válidos.
                if (nickname.isNotEmpty() && numCarnet != null && telefono != null && email.isNotEmpty() && numPers != null && comentario.isNotEmpty()) {

                    modificarResponse.text =""

                    // Validar el nickname utilizando una expresión regular.
                    val namePattern = Regex("[a-zA-Z0-9]+")
                    val emailPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

                    if (!nickname.matches(namePattern)) {
                        // Mensaje de error para el campo nickname
                        modificarNickname.error = "Introduce un nickname válido"
                    } else if (!email.matches(emailPattern)) {
                        modificarEmail.error = "Introduce un email válido"
                    } else {
                        reservaId?.let { id ->

                            // Iniciar una coroutine para realizar operaciones de red.
                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    // Realizar la solicitud de creación de persona y obtener la respuesta.
                                    val response = apiServicios.putReserva(
                                        reservaId ?: "",nickname,numCarnet,telefono,email,salaSeleccionada,fecha,hora,numPers,comentario) // Asegúrate de manejar el caso en que reservaId sea null

                                    launch(Dispatchers.Main) {
                                        // Mostrar un Snackbar con la respuesta del servidor.
                                        Snackbar.make(
                                            it,
                                            "Reserva editada: ",
                                            Snackbar.LENGTH_INDEFINITE
                                        )
                                            .setAction("OK") { }
                                            .show()


                                        // Ocultar el teclado.
                                        activity?.currentFocus?.let { view ->
                                            ocultarTecladoExplicitamente(view)
                                        }
                                        mostrarNotificacion()
                                    }
                                } catch (e: ClientRequestException) {
                                    // Manejar errores específicos de la solicitud.
                                    launch(Dispatchers.Main) {
                                        modificarResponse.text =
                                            "Error: ${e.response.status.description}"
                                    }
                                } catch (e: Exception) {
                                    // Manejar otros errores generales.
                                    launch(Dispatchers.Main) {
                                        modificarResponse.text = "Error: ${e.message}"
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Mostrar mensaje si los campos no están correctamente llenados.
                    modificarResponse.text = "Por favor, completa todos los campos correctamente"
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



        miViewModelCompartido.numPers.observe(viewLifecycleOwner) { numPers ->
            // Actualiza el EditText con el numPers proporcionado
            binding.modificarNumPers.setText(numPers?.toString() ?: "")
        }

        miViewModelCompartido.comentario.observe(viewLifecycleOwner) { comentario ->
            // Actualiza el EditText con el comentario proporcionado
            binding.modificarComentario.setText(comentario ?: "")
        }

    }

    private fun mostrarNotificacion() {
        // Crear el canal de notificación (necesario en Android 8.0+)
        crearCanalNotificacion()
        val intento = Intent(context, MainActivity::class.java)
        intento.putExtra("fragmento", "ReservaFragment")
        // Crea un PendingIntent que envuelve nuestro intento, necesario para que el clic funcione.
        val intencionPendiente = PendingIntent.getActivity(context, 0, intento, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        // Verificar si el permiso de notificación está concedido
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // Configurar los parámetros de la notificación
            val notificacion = NotificationCompat.Builder(requireContext(), CANAL_NOTIFICACION_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Reserva Modificada")
                .setContentText("Tu reserva ha sido modificada con éxito.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 500, 500))
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(intencionPendiente)
            // Enviar la notificación con el gestor
            val gestorNotificaciones = NotificationManagerCompat.from(requireContext())
            gestorNotificaciones.notify(ID_NOTIFICACION, notificacion.build())
        } else {
            Toast.makeText(context, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Canal de Ejemplo"
            val descripcion = "Canal para notificaciones de ejemplo"
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(CANAL_NOTIFICACION_ID, nombre, importancia).apply {
                description = descripcion
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }
            val gestorNotificaciones: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            gestorNotificaciones.createNotificationChannel(canal)
        }
    }

    private fun mostrarSalas() {
        val opciones = arrayOf("Pilates", "TRX", "Yoga")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, opciones)
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

}