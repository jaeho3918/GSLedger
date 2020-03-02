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

    val realGold = MutableLiveData<Float>(1f)
    val realSilver = MutableLiveData<Float>(1f)
    val realCurrency = MutableLiveData<Float>(1f)
    val realPackage = MutableLiveData<Map<String, String>>(null)

    var ratioMetal = MutableLiveData<List<Float>>(mutableListOf(0f, 0f, 0f, 0f))

    val products: LiveData<List<Product>> = productRepository.getProducts()
    val currencyOption = MutableLiveData<Int>(0)

    val realData = MutableLiveData<Map<String,Double>>()

//    val test = MediatorLiveData<List<Float>>().apply {
//        var list = mutableListOf(1f, 1f, 1f)
//        addSource(realGold) { realGold ->
//            list[0] = realGold * (realCurrency.value ?: 1f)
//        }
//        addSource(realSilver) { realSilver ->
//            list[1] = realSilver * (realCurrency.value ?: 1f)
//        }
//        addSource(realCurrency) { realCurrency ->
//            list[0] = realGold.value ?: 1f * (realCurrency ?: 1f)
//            list[1] = realSilver.value ?: 1f * (realCurrency ?: 1f)
//        }
//        addSource(currencyOption) { _ ->
//            list[0] = realGold.value ?: 1f * (realCurrency.value ?: 1f)
//            list[1] = realSilver.value ?: 1f * (realCurrency.value ?: 1f)
//        }
//
//    }

//    val productfilter: LiveData<List<Product>> = Transformations.switchMap(products) {
//        MutableLiveData(it)
//    }


    val totalGold: LiveData<Float> = Transformations.switchMap(realGold) { goldPrice ->
        var reg = 0f
        products.value.also {
            products.value?.forEach { product ->
                reg += 1 + product.reg
            }
        }
        MutableLiveData(reg * goldPrice)
    }

//    val totalGold = MediatorLiveData<Float>()
//        .apply {
//            addSource(realGold) {value ->
//                var reg = 0f
//                products.value.also {
//                    products.value?.forEach {product->
//                        reg += 1 + product.reg
//                    }
//                }
//                this.value = reg * value
//            }
//            addSource(products) {
//                var reg = 0f
//                products.value.also {
//                    products.value?.forEach {
//                        reg += 1 + it.reg
//                    }
//                }
//                this.value = reg * realGold.value!!
//            }
//        }


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
