package com.gsgana.gsledger.viewmodels

import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.utilities.*

class ProductsViewModel(private val product: Product) {

    val id
        get() = product.id.toString()

    val brand
        get() = product.brand //product.year.toString() + " " +

    val memo
        get() = product.memo

    val price
        get() = CURRENCYSYMBOL[product.currency] + String.format("%,.2f", product.price)

    val currency
        get() = CURRENCYSYMBOL[product.currency]

    val buyDate
        get() = product.buyDate

    val quantity
        get() = (product.quantity * PACKAGENUM[product.packageType]).toString()

    val totalPrice
        get() = CURRENCYSYMBOL[product.currency] +String.format("%,.2f", product.prePrice)

    val condition
        get() = product.condition

    val cert
        get() = product.cert

    val grade
        get() = product.grade


}
