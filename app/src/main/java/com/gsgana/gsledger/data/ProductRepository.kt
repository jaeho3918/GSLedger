package com.gsgana.gsledger.data

class ProductRepository private constructor(
    private val productDao: ProductDao
//    private val productImageDao: ProductImageDao
) {

    suspend fun createProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun deleteProducts() {
        productDao.deleteProducts()
    }
    suspend fun deleteProduct(id:Long) {
        productDao.deleteProduct(id)
    }

    fun getProducts() = productDao.getProducts()

    fun getProduct(id: Long) = productDao.getProduct(id)



    companion object {
        @Volatile
        private var instant: ProductRepository? = null

        fun getInstance(
            productDao: ProductDao
//            productImageDao: ProductImageDao
        ) =
            instant ?: synchronized(this) {
                instant ?: ProductRepository(
                    productDao
//                    productImageDao
                ).also { instant = it }
            }
    }
}
