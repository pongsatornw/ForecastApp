package com.example.forecastapp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.forecastapp.Callback.CallbackResponse
import com.example.forecastapp.DataModel.ModelForecast
import com.example.forecastapp.RetrofitCall.CallWeather
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import java.lang.StringBuilder
import android.net.ConnectivityManager
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var call: Call<ModelForecast>
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            editText.clearFocus()
            recyclerView.removeAllViewsInLayout()
            if(!isNetworkAvailable()) {
                Toast.makeText(this@MainActivity, "Internet Connection is not Available!!", Toast.LENGTH_SHORT).show()
            } else if(editText.text == null || editText.text.toString() == ""){
                Toast.makeText(this@MainActivity, "City Name cannot be empty!!", Toast.LENGTH_SHORT).show()
            } else {
                getWeatherFromAPI(editText.text.toString())
            }
        }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    fun getWeatherFromAPI(cityName: String){
        progressBar.visibility = View.VISIBLE
        progressBar.animate()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create<CallWeather>(CallWeather::class.java)
        Log.d("URL", retrofit.baseUrl().toString())


        call = service.getWeatherByCity(cityName, 5, "00b0fb0cfa87b0b0f7cef0685c9bb711", "metric")
        call.enqueue(object: Callback<ModelForecast>{
            override fun onResponse(call: Call<ModelForecast>, response: Response<ModelForecast>) {
                val model = response.body()
                if(model == null) {
                    val errorBody = response.errorBody()
                    if(errorBody!= null) {
                        callback.onSuccessWithError(errorBody)
                    } else {
                        callback.onSuccessWithEmptyBody()
                    }
                } else {
                    callback.onSuccess(model)
                }
                progressBar.clearAnimation()
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<ModelForecast>, t: Throwable) {
                if(!call.isCanceled) {
                    call.cancel()
                }
                progressBar.clearAnimation()
                progressBar.visibility = View.GONE
                callback.onFailure(t)
            }
        })

    }

    val callback = object : CallbackResponse{
        override fun onSuccess(model: ModelForecast) {
            Log.d("Success", "Success")
            Log.d("Forecast", model.toString())

            for(tmp in model.ForecastList){
                tmp.MainData.unit = "Celsius"
            }

            viewManager = LinearLayoutManager(this@MainActivity)
            viewAdapter = RecyclerAdapter(this@MainActivity, model)

            recyclerView.apply{
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

        override fun onSuccessWithError(response: ResponseBody) {
            Log.d("Success", "Success with Error")
            Log.d("Success", response.string())
            Toast.makeText(this@MainActivity, "City not Found.", Toast.LENGTH_SHORT).show()
        }

        override fun onSuccessWithEmptyBody() {
            Toast.makeText(this@MainActivity, "Response Error, No data in body.", Toast.LENGTH_SHORT).show()
            Log.d("Success", "Success with Empty")
        }

        override fun onFailure(t: Throwable) {
            Toast.makeText(this@MainActivity, "Request Failure.", Toast.LENGTH_SHORT).show()
            Log.d("Failure", "Failure")
        }
    }

    inner class RecyclerAdapter(val context: Context, private val model: ModelForecast): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){
        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
            val v = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
            val vh = ViewHolder(v)
            vh.itemView.setOnClickListener {
                if(model.ForecastList[vh.adapterPosition].MainData.unit == "Celsius") {
                    model.ForecastList[vh.adapterPosition].MainData.unit = "Fahrenheit"
                    model.ForecastList[vh.adapterPosition].MainData.Temp = (model.ForecastList[vh.adapterPosition].MainData.Temp * 9/5) + 32
                    model.ForecastList[vh.adapterPosition].MainData.MaxTemp = (model.ForecastList[vh.adapterPosition].MainData.MaxTemp * 9/5) + 32
                    model.ForecastList[vh.adapterPosition].MainData.MinTemp = (model.ForecastList[vh.adapterPosition].MainData.MinTemp * 9/5) + 32
                    vh.itemView.temp.text = StringBuilder().apply{
                        append(model.ForecastList[vh.adapterPosition].MainData.Temp.toString())
                        append(" Fahrenheit")
                    }
                } else {
                    model.ForecastList[vh.adapterPosition].MainData.unit = "Celsius"
                    model.ForecastList[vh.adapterPosition].MainData.Temp = (model.ForecastList[vh.adapterPosition].MainData.Temp - 32) * 5/9
                    model.ForecastList[vh.adapterPosition].MainData.MaxTemp = (model.ForecastList[vh.adapterPosition].MainData.MaxTemp - 32) * 5/9
                    model.ForecastList[vh.adapterPosition].MainData.MinTemp = (model.ForecastList[vh.adapterPosition].MainData.MinTemp - 32) * 5/9

                    vh.itemView.temp.text = StringBuilder().apply{
                        append(model.ForecastList[vh.adapterPosition].MainData.Temp.toString())
                        append(" Celsius")
                    }
                }
            }
            return vh
        }

        override fun onBindViewHolder(holder: ViewHolder, posiiton: Int) {
            val item = holder.itemView

            item.title_location.text = "Location"
            item.title_humidity.text = "Humidity"
            item.title_temp.text = "Tempurature"
            item.title_time.text = "Time"
            item.title_desc.text = "Description"

            item.location.text = StringBuilder().apply{
                append(model.City.CityName)
                append(", ")
                append(model.City.Country)
            }.toString()
            item.humidity.text = StringBuilder().apply{
                append(model.ForecastList[posiiton].MainData.Humidity.toString())
                append(" %")
            }
            item.temp.text = StringBuilder().apply{
                append(model.ForecastList[posiiton].MainData.Temp.toString())
                append(" Celsius")
            }
            item.time.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                Date(model.ForecastList[posiiton].Time * 1000)
            )
            item.desc.text = model.ForecastList[posiiton].Weather[0].Description
        }

        override fun getItemCount(): Int {
            var count = 0
            try {
                count = model.ForecastCount.toInt()
            } catch(nex: NumberFormatException) {
                nex.printStackTrace()
            }
            return count
        }
    }

    override fun onResume() {
        super.onResume()
        if(!isNetworkAvailable()) {
            Toast.makeText(this@MainActivity, "Internet Connection is not Available!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if( ::call.isInitialized && !call.isCanceled) {
            call.cancel()
        }
    }

    override fun onStart() {
        super.onStart()
    }

}
