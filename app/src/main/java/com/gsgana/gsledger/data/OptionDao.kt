package com.gsgana.gsledger.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface OptionDao {
    @Transaction
    @Query("SELECT * FROM option")
    fun getOption(): LiveData<Option>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOption(option: Option)

    @Query("DELETE from option")
    suspend fun deleteOption()
}