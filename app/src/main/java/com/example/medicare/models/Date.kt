package com.example.medicare.models

import android.os.Parcel
import android.os.Parcelable

data class Date(
    val date: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(date)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Date> = object : Parcelable.Creator<Date> {
            override fun createFromParcel(source: Parcel): Date = Date(source)
            override fun newArray(size: Int): Array<Date?> = arrayOfNulls(size)
        }
    }
}
