package com.gsgana.gsledger.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProductImageDao {
    @Transaction
    @Query("SELECT * FROM productImage")
    fun getProductImages(): LiveData<List<ProductImage>>

    @Query("SELECT * FROM productImage WHERE id = :id")
    fun getProductImage(id: String): LiveData<ProductImage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductImage(productImage: ProductImage)

    @Query("DELETE from productImage")
    suspend fun deleteProductImage()
}