@file:Suppress("DEPRECATION")

package com.gsgana.gsledger

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.gsgana.gsledger.databinding.FragmentWrite1Binding
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.viewmodels.WriteViewModel

class Write1Fragment : Fragment() {

    private lateinit var binding: FragmentWrite1Binding
    private var goldCoinSwitch = false
    private var goldBarSwitch = false
    private var silverCoinSwitch = false
    private var silverBarSwitch = false

    private var goldTextColor = 0
    private var silverTextColor = 0

    private var white :Int?= null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        goldTextColor = resources.getColor(
            R.color.font_gold,
            null
        )

        silverTextColor = resources.getColor(
            R.color.font_silver,
            null
        )

        white = resources.getColor(
            R.color.white,
            null
        )


        val viewModel =
            ViewModelProviders.of(
                activity!!,
                InjectorUtils.provideWriteViewModelFactory(activity!!, null)
            )
                .get(WriteViewModel::class.java)

        viewModel.initProduct()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_write1, container, false
        )

        binding.layout1r.setOnClickListener {
            findNavController().navigate(R.id.action_write1Fragment_to_homeViewPagerFragment)
            viewModel.initProduct()

        }
        binding.layout1.setOnClickListener {
            findNavController().navigate(R.id.action_write1Fragment_to_homeViewPagerFragment)
            viewModel.initProduct()
        }

        binding.moveTo1.setOnClickListener {
            viewModel.getProduct().metal = viewModel.getmetalField1()?:0
            viewModel.getProduct().type = viewModel.gettypeField1()?:0
            findNavController().navigate(R.id.action_write1Fragment_to_write2Fragment)

        }
        binding.goldCoin.setOnClickListener {
            binding.moveTo1.setTextColor(white!!)
            binding.moveTo1.isEnabled=true
            goldCoinSwitch = true
            viewModel.setmetalField1(0)
            viewModel.settypeField1(0)
            binding.stateLabel.text = "Gold Coin"
            binding.stateLabel.setTextColor(goldTextColor)
            binding.goldCoin.setImageDrawable(
                resources.getDrawable(
                    R.drawable.ic_default_goldcoin,
                    null
                )
            )

            when {
                goldBarSwitch -> binding.goldBar.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_default_goldbar_none,
                        null
                    )
                )

                silverCoinSwitch -> binding.silverCoin.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_default_silvercoin_none,
                        null
                    )
                )
                silverBarSwitch -> binding.silverBar.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_default_silverbar_none,
                        null
                    )
                )
            }
            goldBarSwitch = false
            silverCoinSwitch = false
            silverBarSwitch = false
        }

        binding.goldBar.setOnClickListener {
            binding.moveTo1.setTextColor(white!!)
            binding.moveTo1.isEnabled=true
            goldBarSwitch = true
            viewModel.setmetalField1(0)
            viewModel.settypeField1(1)
            binding.stateLabel.text = "Gold Bar"
            binding.stateLabel.setTextColor(goldTextColor)
            binding.goldBar.setImageDrawable(
                resources.getDrawable(
                    R.drawable.ic_default_goldbar,
                    null
                )
            )
            when {
                goldCoinSwitch -> binding.goldCoin.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_default_goldcoin_none,
                        null
                    )
                )
                silverCoinSwitch -> binding.silverCoin.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_default_silvercoin_none,
                        null
                    )
                )
                silverBarSwitch -> binding.silverBar.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_default_silverbar_none,
                        null
                    )
                )
            }
            goldCoinSwitch = false
            silverCoinSwitch = false
            silverBarSwitch = false
        }

        binding.silverCoin.setOnClickListener {
            binding.moveTo1.setTextColor(white!!)
            binding.moveTo1.isEnabled=true
            silverCoinSwitch = true
            viewModel.setmetalField1(1)
            viewModel.settypeField1(0)
            binding.stateLabel.text = "Silver Coin"
            binding.stateLabel.setTextColor(silverTextColor)
            binding.silverCoin.setImageDrawable(
                resources.getDrawable(
                    R.drawable.ic_default_silvercoin,
                    null
                )
            )

            when {
                goldBarSwitch -> binding.goldBar.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_default_goldbar_none,
                        null
                    )
                )
                goldCoinSwitch -> binding.goldCoin.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_default_goldcoin_none,
                        null
                    )
                )
                silverBarSwitch -> binding.silverBar.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_default_silverbar_none,
                        null
                    )
                )
            }
            goldBarSwitch = false
            goldCoinSwitch = false
            silverBarSwitch = false
        }

        binding.silverBar.setOnClickListener {
            binding.moveTo1.setTextColor(white!!)
            binding.moveTo1.isEnabled=true
            silverBarSwitch = true
            viewModel.setmetalField1(1)
            viewModel.settypeField1(1)
            binding.stateLabel.text = "Silver Bar"
            binding.stateLabel.setTextColor(silverTextColor)
            binding.silverBar.setImageDrawable(
                resources.getDrawable(
                    R.drawable.ic_default_silverbar,
                    null
                )
            )

            when {
                goldBarSwitch -> binding.goldBar.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_default_goldbar_none,
                        null
                    )
                )
                goldCoinSwitch -> binding.goldCoin.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_default_goldcoin_none,
                        null
                    )
                )
                silverCoinSwitch -> binding.silverCoin.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_default_silvercoin_none,
                        null
                    )
                )
            }
            goldBarSwitch = false
            goldCoinSwitch = false
            silverCoinSwitch = false
        }


        return binding.root
    }
}

