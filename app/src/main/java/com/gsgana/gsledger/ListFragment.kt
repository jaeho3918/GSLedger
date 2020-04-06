package com.gsgana.gsledger

import android.os.Bundle
import android.provider.Contacts.SettingsColumns.KEY
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import com.gsgana.gsledger.adapters.ProductAdapter
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.ListFragmentBinding
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModelFactory
import com.gsgana.gsledger.viewmodels.WriteViewModel


class ListFragment : Fragment() {

    private lateinit var product: Product
    private lateinit var binding: ListFragmentBinding
    private val KEY = "Kd6c26TK65YSmkw6oU"

    private lateinit var mAuth: FirebaseAuth
    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b"
    private lateinit var viewModelFactory: HomeViewPagerViewModelFactory
    private lateinit var rgl: MutableList<Char>

    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(requireActivity(), null)
    }

    private lateinit var realData: Map<String, Double>
    private lateinit var adapter: ProductAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ListFragmentBinding.inflate(inflater, container, false)

        realData = viewModel.realData.value ?: mapOf()

        adapter = ProductAdapter(requireContext(), realData)
        binding.productList.adapter = adapter
        binding.callback = object : Callback {
            override fun add() {
                val randomStringInt = createRandomStringInt()
                product = Product(null)
//                product.brand = randomStringInt[1] as String
                product.metal = randomStringInt[0] as Int
                product.type = randomStringInt[0] as Int
                product.packageType = randomStringInt[0] as Int
                product.quantity = randomStringInt[0] as Int
                product.weight = (randomStringInt[1] as Int).toFloat()
                product.weightUnit = randomStringInt[0] as Int
                product.currency = randomStringInt[0] as Int
                product.price = (randomStringInt[1] as Int).toFloat()
                product.buyDate = randomStringInt[2] as String
                product.editDate = randomStringInt[3] as String
                product.memo = randomStringInt[2] as String

                viewModel.addProduct(product)
            }

            override fun del() {
                viewModel.deleteProduct()
            }
        }

        binding.writeBtn.setOnClickListener {
            findNavController()
                .navigate(R.id.action_homeViewPagerFragment_to_write1Fragment)
        }

        viewModel.getProducts().observe(viewLifecycleOwner) { result ->
            adapter.submitList(result)
        }
        return binding.root
    }

    interface Callback {
        fun add()
        fun del()
    }
}

fun createRandomStringInt(): List<Any> {
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val STRING_LENGTH = 10
    val randomInt = kotlin.random.Random.nextInt(0, 3)
    val randomInt2 = kotlin.random.Random.nextInt(0, 10)

    val randomString = (1..STRING_LENGTH)
        .map { kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")

    val randomString2 = (1..STRING_LENGTH)
        .map { kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")

    return listOf(randomInt, randomInt2, randomString, randomString2)
}