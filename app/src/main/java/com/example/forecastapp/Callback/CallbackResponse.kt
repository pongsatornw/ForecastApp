package com.example.forecastapp.Callback

import com.example.forecastapp.DataModel.ForecastData
import com.example.forecastapp.DataModel.ModelForecast
import okhttp3.ResponseBody
import retrofit2.Response

interface CallbackResponse {

    fun onFailure(t: Throwable)

    fun onSuccess(model: ModelForecast)

    fun onSuccessWithEmptyBody()

    fun onSuccessWithError(response: ResponseBody)

}