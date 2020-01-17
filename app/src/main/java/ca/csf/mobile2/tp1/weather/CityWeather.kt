package ca.csf.mobile2.tp1.weather

import android.os.Parcel
import android.os.Parcelable

data class CityWeather(val city: String, val temperatureInCelsius: Int, val type: String) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(city)
        parcel.writeString(type)
        parcel.writeInt(temperatureInCelsius)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CityWeather> {
        override fun createFromParcel(parcel: Parcel): CityWeather {
            return CityWeather(parcel)
        }

        override fun newArray(size: Int): Array<CityWeather?> {
            return arrayOfNulls(size)
        }
    }
}