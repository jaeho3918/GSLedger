package com.gsgana.gsledger.utilities

import android.content.Context
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

        return if (key == null) {
            val repository = getProductRepository(context, charArrayOf(
            "q"[0],
            "0"[0],
            "J"[0],
            "2"[0],
            "3"[0],
            "o"[0],
            "1"[0],
            "m"[0],
            "D"[0],
            "E"[0],
            "Y"[0],
            "y"[0],
            "X"[0],
            "j"[0],
            "3"[0],
            "Q"[0],
            "s"[0],
            "h"[0],
            "E"[0],
            "l"[0],
            "8"[0],
            "n"[0],
            "j"[0],
            "Y"[0],
            "B"[0],
            "P"[0],
            "J"[0],
            "C"[0],
            "E"[0]
        ))
            HomeViewPagerViewModelFactory(repository)

        } else {
            val repository = getProductRepository(context, key)
            HomeViewPagerViewModelFactory(repository)
        }

    }


    fun provideWriteViewModelFactory(
        context: Context,
        key: CharArray?
    ): WriteViewModelFactory {
        val repository =
            getProductRepository(context, key)
        return WriteViewModelFactory(repository)
    }

    internal fun getProductRepository(
        context: Context,
        key: CharArray?
    ): ProductRepository {

        val reg = AppDatabase.getInstance(context.applicationContext, key)
        return ProductRepository.getInstance(
            reg.productDao()
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