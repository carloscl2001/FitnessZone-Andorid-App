package com.example.fitnesszone.ui.salas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitnesszone.R
class SalasViewModel : ViewModel() {

    //_mascotas es un MutableLiveData que solo puede ser modificado dentro del ViewModel
    private val _salas = MutableLiveData<List<Salas>>()
    //mascotas es una forma segura de exponer este LiveData
    val salas: LiveData<List<Salas>> get() = _salas

    init {
        // Lista de salas
        val datosSalas = listOf(
            Salas(
                "SALA DE PILATES",
                        R.mipmap.sala_pilates,
                "La Sala de Pilates es un espacio diseñado para aquellos que buscan fortalecer su núcleo, mejorar la flexibilidad y desarrollar una postura corporal saludable. Equipada con equipos especializados, esta sala ofrece un ambiente tranquilo y enfocado donde los practicantes pueden participar en ejercicios de bajo impacto que trabajan en la alineación y el control del cuerpo. Con instrucción especializada, nuestros profesionales guiarán tus sesiones de Pilates para ayudarte a lograr un cuerpo fuerte y equilibrado.",
                "Entrenador de pilates, sillas de pilates, barriles de Pilates, pelotas de pilates, bandas elásticas, almohadas y bloques.",
                6,
                120
            ),
            Salas(
                "SALA DE TRX",
                R.mipmap.sala_trx,
                "Bienvenido a la Sala de TRX, un espacio dinámico dedicado a los amantes del entrenamiento funcional. Equipada con sistemas de suspensión TRX, esta sala te permite aprovechar tu propio peso corporal para desarrollar fuerza, resistencia y estabilidad. Con entrenadores expertos que te guiarán a través de una variedad de ejercicios, podrás desafiar y fortalecer tu cuerpo de manera efectiva, ya seas principiante o experimentado, podrás alcanzar tus objetivos de acondicionamiento físico.",
                "Entrenador de trx, espejos, anillas, una barra de dominadas, diferentes trx, esterillas, bandas elásticas, roller abs y roller foams.",
                5,
                100
            ),
            Salas(
                "SALA DE YOGA",
                R.mipmap.sala_yoga,
                "La sala de yoga es un espacio tranquilo y luminoso diseñado para ofrecer a los participantes un ambiente sereno y relajante para su práctica. Con un suelo cómodo y antideslizante, equipamiento de yoga adecuado y elementos decorativos que fomentan la calma, la sala proporciona un refugio para la meditación, la flexibilidad y el bienestar general. Con ventilación adecuada y una atmósfera de privacidad, los practicantes pueden sumergirse en su práctica de yoga, encontrando paz y conexión.",
                "Entrenadores de Yoga, esterillasas de yoga para los participantes, junto con bloques y almohadas.",
                20,
                400
            )
        )
        _salas.value = datosSalas
    }

}