package com.gsgana.gsledger.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.ProductRepository
import com.gsgana.gsledger.data.Products
import kotlinx.coroutines.launch

class ListViewModel internal constructor(
    private val productRepository: ProductRepository
) :
    ViewModel() {

    val product = Product(null)

    val products : LiveData<List<Product>> = productRepository.getProducts()

    fun addProduct(product :Product) {
        viewModelScope.launch {
            productRepository.createProduct(product)
        }
    }
    fun deleteProduct() {
        viewModelScope.launch {
            productRepository.deleteProduct()
        }
    }

}
