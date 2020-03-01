package com.gsgana.gsledger.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "option")
data class Option(
    @PrimaryKey var id: Int = 18,
    @ColumnInfo(name = "currency") var currency: Int = 0
)