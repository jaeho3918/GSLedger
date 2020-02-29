package com.gsna.gsnote.data

import android.os.Parcel
import android.os.Parcelable

class ProductParcel(
    var id: Long?,
    var brand: String?,
    var metal: Int,
    var type: Int,
    var packageType: Int,
    var quantity: Int,
    var weight: Float,
    var weightUnit: Int,
    var weightr: Float,
    var currency: Int,
    var price: Float,
    var buyDate: String?,
    var editDate: String?,
    var memo: String?,
    var reg: Float,
    var prePrice: Float,
    var metPrice: Float,
    var year: Int,
    var condition: Int,
    var grade: String?,
    var cert: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id!!)
        parcel.writeString(brand)
        parcel.writeInt(metal)
        parcel.writeInt(type)
        parcel.writeInt(packageType)
        parcel.writeInt(quantity)
        parcel.writeFloat(weight)
        parcel.writeInt(weightUnit)
        parcel.writeFloat(weightr)
        parcel.writeInt(currency)
        parcel.writeFloat(price)
        parcel.writeString(buyDate)
        parcel.writeString(editDate)
        parcel.writeString(memo)
        parcel.writeFloat(reg)
        parcel.writeFloat(prePrice)
        parcel.writeFloat(metPrice)
        parcel.writeInt(year)
        parcel.writeInt(condition)
        parcel.writeString(grade)
        parcel.writeString(cert)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductParcel> {
        override fun createFromParcel(parcel: Parcel): ProductParcel {
            return ProductParcel(parcel)
        }

        override fun newArray(size: Int): Array<ProductParcel?> {
            return arrayOfNulls(size)
        }
    }

}