package com.gsna.gsnote.data

class ProductRepository private constructor(
    private val productDao: ProductDao,
    private val productImageDao: ProductImageDao,
    private val optionDao: OptionDao
) {

    suspend fun createProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun deleteProduct() {
        productDao.deleteProduct()
    }
    suspend fun deleteIdProduct(id:Long) {
        productDao.deleteidProduct(id)
    }

    suspend fun createProductImage(product: ProductImage) {
        productImageDao.insertProductImage(product)
    }

    suspend fun deleteProductImage() {
        productImageDao.deleteProductImage()
    }

    suspend fun createOption(product: Option) {
        optionDao.insertOption(product)
    }


//    suspend fun updateProduct(product : Product) {
//        productDao.insertProduct(product)
//    }

    fun getProducts() = productDao.getProducts()

    fun getProduct(id: Long) = productDao.getProduct(id)

    fun getProductImages() = productImageDao.getProductImages()

    fun getProductImage(id: String) = productImageDao.getProductImage(id)

    fun getOption() = optionDao.getOption()


    companion object {
        @Volatile
        private var instant: ProductRepository? = null

        fun getInstance(
            productDao: ProductDao,
            productImageDao: ProductImageDao,
            optionDao: OptionDao
        ) =
            instant ?: synchronized(this) {
                instant ?: ProductRepository(
                    productDao,
                    productImageDao,
                    optionDao
                ).also { instant = it }
            }
    }
}
