package com.gsgana.gsledger.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface ProductDao {
    @Transaction
    @Query("SELECT * FROM product")
    fun getProducts(): LiveData<List<Product>>

    @Query("SELECT COUNT (*) FROM product")
    fun getNum(): Int

    @Query("SELECT * FROM product WHERE id = :id")
    fun getProduct(id: Long): LiveData<Product>

    @Insert(onConflict = REPLACE)
    suspend fun insertProduct(product: Product)

    @Query("DELETE from product")
    suspend fun deleteProducts()

    @Query("DELETE from product WHERE id = :id")
    suspend fun deleteProduct(id: Long)

}

//    @Query("SELECT * FROM product WHERE id = 0")
//    fun getProduct(): LiveData<Product>

//    @Query("SELECT * FROM garden_plantings")
//    fun getGardenPlantings(): LiveData<List<GardenPlanting>>
//
//    @Query("SELECT EXISTS(SELECT 1 FROM garden_plantings WHERE plant_id = :plantId LIMIT 1)")
//    fun isPlanted(plantId: String): LiveData<Boolean>
//
//    /**
//     * This query will tell Room to query both the [Plant] and [GardenPlanting] tables and handle
//     * the object mapping.
//     */
//    @Transaction
//    @Query("SELECT * FROM plants WHERE id IN (SELECT DISTINCT(plant_id) FROM garden_plantings)")
//    fun getPlantedGardens(): LiveData<List<PlantAndGardenPlantings>>
//
//    @Insert
//    suspend fun insertGardenPlanting(gardenPlanting: GardenPlanting): Long
//
//    @Delete
//    suspend fun deleteGardenPlanting(gardenPlanting: GardenPlanting)