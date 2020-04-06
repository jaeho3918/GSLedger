package com.gsgana.gsledger.utilities

const val TOZ: Float = 31.10347f
const val KG: Int = 1000
const val DON: Float = 8.29425f

//val BRAND = hashMapOf(
//    "USEG" to "American Eagle",
//    "USBF" to "American Buffalo",
//    "RCMA" to "Canadian Maple Leaf",
//    "RCMS" to "Canadian Moose",
//    "RGBT" to "Great Britain Britannia",
//    "RGQB" to "Queen's Beasts",
//    "RGRA" to "Royal Arms",
//    "RGLN" to "Great Britain Luna",
//    "PAKR" to "Australian Kangaroo",
//    "PALN" to "Australian Lunar",
//    "MAPH" to "Austrian Philharmonic",
//    "SDBT" to "Barbados Trident",
//    "GENA" to "Noah's Ark",
//    "CNPD" to "Panda",
//    "KMTG" to "Korean Tiger",
//    "KMZS" to "Korean Zisin",
//    "BMLB" to "Mexican Libertad",
//    "SAKR" to "Krugerrand"
//)

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
    "Australian Swan",
    "Austrian Philharmonic",
    "South African Krugerrand",
    "Korean Zisin",
    "Korean Tiger",
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
    "Austrian Philharmonic",
    "South African Krugerrand",
    "Sunshine Round",
    "Sunshine Buffalo",
    "Korean Zisin",
    "Korean Tiger",
    "Asahi Buffalo",
    "Mexican Libertad",
    "Armenia Noah's Ark",
    "Scottsdale Barbados Trident",
    "Pobjoy Pegasus",
    "Pobjoy Una and the Lion"
)

val GBTABLE = arrayOf(
    "Default",
    "PAMP Fortuna Minted",
    "PAMP Cast",
    "PAMP Lunar",
    "PAMP Love",
    "PAMP Rosa",
    "PAMP Liberty",
    "Valcambi Minted",
    "Valcambi Cast",
    "Credit Suisse Minted",
    "RMR Minted",
    "RMR Cast",
    "RMR Britannia Minted",
    "Perth Mint Minted",
    "Perth Mint Cast",
    "Austrian Mint Minted",
    "Austrian Mint Cast",
    "Geiger Edelmetalle Cast",
    "Geiger Edelmetalle Minted",
    "Sunshine Mint",
    "LS-Nikko Minted",
    "LS-Nikko Cast",
    "Asahi Minted",
    "Asahi Cast",
    "KOMSCO Lunar",
    "KOMSCO Orodt",
    "Scottsdale Cast",
    "Scottsdale Minted",
    "Scottsdale Marquee",
    "Scottsdale Lunar",
    "Heraeus Cast",
    "Heraeus Minted"
)

val SBTABLE = arrayOf(
    "Default",
    "PAMP Fortuna Minted",
    "PAMP Cast",
    "PAMP Lunar",
    "PAMP Love",
    "PAMP Rosa",
    "Valcambi Minted",
    "Valcambi Cast",
    "Credit Suisse Minted",
    "RMR Minted",
    "RMR Cast",
    "RMR Britannia Minted",
    "Perth Mint Cast",
    "Asahi minted",
    "Asahi Cast",
    "Geiger Edelmetalle Minted",
    "Sunshine Mint",
    "KOMSCO Cast",
    "Scottsdale Cast",
    "Scottsdale Minted",
    "Heraeus Cast",
    "Heraeus Minted"
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
    "toz", "g", "kg", "don"
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