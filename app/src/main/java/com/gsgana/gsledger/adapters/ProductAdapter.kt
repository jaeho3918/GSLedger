package com.gsgana.gsledger.adapters

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gsgana.gsledger.HomeViewPagerFragmentDirections
import com.gsgana.gsledger.R
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.data.Products
import com.gsgana.gsledger.databinding.ListItemProductBinding
import com.gsgana.gsledger.viewmodels.ProductsViewModel

class ProductAdapter(private val context: Context) :
    ListAdapter<Product, ProductAdapter.ViewHolder>(ProductDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder( DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_product, parent, false
            ), context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemProductBinding,
        context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener { view ->
                binding.product?.let { product ->
                    navigateToProduct(product, view)
                }
            }

            binding.itemImage.setImageResource(getResource(
                "drawable",
                "eagle_gc",
                context
            )
            )


        }

        private fun navigateToProduct(
            product: Product,
            it: View
        ) {
            val direction =
                HomeViewPagerFragmentDirections.actionHomeViewPagerFragmentToDetailFragment(
                    product.id!!
                )
            it.findNavController().navigate(direction)
        }

        fun bind(item: Product) {
            with(binding) {
                viewModel = ProductsViewModel(item)
                binding.product = item
                executePendingBindings()
            }
        }
        private fun getResource(type: String, resName: String, context: Context): Int {

            val resContext: Context = context.createPackageContext(context.packageName, 0)
            val res: Resources = resContext.resources
            val id: Int = res.getIdentifier(resName, type, context.packageName)

            return id
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem == newItem
        }
    }

}
