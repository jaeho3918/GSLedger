package com.gsgana.gsledger.viewmodels

import androidx.lifecycle.*
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.ProductRepository
import com.gsgana.gsledger.utilities.CURRENCY
import com.gsgana.gsledger.utilities.PACKAGETYPE
import kotlinx.coroutines.launch


class WriteViewModel internal constructor(
    private val productRepository: ProductRepository
) :
    ViewModel() {

    var product = Product()

    var write2Array: Array<String>? = null

    val metalField1 = MutableLiveData<Int?>(null)

    val typeField1 = MutableLiveData<Int?>(null)

    val brandField1 = MutableLiveData(0)

    val weightField1 = MutableLiveData<String>(null)

    val weightField2 = MutableLiveData<String>(null)

    val weightUnitField = MutableLiveData(0)

    val weightr = MutableLiveData(1f)

    val quantityField = MutableLiveData<String>("1")

    val packageTypeField = MutableLiveData(0)

    val regField = MutableLiveData<Float>(0f)

    val gradeField = MutableLiveData<Int?>(null)

    val gradeNumField = MutableLiveData<Int?>(null)

    val yearSeriesField = MutableLiveData<String>(null)

    val dateField = MutableLiveData<String>(null)

    val currencyField = MutableLiveData<Int?>(null)

    val priceField = MutableLiveData<MutableList<String?>>(listOf(null, null).toMutableList())

    val priceField1 = MutableLiveData<String>(null)

    val priceField2 = MutableLiveData<String>(null)

    val memoField = MutableLiveData<String>(null)

    val totalPrice = MutableLiveData<String>(null)

    val price: LiveData<String> = Transformations.map(priceField) {
        "${it[0] ?: "0"}.${it[1] ?: "00"}"
    }

    val brand = MediatorLiveData<String>().apply {
        addSource(brandField1) {
            value = write2Array?.get(it)
        }
    }

//    val currency = MediatorLiveData<String>().apply {
//        addSource(currencyField) {
//            value = CURRENCY.get(it ?:0)
//        }
//    }
//
//    val packageTypeString = MediatorLiveData<String>().apply {
//        addSource(packageTypeField) {
//            value = PACKAGETYPE[it]
//        }
//    }

    val priceMerger = MediatorLiveData<String>()
        .apply {
            addSource(priceField1) {
                var test1 = priceField1.value ?: "0"
                val test2 = priceField2.value ?: "00"
                if (test1 == "") {
                    test1 = "0"
                }
                val test3 = "${test1}.${test2}"
                value = test3
            }
            addSource(priceField2) {
                var test1 = priceField1.value ?: "0"
                val test2 = priceField2.value ?: "00"
                if (test1 == "") {
                    test1 = "0"
                }
                val test3 = "${test1}.${test2}"
                value = test3
            }
        }


    val weightUnit = MediatorLiveData<Float>()
        .apply {
            addSource(weightUnitField) {
                value = when (it) {
                    0 -> 1f //toz
                    1 -> 0.03215f //g
                    2 -> 32.150747f  //kg
                    3 -> 0.120565f//don
                    else -> 1f
                }
            }
        }

    val packageType = Transformations.map(packageTypeField)
        {
                when (it) {
                    0 -> 1 //1
                    1 -> 10 //tube 10
                    2 -> 20  //tube 20
                    3 -> 25 //tube 25
                    4 -> 250 //mosterBox250
                    5 -> 500 //mosterBox500
                    else -> 1
                }
        }

    val weightMerger = MediatorLiveData<String>()
        .apply {
            addSource(weightField1) {
                var test1 = weightField1.value ?: "0"
                val test2 = weightField2.value ?: "00"
                if (test1 == "") {
                    test1 = "0"
                }
                value = "$test1.${test2}"
            }
            addSource(weightField2) {
                var test1 = weightField1.value ?: "0"
                val test2 = weightField2.value ?: "00"
                if (test1 == "") {
                    test1 = "0"
                }
                value = "$test1.${test2}"
            }
        }

    val weightCalculator = MediatorLiveData<String>()
        .apply {
            addSource(weightMerger) {
                val test1 = weightMerger.value?.toFloat() ?: 1f
                val test2 = weightUnit.value ?: 1f
                val test3 = quantityField.value?.toInt() ?: 1
                val test4 = packageType.value ?: 1
                value = String.format("%.6f", test1 * test2 * test3 * test4)
            }
            addSource(weightUnit) {
                val test1 = weightMerger.value?.toFloat() ?: 1f
                val test2 = weightUnit.value ?: 1f
                val test3 = quantityField.value?.toInt() ?: 1
                val test4 = packageType.value ?: 1
                value = String.format("%.6f", test1 * test2 * test3 * test4)
            }
            addSource(quantityField) {
                val test1 = weightMerger.value?.toFloat() ?: 1f
                val test2 = weightUnit.value ?: 1f
                val test3 = quantityField.value?.toInt() ?: 1
                val test4 = packageType.value ?: 1
                value = String.format("%.6f", test1 * test2 * test3 * test4)
            }
            addSource(packageType) {
                val test1 = weightMerger.value?.toFloat() ?: 1f
                val test2 = weightUnit.value ?: 1f
                val test3 = quantityField.value?.toInt() ?: 1
                val test4 = packageType.value ?: 1
                value = String.format("%.6f", test1 * test2 * test3 * test4)
            }
        }


    fun initProduct() {
        this.product = Product()
        this.write2Array = null
        this.metalField1.value = null
        this.typeField1.value = null
        this.brandField1.value = 0
        this.weightField1.value = null
        this.weightField2.value = null
        this.weightUnitField.value = 0
        this.quantityField.value = "1"
        this.packageTypeField.value = 0
        this.brand.value = ""
        this.gradeField.value = null
        this.gradeNumField.value = null
        this.yearSeriesField.value = null
        this.dateField.value = null
        this.gradeField.value = null
        this.gradeNumField.value = null
        this.yearSeriesField.value = null
        this.dateField.value = null
        this.currencyField.value = null
        this.priceField1.value = null
        this.priceField2.value = null
        this.priceField.value = listOf(null, null).toMutableList()
        this.totalPrice.value = null
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            productRepository.createProduct(product)
        }
    }
}



