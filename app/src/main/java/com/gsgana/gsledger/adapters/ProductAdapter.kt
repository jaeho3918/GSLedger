package com.gsgana.gsledger.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gsgana.gsledger.HomeViewPagerFragmentDirections
import com.gsgana.gsledger.R
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.ListItemProductBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.ProductsViewModel
import java.lang.Exception

class ProductAdapter(private val context: Context, private val realData: Map<String, Double>) :
    ListAdapter<Product, ProductAdapter.ViewHolder>(ProductDiffCallback()) {
    private lateinit var brand: String
    private lateinit var metal: String
    private lateinit var type: String
    private lateinit var metalType: String
    private lateinit var weight: String

    private val PREF_NAME = "01504f779d6c77df04"
    private val PL = "18xRWXR1PDWaSW0jXI"

    private val plSwitch =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(PL, 0)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_product, parent, false
            ), context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, getItem(position), realData)
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

        fun bind(context: Context, item: Product, realData: Map<String, Double>) {
            with(binding) {
                viewModel = ProductsViewModel(item)
                binding.product = item
                val brand = item.brand.toLowerCase().replace(" ", "")
                val metal = METAL[item.metal].toLowerCase()
                val type = TYPE[item.type].toLowerCase()
                val metalType = METAL[product!!.metal] + " " + TYPE[product!!.type]
                var weight = 0
                val metalPrice = realData[METALCODE[item.metal]]!!.toFloat()
                var pl = 0f
                val currency = realData[CURRENCY[item.currency]]!!.toFloat()
                val memo = item.memo.replace("\n", " ")
                val realPrice =
                    metalPrice * (1 + item.reg) * PACKAGENUM[item.packageType] * item.quantity * item.weightr * item.weight

                val price = item.prePrice / currency

                pl = ((realPrice - price) / realPrice) * 100f

//                pl = when (item.currency) {
//                    0 -> {
//                        (price / (metalPrice) - 1) * 100f
//                    }
//                    else -> {
//                        (price / (metalPrice * currency) - 1) * 100f
//                    }
//                }

                binding.productItemPl.text = "(" + String.format("%,.2f", pl) + "%)"
                pl = 0f


                when (item.weight) {
                    1.0f -> {
                        weight = item.weight.toInt()
                    }
                    2.0f -> {
                        weight = item.weight.toInt()
                    }
                    3.0f -> {
                        weight = item.weight.toInt()
                    }
                    5.0f -> {
                        weight = item.weight.toInt()
                    }
                    10.0f -> {
                        weight = item.weight.toInt()
                    }
                    100.0f -> {
                        weight = item.weight.toInt()
                    }
                    500.0f -> {
                        weight = item.weight.toInt()
                    }
                }
                val test = item.memo.length
                when {
                    memo.length in 1..18 -> {
                        binding.productItemMemo.text = "Memo : $memo"
                    }
                    memo.length > 18 -> {
                        binding.productItemMemo.text = "Memo : ${memo.substring(0..18)} ..."
                    }
                    else -> {
                        binding.productItemMemo.visibility = View.GONE
                    }
                }

                if (weight == 0) {
                    binding.productItemType.text =
                        metalType + "   " + item.weight + WEIGHTUNIT[product!!.weightUnit]
                } else {
                    binding.productItemType.text =
                        metalType + "   " + weight + WEIGHTUNIT[product!!.weightUnit]
                }


                val imgId = getResource(
                    "drawable",
                    "${brand}_${metal}${type}",
                    context
                )
                if (imgId == 0) {
                    binding.itemImage.setImageResource(
                        getResource(
                            "drawable",
                            "default_goldbar",
                            context
                        )//"Default_${METAL[item.metal]}${TYPE[item.type]}"
                    )
                } else {
                    binding.itemImage.setImageResource(imgId)
                }

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
