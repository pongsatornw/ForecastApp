package com.example.forecastapp.DataModel

import com.google.gson.annotations.SerializedName

data class ModelForecast(
    @SerializedName("cod") val Code: String,
    @SerializedName("message") val Message: Any,
    @SerializedName("cnt") val ForecastCount: String,
    @SerializedName("list") val ForecastList: List<ForecastData>,
    @SerializedName("city") val City: City

)

data class City(
    @SerializedName("id") val ID: String,
    @SerializedName("name") val CityName: String,
    //@SerializedName("coord") val Coord: String,
    @SerializedName("country") val Country: String,
    @SerializedName("population") val Population: String
)

data class ForecastData(
    @SerializedName("dt") val Time: Long,
    @SerializedName("main") val MainData: MainData,
    //@SerializedName("coord") val Coord: String,
    @SerializedName("weather") val Weather: List<WeatherData>,
    /*@SerializedName("clouds") val Clouds: String,
    @SerializedName("wind") val Wind: String,
    @SerializedName("rain") val Rain: String,
    @SerializedName("sys") val Sys: String,*/
    @SerializedName("dt_txt") val DateText: String
)

data class MainData (
    @SerializedName("temp") var Temp: Float,
    @SerializedName("temp_min") var MinTemp: Float,
    @SerializedName("temp_max") var MaxTemp: Float,
    @SerializedName("pressure") val Pressure: Float,
    @SerializedName("sea_level") val SeaLV: Float,
    @SerializedName("grnd_level") val GroundLV: Float,
    @SerializedName("humidity") val Humidity: Float,
    @SerializedName("temp_kf") val TempKF: Float,
    var unit: String
)

data class WeatherData (
    @SerializedName("id") val ID: Int,
    @SerializedName("main") val Main: String,
    @SerializedName("description") val Description: String,
    @SerializedName("icon") val Icon: String
)