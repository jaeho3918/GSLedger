package com.gsgana.gsledger.viewmodels

import androidx.lifecycle.*
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.ProductRepository
import kotlinx.coroutines.launch

class HomeViewPagerViewModel internal constructor(
    private val productRepository: ProductRepository
) :
    ViewModel() {

    private val ratioMetal = MutableLiveData<List<Double>>(mutableListOf(0.0, 0.0, 0.0, 0.0))
    fun setRatioMetal(input: List<Double>) {
        ratioMetal.value = input
    }

    private val products: LiveData<List<Product>> = productRepository.getProducts()
    fun getProducts(): LiveData<List<Product>> {
        return products
    }

    private val currencyOption = MutableLiveData(0)
    fun setCurrencyOption(input: Int) {
        currencyOption.value = input }
    fun getCurrencyOption(): LiveData<Int> {
        return currencyOption }

    private val realData = MutableLiveData<Map<String, Double>>()
    fun setRealData(input :Map<String, Double>) {
        realData.value = input }
    fun getRealData() : LiveData<Map<String, Double>>{
        return realData  }


    fun productNum() = products.value?.size ?: 0

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


    companion object {
        private lateinit var sInstant: HomeViewPagerViewModel

        fun get(productRepository: ProductRepository): HomeViewPagerViewModel {
            sInstant =
                if (::sInstant.isInitialized) sInstant else HomeViewPagerViewModel(productRepository)
            return sInstant
        }
    }

}
