package com.gsgana.gsledger.utilities

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.gsgana.gsledger.data.AppDatabase
import com.gsgana.gsledger.data.ProductRepository
import com.gsgana.gsledger.viewmodels.DetailViewModelFactory
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModelFactory
import com.gsgana.gsledger.viewmodels.WriteViewModelFactory

object InjectorUtils {

    fun provideHomeViewPagerViewModelFactory(
        context: Context,
        key: CharArray?
    ): HomeViewPagerViewModelFactory {
        val repository =
            getProductRepository(context, key)
        return HomeViewPagerViewModelFactory(repository)
    }


    fun provideWriteViewModelFactory(
        context: Context,
        key: CharArray?
    ): WriteViewModelFactory {
        val repository =
            getProductRepository(context, key)
        return WriteViewModelFactory(repository)
    }

    private fun getProductRepository(
        context: Context,
        key: CharArray?
    ): ProductRepository {

        val reg =AppDatabase.getInstance(context.applicationContext, key)

        return ProductRepository.getInstance(
            reg.productDao(), reg.productImageDao(),reg.optionDao()
        )
    }

    fun provideDetailViewModelFactory(
        context: Context,
        id: Long
    ): DetailViewModelFactory {
        return DetailViewModelFactory(
            getProductRepository(context, null), id
        )
    }
}