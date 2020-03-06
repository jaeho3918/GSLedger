package com.gsgana.gsledger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gsgana.gsledger.data.ProductRepository

class HomeViewPagerViewModelFactory(
    repository: ProductRepository
) : ViewModelProvider.NewInstanceFactory() {
    private val repository = repository

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewPagerViewModel(repository) as T
    }

    companion object {
        private var instance: HomeViewPagerViewModelFactory? = null
        fun getInstance() = instance ?: synchronized(HomeViewPagerViewModelFactory::class.java) {
            instance ?: HomeViewPagerViewModelFactory.also { instance }
        }
    }
}
