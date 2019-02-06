package com.example.forecastapp.RetrofitCall

import com.example.forecastapp.DataModel.ModelForecast
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CallWeather {

    @GET("forecast?")
    fun getWeatherByCity(
        @Query("q") city: String,
        @Query("cnt") days: Int,
        @Query("appid") appid: String,
        @Query("units") unit: String
    ): Call<ModelForecast>
}