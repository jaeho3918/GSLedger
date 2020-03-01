package com.gsgana.gsledger.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.PrimaryKey
import androidx.room.Relation

data class Products(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "brand") var brand: Int=0,
    @ColumnInfo(name = "metal") var metal: Int=0,
    @ColumnInfo(name = "type") var type: Int=0,
    @ColumnInfo(name = "packageType") var packageType: Int=0,
    @ColumnInfo(name = "quantity") var quantity: Int=0,
    @ColumnInfo(name = "weight") var weight: Float = 0f,
    @ColumnInfo(name = "weightUnit") var weightUnit: Int=0,
    @ColumnInfo(name = "currency") var currency: Int=0,
    @ColumnInfo(name = "price") var price: Float = 0f,
    @ColumnInfo(name = "reg") var reg: Float = 0f,
    @ColumnInfo(name = "buyDate") var buyDate: String = "",
    @ColumnInfo(name = "editDate") var editDate: String = "",
    @ColumnInfo(name = "memo") var memo: String=""
)




//brand: Int=0
//metal: Int=0
//type: Int=0
//packageType: Int=0
//quantity: Int=0
//weight: Float = 0f
//weightUnit: Int=0
//currency: Int=0
//price: Float = 0f
//buyDate: String = ""
//editDate: String = ""
//memo: String=""