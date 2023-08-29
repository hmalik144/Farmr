package com.appttude.h_mal.farmr.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appttude.h_mal.farmr.model.ViewState

open class BaseViewModel: ViewModel() {

    private val _uiState = MutableLiveData<ViewState>()
    val uiState: LiveData<ViewState> = _uiState


    fun onStart() {
        _uiState.postValue(ViewState.HasStarted)
    }

    fun <T : Any> onSuccess(result: T) {
        _uiState.postValue(ViewState.HasData(result))
    }

    protected fun <E : Any> onError(error: E) {
        _uiState.postValue(ViewState.HasError(error))
    }
}