package com.gsgana.gsledger.utilities

val PREF_NAME = "01504f779d6c77df04"
val TODAY_NAME = "0d07f05fd0c595f615"
val KEY = "Kd6c26TK65YSmkw6oU"
val CURR_NAME = "1w3d4f7w9d2qG2eT36"
val WEIGHT_NAME = "f79604050dfc500715"

val GCTABLE = arrayOf(
    "Default",
    "American Eagle",
    "American Buffalo",
    "Canadian Maple Leaf",
    "Canadian Moose",
    "Great Britain Britannia",
    "Great Britain Lunar",
    "Great Britain Queen's Beasts",
    "Great Britain Royal Arm",
    "Australian Kangaroo",
    "Australian Lunar",
    "Australian Rectangle Dragon",
    "Austrian Philharmonic",
    "South African Krugerrand",
    "Korean Zisin",
//    "Korean Tiger",
    "Chinese Panda",
    "Mexican Libertad",
    "Armenia Noah's Ark",
    "Scottsdale Barbados Trident"
)

val SCTABLE = arrayOf(
    "Default",
    "American Eagle",
    "Canadian Maple Leaf",
    "Great Britain Britannia",
    "Great Britain Lunar",
    "Great Britain Queen's Beasts",
    "Great Britain Royal Arm",
    "Australian Kangaroo",
    "Australian Lunar",
    "Australian Koala",
    "Australian Kookaburra",
    "Australian Rectangle Dragon",
    "Austrian Philharmonic",
    "South African Krugerrand",
    "Sunshine Round",
    "Sunshine Buffalo",
    "Chinese Panda",
//    "Korean Zisin",
    "Korean Tiger",
    "Asahi Buffalo",
    "Mexican Libertad",
    "Armenia Noah's Ark",
    "Scottsdale Barbados Trident",
    "Pobjoy Pegasus"
//    "Pobjoy Una and the Lion"
)

val GBTABLE = arrayOf(
    "Default",
    "PAMP Fortuna Minted",
    "PAMP Cast",
//    "PAMP Lunar",
//    "PAMP Love",
//    "PAMP Rosa",
//    "PAMP Liberty",
    "Valcambi Minted",
    "Valcambi Cast",
    "Credit Suisse Minted",
    "RMR Minted",
    "RMR Cast",
    "RMR Britannia Minted",
    "Perth Mint Minted",
    "Perth Mint Cast",
//    "Austrian Mint Minted",
//    "Austrian Mint Cast",
    "Muenze Oesterreich Cast",
    "Muenze Oesterreich Minted",
    "Geiger Edelmetalle Minted",
    "Sunshine Minted",
    "LS-Nikko Minted",
    "LS-Nikko Cast",
//    "KOMSCO Lunar",
    "KOMSCO Orodt",
    "KOREA Exchange Minted",
    "Asahi Minted",
    "Asahi Cast",
    "Scottsdale Cast",
    "Scottsdale Minted",
    "Scottsdale Marquee",
    "Scottsdale Lunar",
    "Heraeus Cast"
)

val SBTABLE = arrayOf(
    "Default",
    "PAMP Fortuna Minted",
    "PAMP Cast",
//    "PAMP Lunar",
//    "PAMP Love",
//    "PAMP Rosa",
    "Valcambi Minted",
    "Valcambi Cast",
    "KOMSCO Minted",
    "Credit Suisse Minted",
    "RMR Minted",
    "RMR Cast",
    "RMR Britannia Minted",
    "Perth Mint Cast",
    "Asahi Minted",
    "Asahi Cast",
    "Geiger Edelmetalle Minted",
    "Sunshine Minted",
    "Scottsdale Cast",
    "Scottsdale Minted",
    "Scottsdale Stacker",
    "Heraeus Cast"
)

val TYPE = arrayOf(
    "Coin",
    "Bar"
)

//fun typeTable(index: Int) = when (index) {
//    0 -> TYPE[0]
//    1 -> TYPE[1]
//    else -> TYPE[0] //Coin
//}

val METAL = arrayOf(
    "Gold",
    "Silver",
    "Paladium"
)

val METALCODE = arrayOf(
    "AU",
    "AG",
    "PA"
)

//fun metalTable(index: Int) = when (index) {
//    0 -> METAL[0]
//    1 -> METAL[1]
//    2 -> METAL[2]
//    else -> METAL[0] //Gold
//}

val CURRENCY = arrayOf(
    "USD", "GBP", "INR", "EUR", "JPY", "KRW", "CNY", "CAD", "AUD"
)

val CURRENCYANDSYMBOL = arrayOf(
    "USD: \$", "GBP: ￡", "INR: ₹", "EUR: €", "JPY: ¥", "KRW: ₩", "CNY: Ұ", "CAD: C\$", "AUD: A\$"
)

val CURRENCYSYMBOL = arrayOf(
    "$", "￡", "₹", "€", "¥", "₩", "Ұ", "$", "$"
)

val ALERTSWITCH = arrayOf(
    "Off", "On"
)
val ALERTRANGE = arrayOf(
    "± 1.0% ", "± 2.0%", "± 3.0%"
)


//val LANGUAGE = arrayOf(
//    "English", "Japanese", "Chinese", "Korean"
//)

//val CURRENCYSYMBOL = arrayOf(
//    "$", "￡", "€", "¥", "Ұ", "₩", "C$", "A$", "₹"
//)


val WEIGHTUNIT = arrayOf(
    "oz", "g", "kg", "don"
)

fun weightUnitTable(index: Int) = when (index) {
    0 -> WEIGHTUNIT[0]
    1 -> WEIGHTUNIT[1]
    2 -> WEIGHTUNIT[2]
    3 -> WEIGHTUNIT[3]
    else -> WEIGHTUNIT[0] // t oz
}


val PACKAGETYPE = arrayOf(
    "1",
    "Tube(10)",
    "Tube(20)",
    "Tube(25)",
    "MonsterBox(250)",
    "MonsterBox(500)"
)

val PACKAGENUM = arrayOf(
    1,
    10,
    20,
    25,
    250,
    500
)


val GRADE = arrayOf(
    "None",
    "PCGS MS",
    "PCGS PR",
    "NGC MS",
    "NGC PF"
)