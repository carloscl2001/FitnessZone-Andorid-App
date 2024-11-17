package com.example.fitnesszone.ui.reserva

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ReservaViewModel : ViewModel() {

    private val _id = MutableLiveData<String>()
    private val _nickname = MutableLiveData<String>()
    private val _numCarnet = MutableLiveData<Int>()
    private val _telefono = MutableLiveData<Int>()
    private val _email = MutableLiveData<String>()
    private val _sala = MutableLiveData<String>()
    private val _fecha = MutableLiveData<String>()
    private val _hora = MutableLiveData<String>()
    private val _numPers = MutableLiveData<Int>()
    private val _comentario = MutableLiveData<String>()

    // Es para el holder
    val id: LiveData<String> = _id
    val nickname: LiveData<String> = _nickname
    val numCarnet: LiveData<Int> = _numCarnet
    val telefono: LiveData<Int> = _telefono
    val email: LiveData<String> = _email
    val sala: LiveData<String> = _sala
    val fecha: LiveData<String> = _fecha
    val hora: LiveData<String> = _hora
    val numPers: LiveData<Int> = _numPers
    val comentario: LiveData<String> = _comentario

    fun setDatos(id: String?, nickname: String?, numCarnet: Int?, telefono: Int?,
                 email: String?, sala: String?, fecha: String?, hora: String?,
                 numPers: Int?, comentario: String?) {

        _id.value = id ?: "N/A"
        _nickname.value = nickname ?: "N/A"
        _numCarnet.value = numCarnet ?: -1
        _telefono.value = telefono ?: -1
        _email.value = email ?: "N/A"
        _sala.value = sala ?: "N/A"
        _fecha.value = fecha ?: "N/A"
        _hora.value = hora ?: "N/A"
        _numPers.value = numPers ?: -1
        _comentario.value = comentario ?: "N/A"
    }
}
