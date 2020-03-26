package com.gsgana.gsledger.utilities

const val TOZ: Float = 31.10347f
const val KG: Int = 1000
const val DON: Float = 8.29425f

val BRAND = hashMapOf(
    "USEG" to "American Eagle",
    "USBF" to "American Buffalo",
    "RCMA" to "Canadian Maple Leaf",
    "RCMS" to "Canadian Moose",
    "RGBT" to "Great Britain Britannia",
    "RGQB" to "Queen's Beasts",
    "RGRA" to "Royal Arms",
    "RGLN" to "Great Britain Luna",
    "PAKR" to "Australian Kangaroo",
    "PALN" to "Australian Lunar",
    "MAPH" to "Austrian Philharmonic",
    "SDBT" to "Barbados Trident",
    "GENA" to "Noah's Ark",
    "CNPD" to "Panda",
    "KMTG" to "Korean Tiger",
    "KMZS" to "Korean Zisin",
    "BMLB" to "Mexican Libertad",
    "SAKR" to "Krugerrand"
)

val BRAND_ARRAY = BRAND.values.toTypedArray()

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
    "Pert Mint Minted",
    "Pert Mint Cast",
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
    "Sunshine Mint Coin",
    "Korean Zisin",
    "Korean Tiger",
    "Asahi Buffalo",
    "Mexican Libertad",
    "Armenia Noah's Ark",
    "Scottsdale Barbados Trident",
    "Pobjoy Pegasus",
    "Pobjoy Una and the Lion"
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
    "Pert Mint Cast",
    "Asahi minted",
    "Asahi Cast",
    "Geiger Edelmetalle Minted",
    "Sunshine Mint",
    "KOMSCO Cast",
    "Scottsdale Cast",
    "Scottsdale Minted",
    "Scottsdale Stacker Round",
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


//val CURRENCY_ARRAY = BRAND.values.toTypedArray()


val WEIGHTUNIT = arrayOf(
    "toz", "g", "kg", "don"
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
    "PMG"
)

//fun packageTypeTable(index: Int) = when (index) {
//    0 -> PACKAGETYPE[0]
//    1 -> PACKAGETYPE[1]
//    2 -> PACKAGETYPE[2]
//    3 -> PACKAGETYPE[3]
//    4 -> PACKAGETYPE[4]
//    5 -> PACKAGETYPE[5]
//    else -> PACKAGETYPE[0]
//}

