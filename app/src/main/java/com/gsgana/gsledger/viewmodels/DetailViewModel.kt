package com.gsgana.gsledger.viewmodels

import android.os.Build.BRAND
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.ProductRepository
import com.gsgana.gsledger.utilities.CURRENCYSYMBOL
import com.gsgana.gsledger.utilities.PACKAGENUM
import com.gsgana.gsledger.utilities.PACKAGETYPE
import kotlinx.coroutines.launch

class DetailViewModel(
    private val productRepository: ProductRepository,
    id: Long
) : ViewModel() {

    private val product = productRepository.getProduct(id)

    fun getProduct(): LiveData<Product> {
        return product
    }

    val brand
        get() = product.value!!.brand //product.value!!.year.toString() + " " +

    val img
        get()={
           1
        }

    val memo
        get() = product.value!!.memo

    val price
        get() = CURRENCYSYMBOL[product.value!!.currency] + String.format(
            "%,.2f",
            product.value!!.price
        )

    val currency
        get() = CURRENCYSYMBOL[product.value!!.currency]

    val buyDate
        get() = product.value!!.buyDate

    val quantity
        get() = (product.value!!.quantity * PACKAGENUM[product.value!!.packageType]).toString()

    val quantityDetail
        get() = "(" + product.value!!.quantity.toString() + " x " + PACKAGETYPE[product.value!!.packageType] + ")"

    val totalPrice
        get() = CURRENCYSYMBOL[product.value!!.currency] + String.format(
            "%,.2f",
            product.value!!.prePrice
        )

    val condition
        get() = product.value!!.condition

    val cert
        get() = product.value!!.cert

    val grade
        get() = product.value!!.grade


    fun addProduct(product: Product) {
        viewModelScope.launch {
            productRepository.createProduct(product)
        }
    }

    fun delProduct(id: Long) {
        viewModelScope.launch {
            productRepository.deleteIdProduct(id)
        }
    }


}
