package com.example.fitnesszone.ui.salas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocalizacionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "¿Dónde nos encontramos?"
    }
    val text: LiveData<String> = _text
}