//val GCMAP = hashMapOf(
//    "AAAAGC" to "Default",
//    "USBFGC" to "American Buffalo",
//    "USEGGC" to "American Eagle",
//    "RCMAGC" to "Canadian Maple Leaf",
//    "RCMSGC" to "Canadian Moose",
//    "RGBTGC" to "Great Britain Britannia",
//    "RGLNGC" to "Great Britain Lunar",
//    "RGQBGC" to "Queen's Beasts",
//    "RGRAGC" to "Royal Arm",
//    "PAKRGC" to "Australian Kangaroo",
//    "PALNGC" to "Australian Lunar",
//    "PASWGC" to "Australian Swan",
//    "MAPHGC" to "Austrian Philharmonic",
//    "SAKRGC" to "South African Krugerrand",
//    "KMZSGC" to "Korean Zisin",
//    "KMTGGC" to "Korean Tiger",
//    "CNPDGC" to "Chinese Panda",
//    "BMLBGC" to "Mexican Libertad",
//    "GENAGC" to "Armenia Noah's Ark",
//    "SDBTGC" to "Scottsdale Barbados Trident"
//)
//val GCTABLE = GCMAP.values.toTypedArray()
//
//val GBMAP = hashMapOf(
//    "AAAAGB" to "Default",
//    "PPFMGB" to "PAMP Fortuna Minted Bar",
//    "PPNCGB" to "PAMP Cast Bar",
//    "PPLNGB" to "PAMP Lunar Bar",
//    "PPLVGB" to "PAMP Love Bar",
//    "PPRSGB" to "PAMP Rosa Bar",
//    "PPLTGB" to "PAMP Liberty Bar",
//    "VCNMGB" to "Valcambi Minted Bar",
//    "VCNCGB" to "Valcambi Cast Bar",
//    "CSNBGB" to "Credit Suisse Minted Bar",
//    "RRNMGB" to "RMR Minted Bar",
//    "RRNCGB" to "RMR Cast Bar",
//    "RRBMGB" to "RMR Britannia Minted Bar",
//    "PANMGB" to "Pert Mint Bar Minited",
//    "PANCGB" to "Pert Mint Cast Bar",
//    "MANMGB" to "Austrian Mint Minted Bar",
//    "MANCGB" to "Austrian Mint Cast Bar",
//    "SMNMGB" to "Sunshine Mint Bar",
//    "LSNMGB" to "LS-Nikko Minted Bar",
//    "LSNCGB" to "LS-Nikko Cast Bar",
//    "KMLNGB" to "KOMSCO Lunar Bar",
//    "KMORGB" to "KOMSCO Orodt Bar",
//    "ASNMGB" to "Asahi minted Bar",
//    "ASNCGB" to "Asahi Cast Bar",
//    "GENCGB" to "Geiger Edelmetalle Cast Bar",
//    "GENMGB" to "Geiger Edelmetalle Minted Bar",
//    "SDNCGB" to "Scottsdale Cast Bar",
//    "SDNMGB" to "Scottsdale Minted Bar",
//    "SDMQGB" to "Scottsdale Marquee Bar",
//    "SDLNGB" to "Scottsdale Lunar Bar"
//)
//val GBTABLE = GBMAP.values.toTypedArray()
//
//val SCMAP = hashMapOf(
//    "AAAASC" to "Default",
//    "USEGSC" to "American Eagle",
//    "RCMAGC" to "Canadian Maple Leaf",
//    "RGBTSC" to "Great Britain Britannia",
//    "RGLNSC" to "Great Britain Lunar",
//    "RGQBSC" to "Queen's Beasts",
//    "RGRASC" to "Royal Arm",
//    "PAKRSC" to "Australian Kangaroo",
//    "PALNSC" to "Australian Lunar",
//    "PAKLSC" to "Australian Koala",
//    "PAKBSC" to "Australian Kookaburra",
//    "MAPHSC" to "Austrian Philharmonic",
//    "SAKRSC" to "South African Krugerrand",
//    "SMNNSC" to "Sunshine Mint Coin",
//    "KMZSSC" to "Korean Zisin",
//    "KMTGGC" to "Korean Tiger",
//    "ASBFSC" to "Asahi Buffalo",
//    "BMLBSC" to "Mexican Libertad",
//    "GENASC" to "Armenia Noah's Ark",
//    "SDBTSC" to "Scottsdale Barbados Trident",
//    "PJPSSC" to "Pobjoy Pegasus",
//    "PJULSC" to "Pobjoy Una and the Lion"
//)
//val SCTABLE = SCMAP.values.toTypedArray()
//
//val SBMAP = hashMapOf(
//    "AAAASB" to "Default",
//    "PPFMSB" to "PAMP Fortuna Minted Bar",
//    "PPNCSB" to "PAMP Cast Bar",
//    "PPLNGB" to "PAMP Lunar Bar",
//    "PPLVGB" to "PAMP Love Bar",
//    "PPRSGB" to "PAMP Rosa Bar",
//    "VCNMSB" to "Valcambi Minted Bar",
//    "VCNCSB" to "Valcambi Cast Bar",
//    "CSNBSB" to "Credit Suisse Minted Bar",
//    "RRNMSB" to "RMR Minted Bar",
//    "RRNCSB" to "RMR Cast Bar",
//    "RRBMSB" to "RMR Britannia Minted Bar",
//    "PANCSB" to "Pert Mint Cast Bar",
//    "ASNMSB" to "Asahi minted Bar",
//    "ASNCSB" to "Asahi Cast Bar",
//    "GENMGB" to "Geiger Edelmetalle Minted Bar",
//    "SMNMSB" to "Sunshine Mint Bar",
//    "KMNCSB" to "KOMSCO Cast Bar",
//    "SDNCSB" to "Scottsdale Cast Bar",
//    "SDNMSB" to "Scottsdale Minted Bar",
//    "SDSRSB" to "Scottsdale Stacker Round"
//)
//val SBTABLE = SBMAP.values.toTypedArray()