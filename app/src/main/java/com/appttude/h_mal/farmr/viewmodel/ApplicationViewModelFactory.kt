package com.appttude.h_mal.farmr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appttude.h_mal.farmr.data.RepositoryImpl


class ApplicationViewModelFactory(
    private val repository: RepositoryImpl
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            return when {
                isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repository)
                else -> throw IllegalArgumentException("Unknown ViewModel class")
            } as T
        }
    }

}