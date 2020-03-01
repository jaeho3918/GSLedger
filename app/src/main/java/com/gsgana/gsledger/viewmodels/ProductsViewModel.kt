package com.gsgana.gsledger.viewmodels

import android.os.Build.BRAND
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.Products

class ProductsViewModel(private val product: Product) {

    val id
        get() = product.id.toString()

    val brand
        get() = product.brand

    val memo
        get() = product.memo

    val metal
        get() = product.metal

    val type
        get() = product.type

    val editDate
        get() = product.editDate

    val price
        get() = product.price

    val currency
        get() = product.currency

    val buyDate
        get() = product.buyDate

    val quantity
        get() = product.quantity

    val weight
        get() = product.weight

    val weightUnit
        get() = product.weightUnit

    val totalprice
        get() = product.prePrice
}
