package com.example.medicare.models

import android.os.Parcel
import android.os.Parcelable

data class Speciality(
                      val expertise: String = "",
                      var doctorlist: ArrayList<Doctor> = ArrayList()
                     ): Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,

    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {

        writeString(expertise)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Doctor> = object : Parcelable.Creator<Doctor> {
            override fun createFromParcel(source: Parcel): Doctor = Doctor(source)
            override fun newArray(size: Int): Array<Doctor?> = arrayOfNulls(size)
        }
    }
}

