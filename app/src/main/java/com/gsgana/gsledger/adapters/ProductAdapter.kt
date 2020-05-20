package com.gsgana.gsledger.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
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

class ProductAdapter(private val context: Context, private val realData: Map<String, Double>) :
    ListAdapter<Product, ProductAdapter.ViewHolder>(ProductDiffCallback()) {

    private lateinit var brand: String
    private lateinit var metal: String
    private lateinit var type: String
    private lateinit var weight: String

    private val PREF_NAME = "01504f779d6c77df04"
    private val PL = "18xRWR1PDWW01PjjXI"
    private val UPDOWN = "17RD79dX7d1DWf0j0I"

    private val plSwitch =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(PL, 0)

    private val plStlye =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(UPDOWN, 1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_product, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, getItem(position), realData, plSwitch, plStlye)
    }

    class ViewHolder(
        private val binding: ListItemProductBinding
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
            if (it.findNavController().currentDestination?.id == R.id.homeViewPagerFragment) {
                it.findNavController().navigate(direction)
            }
        }

        @SuppressLint("DefaultLocale", "SetTextI18n")
        fun bind(
            context: Context,
            item: Product,
            realData: Map<String, Double>,
            plSwitch: Int,
            plStlye: Int
        ) {
            with(binding) {
                viewModel = ProductsViewModel(item)
                binding.product = item
                var brand = item.brand.toLowerCase()
                val metal = METAL[item.metal].toLowerCase()
                val type = TYPE[item.type].toLowerCase()
                val metalType = METAL[product!!.metal] + " " + TYPE[product!!.type]
                var weight = 0
                val metalPrice = realData[METALCODE[item.metal]]!!.toFloat()
                val pl: Float
                val currency = realData[CURRENCY[item.currency]]!!.toFloat()
                val memo = item.memo.replace("\n", " ")
                val realPrice =
                    metalPrice * (1 + item.reg) * PACKAGENUM[item.packageType] * item.quantity * item.weightr * item.weight
                val price = item.prePrice / currency

                val brandName = if (product!!.brand == "Default") {
                    ""
                } else {
                    product!!.brand
                }

                pl = ((realPrice - price) / price) * 100f

                binding.productItemPlCurrency.text = CURRENCYSYMBOL[item.currency]

                binding.productItemTotalprice.text =
                    CURRENCYSYMBOL[item.currency] + " " + priceToString(
                        (realPrice * currency).toDouble(),
                        "PriceInt"
                    )

                setPriceColor(
                    context,
                    pl.toDouble(),
                    "pl",
                    binding.productItemPl,
                    plSwitch,
                    plStlye
                )

                var weightText: String = ""
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
                    0.05f -> {
                        weightText = "1/20"
                    }
                    0.1f -> {
                        weightText = "1/10"
                    }
                    0.25f -> {
                        weightText = "1/4"
                    }
                    0.5f -> {
                        weightText = "1/2"
                    }
                }

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

                if (weight < 1) {
                    binding.productItemType.text =
                        product!!.year.toString() + " " + weightText + WEIGHTUNIT[product!!.weightUnit] + " " + METAL[product!!.metal] + " " + brandName + " " + TYPE[product!!.type]
                } else {
                    binding.productItemType.text =
                        product!!.year.toString() + " " + weight.toString() + WEIGHTUNIT[product!!.weightUnit] + " " + METAL[product!!.metal] + " " + brandName + " " + TYPE[product!!.type]
                }

                brand = brand.toLowerCase().replace(" ", "").replace("'", "")
                    .replace(".", "").replace("-", "")

                val imgId = getResource(
                    "drawable",
                    "${brand}_${metal[0]}${type[0]}",
                    context
                )
                if (imgId == 0) {
                    binding.itemImage.setImageResource(
                        getResource(
                            "drawable",
                            "ic_default_${metal}${type}",
                            context
                        )
                    )
                } else {
                    binding.itemImage.setImageResource(imgId)
                }

                val grade = item.grade
                val gradeNum = item.gradeNum.toString()
                if (grade != "None") {
                    if (grade.substring(0..3) == "PCGS") {
                        binding.certImage.setImageResource(R.drawable.ic_pg)
                    } else if (grade.substring(0..2) == "NGC") {
                        binding.certImage.setImageResource(R.drawable.ic_ngc)
                    }
                    binding.certLabel.text = "${grade} ${gradeNum}"
                }
                executePendingBindings()
            }
        }

        private fun priceToString(price: Double, type: String): String {

            return when (type) {
                "PriceInt" -> {
                    String.format("%,.0f", price)
                }

                "PriceFloat" -> {
                    String.format("%,.2f", price)
                }

                "Pl" -> {
                    when {
                        price > 0.01 -> {
                            "(+" + String.format("%,.2f", price) + "%)"
                        }
                        price < -0.01 -> {
                            "(" + String.format("%,.2f", price) + "%)"
                        }
                        else -> "( 0.00%)"
                    }
                }
                "PricePl" -> {
                    when {
                        price > 1 -> {
                            "+" + String.format("%,.0f", price)
                        }
                        price < -1 -> {
                            "" + String.format("%,.0f", price)
                        }
                        else -> "0"
                    }
                }

                else -> {

                    ""
                }
            }
        }

        private fun setPriceColor(
            context: Context,
            price: Double,
            type: String,
            textView: TextView,
            style: Int = 0,
            plSwitch: Int = 1
        ): Int {

            if (plSwitch == 0) {
                textView.visibility = View.INVISIBLE
                return 0
            }
//        val white = ContextCompat.getColor(context, R.color.white)
//        val gray = ContextCompat.getColor(context, R.color.colorAccent)
            val red = ContextCompat.getColor(context, R.color.mu1_data_down)
            val green = ContextCompat.getColor(context, R.color.mu1_data_up)
            val blue = ContextCompat.getColor(context, R.color.mu2_data_down)

            val string = when (type) {
                "priceint" -> {
                    String.format("%,.0f", price)
                }

                "pricefloat" -> {
                    String.format("%,.2f", price)
                }

                "pl" -> {
                    when {
                        price > 0.01 -> {
                            if (style == 0) textView.setTextColor(green) else textView.setTextColor(
                                red
                            )
                            "(+" + String.format("%,.2f", price) + "%)"
                        }
                        price < -0.01 -> {
                            if (style == 0) textView.setTextColor(red) else textView.setTextColor(
                                blue
                            )
                            "(" + String.format("%,.2f", price) + "%)"
                        }
                        else -> "( 0.00%)"
                    }
                }
                "pricepl" -> {
                    when {
                        price > 1 -> {
                            if (style == 0) textView.setTextColor(green) else textView.setTextColor(
                                red
                            )
                            "+" + String.format("%,.0f", price)
                        }
                        price < -1 -> {
                            if (style == 0) textView.setTextColor(red) else textView.setTextColor(
                                blue
                            )
                            "" + String.format("%,.0f", price)
                        }
                        else -> "0"
                    }
                }
                else -> {
                    ""
                }
            }
            textView.text = string
            return 1
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
