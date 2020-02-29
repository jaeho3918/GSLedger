package com.gsna.gsnote.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productImage")
data class ProductImage(
    @PrimaryKey var id: String,
    @ColumnInfo(name = "version") var version: String = "",
    @ColumnInfo(name = "image") var image: Byte
)