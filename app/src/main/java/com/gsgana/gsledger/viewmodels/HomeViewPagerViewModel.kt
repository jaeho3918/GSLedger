package com.gsgana.gsledger.viewmodels

import androidx.lifecycle.*
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.ProductRepository
import kotlinx.coroutines.launch

class HomeViewPagerViewModel internal constructor(
    private val productRepository: ProductRepository
) :
    ViewModel() {

    private var dateTime = 0L
    fun setDateTime(string: Long) {
        dateTime = string
    }

    fun getDateTime(): Long {
        return dateTime
    }

    private var chartDate = arrayListOf<String>()
    fun setChartDate(inputList: ArrayList<String>) {
        chartDate = inputList
    }

    fun getChartDate(): ArrayList<String> {
        return chartDate
    }

    private val SSShortchartData = MutableLiveData<Map<String, ArrayList<*>>>()
    fun setSSShortchartData(input: Map<String, ArrayList<*>>) {
        SSShortchartData.value = input
    }

    fun getSSShortchart(): LiveData<Map<String, ArrayList<*>>> {
        return SSShortchartData
    }

    fun getSSShortchartData(): Map<String, ArrayList<*>> {
        return chartData.value ?: mapOf(
            "date" to arrayListOf(""),
            "value_AG" to arrayListOf(0f),
            "value_AU" to arrayListOf(0f),
            "value_RATIO" to arrayListOf(0f)
        )
    }


    private val LongchartData = MutableLiveData<Map<String, ArrayList<*>>>()
    fun setLongchartData(input: Map<String, ArrayList<*>>) {
        LongchartData.value = input
    }

    fun getLongchart(): LiveData<Map<String, ArrayList<*>>> {
        return LongchartData
    }

    fun getLongchartData(): Map<String, ArrayList<*>> {
        return chartData.value ?: mapOf(
            "date" to arrayListOf(""),
            "value_AG" to arrayListOf(0f),
            "value_AU" to arrayListOf(0f),
            "value_RATIO" to arrayListOf(0f)
        )
    }

    private val stackChartData = MutableLiveData<Map<String, ArrayList<*>>>()
    fun setstackChartData(input: Map<String, ArrayList<*>>) {
        stackChartData.value = input
    }

    fun getstackChart(): LiveData<Map<String, ArrayList<*>>> {
        return stackChartData
    }

    fun getstackChartData(): Map<String, ArrayList<*>> {
        return stackChartData.value ?: mapOf(
            "date" to arrayListOf(""),
            "value_AG" to arrayListOf(0f),
            "value_AU" to arrayListOf(0f),
            "value_RATIO" to arrayListOf(0f)
        )
    }


    private val chartData = MutableLiveData<Map<String, ArrayList<*>>>()
    fun setchartData(input: Map<String, ArrayList<*>>) {
        chartData.value = input
    }

    fun getchart(): LiveData<Map<String, ArrayList<*>>> {
        return chartData
    }

    fun getchartData(): Map<String, ArrayList<*>> {
        return chartData.value ?: mapOf(
            "date" to arrayListOf(""),
            "value_AG" to arrayListOf(0f),
            "value_AU" to arrayListOf(0f),
            "value_RATIO" to arrayListOf(0f)
        )
    }


    private val ratioMetal = MutableLiveData<List<Double>>(mutableListOf(0.0, 0.0, 0.0, 0.0))
    fun setRatioMetal(input: List<Double>) {
        ratioMetal.value = input
    }

    private val products: LiveData<List<Product>> = productRepository.getProducts()
    fun getProducts(): LiveData<List<Product>> {
        return products
    }


    private val realData = MutableLiveData<Map<String, Double>>()
    fun setRealData(input: Map<String, Double>) {
        realData.value = input
    }

    fun getRealData(): LiveData<Map<String, Double>> {
        return realData
    }

    fun getRealDataValue(): Map<String, Double> {
        return realData.value ?: mapOf(
            "AU" to 1.0,
            "AG" to 1.0,
            "USD" to 1.0,
            "KRW" to 1.0,
            "EUR" to 1.0,
            "JPY" to 1.0,
            "AUD" to 1.0,
            "CAD" to 1.0,
            "INR" to 1.0,
            "currency" to 0.0,
            "weightUnit" to 0.0,
            "GBP" to 1.0,
            "CNY" to 1.0
        )
    }


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
