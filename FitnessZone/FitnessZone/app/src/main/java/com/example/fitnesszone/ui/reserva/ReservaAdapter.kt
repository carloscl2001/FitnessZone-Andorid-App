package com.example.fitnesszone.ui.reserva

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesszone.R
import com.example.fitnesszone.databinding.ListItemReservaBinding

interface ReservaItemClickListener {
    fun onReservaItemClicked(reservaId: String)
}

class ReservaAdapter(private var reservas: List<Reserva>,private val listener: ReservaItemClickListener) :
    RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    inner class ReservaViewHolder(private val binding: ListItemReservaBinding) : RecyclerView.ViewHolder(binding.root) {
        val textViewNickname: TextView = binding.textViewNickname
        val textViewSala: TextView = binding.textViewSala
        val textViewFecha: TextView = binding.textViewFecha
        val textViewHora: TextView = binding.textViewHora

        val buttonVer: Button = binding.buttonVer

        init {
            // Agregar el OnClickListener al botón "Ver"
            buttonVer.setOnClickListener {
                // Obtener la reserva asociada a la posición del ViewHolder
                val reserva = reservas[adapterPosition]
                // Aquí puedes realizar la acción deseada, como navegar a otra pantalla o mostrar más detalles
                listener.onReservaItemClicked(reserva.id)
                it.findNavController().navigate(R.id.verFragment)
                // Puedes acceder al contexto a través de itemView.context y realizar acciones según sea necesario
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val binding = ListItemReservaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReservaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = reservas[position]
        holder.textViewNickname.text = reserva.nickname
        holder.textViewSala.text = reserva.sala
        holder.textViewFecha.text = reserva.fecha
        holder.textViewHora.text = reserva.hora
        // Puedes configurar más vistas si es necesario

        // También puedes configurar el botón "Ver" aquí si es necesario
    }

    override fun getItemCount(): Int {
        return reservas.size
    }

    fun updateReservas(newReservas: List<Reserva>) {
        reservas = newReservas
        notifyDataSetChanged()
    }
}
