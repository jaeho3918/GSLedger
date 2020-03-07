package com.gsgana.gsledger

//                    val divAuValue = ((it["AU"] ?: 0.0) - (it["YESAU"] ?: 0.0)) / (it["AU"] ?: 0.0)
//                    val divAgValue = ((it["AG"] ?: 0.0) - (it["YESAG"] ?: 0.0)) / (it["AG"] ?: 0.0)
//                    when {
//                        divAuValue > 0 -> {
//                            binding.realGoldPrice.setText(
//                                "(" + "+" + String.format(
//                                    " %.2f",
//                                    divAuValue
//                                ) + "%)"
//                            )
//                        }
//                        divAuValue < 0 -> {
//                            binding.realGoldPrice.setText(
//                                "(" + "-" + String.format(
//                                    " %.2f",
//                                    -1 * divAuValue
//                                ) + "%)"
//                            )
//                        }
//                        else -> {
//                            binding.realGoldPrice.setText("( 0.00%)");
//                        }
//                    }
//                    when {
//                        divAgValue > 0 -> {
//                            binding.realSilverPrice.setText(
//                                "(" + "+" + String.format(
//                                    " %.2f",
//                                    divAgValue
//                                ) + "%)"
//                            )
//                        }
//                        divAgValue < 0 -> {
//                            binding.realSilverPrice.setText(
//                                "(" + "-" + String.format(
//                                    " %.2f",
//                                    -1 * divAgValue
//                                ) + "%)"
//                            )
//                        }
//                        else -> {
//                            binding.realSilverPrice.setText("( 0.00%)")
//                        }
//                    }
//                    if (preAu!! > (it["AU"]?.toInt()!!)) {
//                        ObjectAnimator.ofObject(
//                            binding.realGoldPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            white,
//                            red
//                        )
//                            .setDuration(duration)
//                            .start();
//                        ObjectAnimator.ofObject(
//                            binding.realGoldPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            red,
//                            white
//                        )
//                            .setDuration(duration)
//                            .start();
//                    } else if (preAu == null) {
//                        preAu = (it["AU"]?.toInt()!!)
//                    } else {
//                        ObjectAnimator.ofObject(
//                            binding.realGoldPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            white,
//                            green
//                        )
//                            .setDuration(duration)
//                            .start();
//                        ObjectAnimator.ofObject(
//                            binding.realGoldPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            green,
//                            white
//                        )
//                            .setDuration(duration)
//                            .start();
//                    }
//                    if (preAg!! > (it["AG"]?.toInt()!!)) {
//                        ObjectAnimator.ofObject(
//                            binding.realSilverPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            white,
//                            red
//                        )
//                            .setDuration(duration)
//                            .start();
//                        ObjectAnimator.ofObject(
//                            binding.realSilverPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            red,
//                            white
//                        )
//                            .setDuration(duration)
//                            .start();
//                    } else if (preAg == null) {
//                        preAg = (it["AG"]?.toInt()!!);
//                    } else {
//                        ObjectAnimator.ofObject(
//                            binding.realSilverPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            white,
//                            green
//                        )
//                            .setDuration(duration)
//                            .start();
//                        ObjectAnimator.ofObject(
//                            binding.realSilverPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            green,
//                            white
//                        )
//                            .setDuration(duration)
//                            .start();
//                    }

/////////////////////////////////////////////////////////////////////////////////////////////