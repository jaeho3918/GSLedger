package com.gsgana.gsledger.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "brand") var brand: String = "",
    @ColumnInfo(name = "metal") var metal: Int = -1,
    @ColumnInfo(name = "type") var type: Int = -1,
    @ColumnInfo(name = "packageType") var packageType: Int = 0,
    @ColumnInfo(name = "quantity") var quantity: Int = 1,
    @ColumnInfo(name = "weight") var weight: Float = 1.0f,
    @ColumnInfo(name = "weightUnit") var weightUnit: Int = 0,
    @ColumnInfo(name = "weightr") var weightr: Float = 0f,
    @ColumnInfo(name = "currency") var currency: Int = 0,
    @ColumnInfo(name = "price") var price: Float = 0f,
    @ColumnInfo(name = "totalPrice") var totalPrice: Float = 0f,
    @ColumnInfo(name = "buyDate") var buyDate: String = "",
    @ColumnInfo(name = "editDate") var editDate: String = "",
    @ColumnInfo(name = "memo") var memo: String = "",
    @ColumnInfo(name = "reg") var reg: Float = 0.0f,
    @ColumnInfo(name = "prePrice") var prePrice: Float = -1.0f,
    @ColumnInfo(name = "cur") var cur: Float = -1.0f,
    @ColumnInfo(name = "year") var year: Int = 0,
    @ColumnInfo(name = "condition") var condition: Int = -1,
    @ColumnInfo(name = "grade") var grade: String = "",
    @ColumnInfo(name = "gradeNum") var gradeNum:  Int = 0,
    @ColumnInfo(name = "cert") var cert: String = "",
    @ColumnInfo(name = "pre") var pre: Float = 0.0f,
    @ColumnInfo(name = "chart") var chart: String = "",
    @ColumnInfo(name = "favorite") var favorite: Boolean = false,
    @ColumnInfo(name = "ppr") var ppr: Float = 0.0f
)




