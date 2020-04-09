package com.gsgana.gsledger.utilities

const val TOZ: Float = 31.10347f
const val KG: Int = 1000
const val DON: Float = 8.29425f

val GCTABLE = arrayOf(
    "Default",
    "American Buffalo",
    "American Eagle",
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

fun typeTable(index: Int) = when (index) {
    0 -> TYPE[0]
    1 -> TYPE[1]
    else -> TYPE[0] //Coin
}

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

fun metalTable(index: Int) = when (index) {
    0 -> METAL[0]
    1 -> METAL[1]
    2 -> METAL[2]
    else -> METAL[0] //Gold
}

val CURRENCY = arrayOf(
    "USD", "GBP", "EUR", "JPY", "CNY", "KRW", "CAD", "AUD", "INR"
)

val CURRENCYSYMBOL = arrayOf(
    "$", "￡", "€", "¥", "¥", "₩", "$", "$", "₹"
)

val LANGUAGE = arrayOf(
    "English", "Japanese", "Chinese", "Korean"
)

//val CURRENCYSYMBOL = arrayOf(
//    "$", "￡", "€", "¥", "Ұ", "₩", "C$", "A$", "₹"
//)


val WEIGHTUNIT = arrayOf(
    "oz", "g", "kg", "don"
)

val WEIGHTUNITBRAND = arrayOf(
    " oz", " g", " kg", " don"
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