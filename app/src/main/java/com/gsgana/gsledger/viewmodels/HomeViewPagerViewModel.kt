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

    companion object {
        private var instance : HomeViewPagerViewModel? = null
        fun getInstance(productRepository: ProductRepository) =
            instance ?: synchronized(HomeViewPagerViewModel::class.java){
                instance ?: HomeViewPagerViewModel(productRepository).also { instance = it }
            }
    }

    val realGold = MutableLiveData<Float>(1f)
    val realSilver = MutableLiveData<Float>(1f)
    var ratioMetal = MutableLiveData<List<Float>>(mutableListOf(0f, 0f, 0f, 0f))
    val products: LiveData<List<Product>> = productRepository.getProducts()
    val realData = MutableLiveData<Map<String,Double>>()



    val totalGold: LiveData<Float> = Transformations.switchMap(realGold) { goldPrice ->
        var reg = 0f
        products.value.also {
            products.value?.forEach { product ->
                reg += 1 + product.reg
            }
        }
        MutableLiveData(reg * goldPrice)
    }

    val totalSilver = MediatorLiveData<Float>()
        .apply {
            addSource(realSilver) {
                var reg = 0f

                products.value.also {
                    products.value?.forEach {
                        reg += 1 + it.reg
                    }
                }
                this.value = reg * it
            }
            addSource(products) {
                var reg = 0f
                products.value.also {
                    products.value?.forEach {
                        reg += 1 + it.reg
                    }
                }
                this.value = reg * realSilver.value!!
            }
        }


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
