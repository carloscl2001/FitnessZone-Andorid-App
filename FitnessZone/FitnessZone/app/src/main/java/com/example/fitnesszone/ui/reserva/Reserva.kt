package com.example.fitnesszone.ui.reserva

import kotlinx.serialization.Serializable

@Serializable
data class Reserva(

    val id: String,
    val nickname: String,
    val numCarnet: Int,
    val telefono: Int,
    val email: String,
    val sala: String,
    val fecha: String,
    val hora: String,
    val numPers: Int,
    val comentario: String
)
