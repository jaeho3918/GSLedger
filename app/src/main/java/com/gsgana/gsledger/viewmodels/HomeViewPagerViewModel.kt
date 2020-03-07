package com.gsgana.gsledger.viewmodels

import androidx.lifecycle.*
import com.gsgana.gsledger.data.Option
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.ProductRepository
import kotlinx.coroutines.launch

class HomeViewPagerViewModel internal constructor(
    private val productRepository: ProductRepository
) :
    ViewModel() {

    var ratioMetal = MutableLiveData<List<Float>>(mutableListOf(0f, 0f, 0f, 0f))
    val products: LiveData<List<Product>> = productRepository.getProducts()
    val realData = MutableLiveData<Map<String, Double>>()


    fun addProduct(product: Product) {
        viewModelScope.launch {
            productRepository.createProduct(product)
        }
    }

    fun insertOption(option: Option) {
        viewModelScope.launch {
            productRepository.createOption(option)
        }
    }

    fun deleteProduct() {
        viewModelScope.launch {
            productRepository.deleteProduct()
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
