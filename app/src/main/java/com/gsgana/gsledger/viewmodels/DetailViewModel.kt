package com.gsgana.gsledger.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.ProductRepository
import com.gsgana.gsledger.utilities.*
import kotlinx.coroutines.launch

class DetailViewModel(
    private val productRepository: ProductRepository,
    id: Long
) : ViewModel() {

    private val product = productRepository.getProduct(id)

    fun getProduct(): LiveData<Product> {
        return product
    }

    private var dateTime = ""

    fun setDateTime(string: String) { dateTime = string }
    fun getDateTime(): String { return dateTime }

    private var chartDate = arrayListOf<String>()
    fun setChartDate(inputList: ArrayList<String>) {
        chartDate = inputList
    }
    fun getChartDate() :ArrayList<String> {
        return chartDate
    }



    private val chartData = MutableLiveData<Map<String, ArrayList<*>>>()
    fun setchartData(input: Map<String, ArrayList<*>>) {
        chartData.value = input
    }
    fun getchart(): LiveData<Map<String, ArrayList<*>>> {
        return chartData
    }
















    val brand
        get() = {
            val buf_weight = when (product.value!!.weight) {
                1f -> "1"
                0.05f -> "1/20"
                0.1f -> "1/10"
                0.4f -> "4/10"
                0.5f -> "1/2"
                else -> "1"
            }
            product.value!!.year.toString() + " " + buf_weight + WEIGHTUNIT[product.value!!.weightUnit] + " " + METAL[product.value!!.metal] + " " + TYPE[product.value!!.type] + product.value!!.brand //
        }

    val img
        get() = {
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

    val packageType
        get() = "Package : " + PACKAGETYPE[product.value!!.packageType]

//    val quantityDetail
//        get() = "Quantity : "+(product.value!!.quantity * PACKAGENUM[product.value!!.packageType]).toString() + " (" + product.value!!.quantity.toString() + " x " + PACKAGETYPE[product.value!!.packageType] + ")"

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

    val gradeNum
        get() = product.value!!.gradeNum.toString()

    fun addProduct(product: Product) {
        viewModelScope.launch {
            productRepository.createProduct(product)
        }
    }

    fun delProduct(id: Long) {
        viewModelScope.launch {
            productRepository.deleteProduct(id)
        }
    }


}
