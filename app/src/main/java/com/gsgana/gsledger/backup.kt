//setPriceColor(
//context!!,
//divAuValue,
//"pl",
//binding.realGoldPL,
//style = optionUpDown,
//plSwitch = plSwitch
//)
//setPriceColor(
//context!!,
//divAgValue,
//"pl",
//binding.realSilverPL,
//style = optionUpDown,
//plSwitch = plSwitch
//)
//
//
//}
//
//return binding.root
//}
//
//private fun setPriceColor(
//    context: Context,
//    price: Double,
//    type: String,
//    textView: TextView,
//    style: Int = 0,
//    plSwitch: Int = 0
//): Unit {
//
//    if (plSwitch == 0) {
//        textView.visibility = View.INVISIBLE
//        return
//    }
//
//    val white = ContextCompat.getColor(context!!, R.color.white)
//    val gray = ContextCompat.getColor(context!!, R.color.colorAccent)
//    val red = ContextCompat.getColor(context!!, R.color.mu1_data_down)
//    val green = ContextCompat.getColor(context!!, R.color.mu1_data_up)
//    val blue = ContextCompat.getColor(context!!, R.color.mu2_data_down)
//
//    val string = when (type) {
//        "priceint" -> {
//            String.format("%,.0f", price)
//        }
//
//        "pricefloat" -> {
//            String.format("%,.2f", price)
//        }
//
//        "pl" -> {
//            when {
//                price > 0.01 -> {
//                    if (style == 0) textView.setTextColor(green) else textView.setTextColor(
//                        red
//                    )
//                    "(+" + String.format("%,.2f", price) + "%)"
//                }
//                price < -0.01 -> {
//                    if (style == 0) textView.setTextColor(red) else textView.setTextColor(
//                        blue
//                    )
//                    "(" + String.format("%,.2f", price) + "%)"
//                }
//                else -> "( 0.00%)"
//            }
//        }
//        "pricepl" -> {
//            when {
//                price > 1 -> {
//                    if (style == 0) textView.setTextColor(green) else textView.setTextColor(
//                        red
//                    )
//                    "+" + String.format("%,.0f", price)
//                }
//                price < -1 -> {
//                    if (style == 0) textView.setTextColor(red) else textView.setTextColor(
//                        blue
//                    )
//                    "" + String.format("%,.0f", price)
//                }
//                else -> "0"
//            }
//        }
//        else -> {
//            ""
//        }
//    }
//    textView.text = string
//}









