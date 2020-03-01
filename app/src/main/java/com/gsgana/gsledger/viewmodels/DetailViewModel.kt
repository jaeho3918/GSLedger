package com.gsgana.gsledger.viewmodels

import android.os.Build.BRAND
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.ProductRepository
import kotlinx.coroutines.launch

class DetailViewModel(
    private val productRepository: ProductRepository,
    id: Long
) : ViewModel() {

    val product = productRepository.getProduct(id)

    fun addProduct(product:Product) {
        viewModelScope.launch {
            productRepository.createProduct(product)
        }
    }

    fun delProduct(id:Long) {
        viewModelScope.launch {
            productRepository.deleteIdProduct(id)
        }
    }
//
//    val id
//        get() = product.id
//
//    val brand
//        get() = product.brand
//
//    val memo
//        get() = product.memo
//
//    val metal
//        get() = product.metal
//
//    val editDate
//        get() = product.editDate
//
//        val packageType
//        get() = product.packageType
//
//    val weight
//        get() = product.weight


//    val product = product1.value!!
//
//    val stringId
//        get() = id.toString()
//
//    val brand
//        get() = BRAND[product.brand].toString()
//
//    val memo
//        get() = product.memo
//
//    val metal
//        get() = product.metal.toString()
//
//    val editDate
//        get() = product.editDate
//
//    val packageType
//        get() = product.packageType.toString()
//
//    val weight
//        get() = product.weight.toString()

//    fun editProduct() {
//        viewModelScope.launch {
//            productRepository.updateProduct(id)
//        }
//    }
}
