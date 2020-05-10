package com.gsgana.gsledger.viewmodels

import androidx.lifecycle.*
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.ProductRepository
import kotlinx.coroutines.launch


class WriteViewModel internal constructor(
    private val productRepository: ProductRepository
) :
    ViewModel() {

    private var product = Product()
    fun getProduct() = product

    private var min = 0f
    fun getmin() = min
    fun setmin(input: Float){ min= input}

    private var max = 0f
    fun getmax() = max
    fun setmax(input: Float){ max= input}

    private var priceTest = ""
    fun getPriceTest() = priceTest
    fun setPriceTest(input : String){ priceTest = input }

    private var write2Array: Array<String>? = null
    fun getwrite2Array() = write2Array
    fun setwrite2Array(input : Array<String>) {write2Array = input}

    private val metalField1 = MutableLiveData<Int?>(null)
    fun getmetalField1():Int?{return metalField1.value}
    fun setmetalField1(input : Int?) {metalField1.value = input}

    private val typeField1 = MutableLiveData<Int?>(null)
    fun gettypeField1():Int?{return typeField1.value}
    fun settypeField1(input : Int?) {typeField1.value = input}

    private val brandField1 = MutableLiveData(-1)
    fun getbrandField1():Int?{return brandField1.value}
    fun setbrandField1(input : Int?) {brandField1.value = input}

    private val weightField1 = MutableLiveData<String?>(null)
    fun getweightField1():String?{return weightField1.value}
    fun setweightField1(input : String?) {weightField1.value = input}

    private val weightField2 = MutableLiveData<String?>(null)
    fun getweightField2():String?{return weightField2.value}
    fun setweightField2(input : String?) {weightField2.value = input}

    private val weightUnitField = MutableLiveData(0)
    fun getweightUnitField():Int?{return weightUnitField.value}
    fun setweightUnitField(input : Int?) {weightUnitField.value = input}

    private val quantityField = MutableLiveData<String>("1")
    fun getquantityField():String?{return quantityField.value}
    fun setquantityField(input : String?) {quantityField.value = input}

    private val packageTypeField = MutableLiveData(0)
    fun getpackageTypeField():Int?{return packageTypeField.value}
    fun setpackageTypeField(input : Int?) {packageTypeField.value = input}

    private val regField = MutableLiveData<Float>(0f)
    fun getregField():Float?{return regField.value}
    fun setregField(input : Float?) {regField.value = input}

    private val curField = MutableLiveData<Float>(0f)
    fun getcurField():Float?{return curField.value}
    fun setcurField(input : Float?) {curField.value = input}

    private val gradeField = MutableLiveData<Int?>(null)
    fun getgradeField():Int?{return gradeField.value}
    fun setgradeField(input : Int?) {gradeField.value = input}

    private val gradeNumField = MutableLiveData<Int?>(null)
    fun getgradeNumField():Int?{return gradeNumField.value}
    fun setgradeNumField(input : Int?) {gradeNumField.value = input}

    private val yearSeriesField = MutableLiveData<Int>(null)
    fun getyearSeriesField():Int?{return yearSeriesField.value}
    fun setyearSeriesField(input : Int?) {yearSeriesField.value = input}

    private val dateField = MutableLiveData<String>(null)
    fun getdateField():String?{return dateField.value}
    fun setdateField(input : String?) {dateField.value = input}

    private val currencyField = MutableLiveData<Int?>(null)
    fun getcurrencyField():Int?{return currencyField.value}
    fun setcurrencyField(input : Int?) {currencyField.value = input}

    private val priceField = MutableLiveData<MutableList<String?>>(listOf(null, null).toMutableList())
//    fun getpriceField():MutableList<String?>?{return priceField.value}
//    fun setpriceField(input : MutableList<String?>?) {priceField.value = input}

    private val priceField1 = MutableLiveData<String>(null)
//    fun getpriceField1():String?{return priceField1.value}
//    fun setpriceField1(input : String?) {priceField1.value = input}

    private val priceField2 = MutableLiveData<String>(null)
//    fun getpriceField2():String?{return priceField2.value}
//    fun setpriceField2(input : String?) {priceField2.value = input}

    private val memoField = MutableLiveData<String>(null)
    fun getmemoField():String?{return memoField.value}
    fun setmemoField(input : String?) {memoField.value = input}

    private val totalPrice = MutableLiveData<String>(null)
//    fun gettotalPrice():String?{return totalPrice.value}
//    fun settotalPrice(input : String?) {totalPrice.value = input}

    private val pre = MutableLiveData<Float>(null)
    fun getpre():Float?{return pre.value}
    fun setpre(input : Float?) {pre.value = input}

    val price: LiveData<String> = Transformations.map(priceField) {
        "${it[0] ?: "0"}.${it[1] ?: "00"}"
    }

    val brand = MediatorLiveData<String>().apply {
        addSource(brandField1) {
            value = getwrite2Array()?.get(it)
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
                val test2 =  1f
                val test3 = quantityField.value?.toInt() ?: 1
                val test4 = packageType.value ?: 1
                value = String.format("%.6f", test1 * test2 * test3 * test4)
            }
            addSource(weightUnit) {
                val test1 = weightMerger.value?.toFloat() ?: 1f
                val test2 =  1f
                val test3 = quantityField.value?.toInt() ?: 1
                val test4 = packageType.value ?: 1
                value = String.format("%.6f", test1 * test2 * test3 * test4)
            }
            addSource(quantityField) {
                val test1 = weightMerger.value?.toFloat() ?: 1f
                val test2 =  1f
                val test3 = quantityField.value?.toInt() ?: 1
                val test4 = packageType.value ?: 1
                value = String.format("%.6f", test1 * test2 * test3 * test4)
            }
            addSource(packageType) {
                val test1 = weightMerger.value?.toFloat() ?: 1f
                val test2 =  1f
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
        this.gradeNumField.value = null
        this.gradeNumField.value = null
        this.yearSeriesField.value = null
        this.dateField.value = null
        this.currencyField.value = null
        this.priceField1.value = null
        this.priceField2.value = null
        this.priceField.value = listOf(null, null).toMutableList()
        this.totalPrice.value = null
        this.memoField.value = null
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            productRepository.createProduct(product)
        }
    }
}



