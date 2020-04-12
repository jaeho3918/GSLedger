package com.gsgana.gsledger.viewmodels

import androidx.lifecycle.*
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.ProductRepository
import kotlinx.coroutines.launch

class HomeViewPagerViewModel internal constructor(
    private val productRepository: ProductRepository
) :
    ViewModel() {

    var ratioMetal = MutableLiveData<List<Double>>(mutableListOf(0.0, 0.0, 0.0, 0.0))

    private val products: LiveData<List<Product>> = productRepository.getProducts()

    val realData = MutableLiveData<Map<String, Double>>()


    fun getProducts() : LiveData<List<Product>>  {
        return products
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            productRepository.createProduct(product)
        }
    }


    fun deleteProducts() {
        viewModelScope.launch {
            productRepository.deleteProducts()
        }
    }

    companion object{
        private lateinit var sInstant: HomeViewPagerViewModel

        fun get(productRepository: ProductRepository): HomeViewPagerViewModel{
            sInstant = if (::sInstant.isInitialized) sInstant else HomeViewPagerViewModel(productRepository)
            return sInstant
        }
    }


}

private class RealData() : LiveData<Map<String, Double>>() {

    companion object{
        private lateinit var sInstant:RealData

        fun get(): RealData{
            sInstant = if (::sInstant.isInitialized) sInstant else RealData()
            return sInstant
        }
    }

}
