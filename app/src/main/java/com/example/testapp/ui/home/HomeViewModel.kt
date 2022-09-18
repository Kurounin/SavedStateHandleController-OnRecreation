package com.example.testapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class HomeViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _text = savedStateHandle.getLiveData<String>("text")
    val text: LiveData<String> = _text

    fun setText(text: String) {
        savedStateHandle.set("text", text)
    }
}