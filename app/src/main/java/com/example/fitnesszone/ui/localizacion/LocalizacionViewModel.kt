package com.example.fitnesszone.ui.localizacion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocalizacionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is localizacion Fragment"
    }
    val text: LiveData<String> = _text
}