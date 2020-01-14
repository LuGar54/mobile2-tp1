package ca.csf.mobile2.tp1

import com.fasterxml.jackson.module.kotlin.*

data class CityWeather(val json: String) {

    var city: String = ""
    var type: String = ""
    var temperatureInCelsius: Int = 0

    init {

        val mapper = jacksonObjectMapper()

        val cityWeather: CityWeather = mapper.readValue(json)

        city = cityWeather.city
        type = cityWeather.type
        temperatureInCelsius = cityWeather.temperatureInCelsius

    }
}