package com.gsgana.gsledger.viewmodels

import android.os.Build.BRAND
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.Products
import com.gsgana.gsledger.utilities.*

class ProductsViewModel(private val product: Product) {

    val id
        get() = product.id.toString()

    val brand
        get() = product.year.toString() + " " +product.brand

    val memo
        get() = product.memo

    val metalType
        get() = METAL[product.metal] + " " + TYPE[product.type]

    val price
        get() = String.format("%,.2f", product.price)

    val currency
        get() = CURRENCYSYMBOL[product.currency]

    val buyDate
        get() = product.buyDate

    val quantity
        get() = (product.quantity * PACKAGENUM[product.packageType]).toString()

    val quantityDetail
        get() = product.quantity.toString() + " x " + PACKAGETYPE[product.packageType]

    val weight
        get() = product.weight.toString() + " " + WEIGHTUNIT[product.weightUnit]

    val totalPrice
        get() = String.format("%,.2f", product.prePrice)

    val condition
        get() = product.condition

    val cert
        get() = product.cert

    val grade
        get() = product.grade


}
