package com.example.fitnesszone.ui.salas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SalasViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is salas Fragment"
    }
    val text: LiveData<String> = _text
}