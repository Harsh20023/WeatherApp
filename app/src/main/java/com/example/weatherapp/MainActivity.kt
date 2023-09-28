package com.example.weatherapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weatherapp.Apiinterface
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.weatherdata
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {

        ActivityMainBinding. inflate (layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchweatherdata("goa")
        searchcity()
    }
    private fun searchcity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    fetchweatherdata(query)
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true

            }

        })
    }


    private fun fetchweatherdata(cityname:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(Apiinterface::class.java)
        val response = retrofit.getweatherdata(cityname,"c90b5bf3c7106e316efebc91b4f48df9","metric")
        response.enqueue(object:Callback<weatherdata>{

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<weatherdata>, response: Response<weatherdata>) {
                val responseBody = response.body ()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()

                    val humidity = responseBody.main.humidity

                    val sunrise = responseBody. sys. sunrise.toLong()
                    val sunSet= responseBody. sys. sunset.toLong()
                    val seaLevel = responseBody .main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxTemp = responseBody .main. temp_max
                    val minTemp = responseBody .main. temp_min
                    binding.temp.text = "$temperature°C"
                    binding.weather.text = condition
                    binding.mintemp.text = "Min Temp:$minTemp°C"
                    binding.maxtemp.text = "Max Temp:$maxTemp°C"
                    binding.humidity.text = "$humidity "

                    binding.sunrise.text= time(sunrise)
                    binding. sunset.text= time(sunSet)
                    binding.sea.text ="$seaLevel "
                    binding.condition.text = condition
                    binding.location.text = cityname
                    binding.date.text = dayName(System.currentTimeMillis())
                    binding.day.text =date()

                    imagechangeaccording(condition)
                    //Log.d("TAG", "onResponse: $temperature")

                }
            }

            override fun onFailure(call: Call<weatherdata>, t: Throwable) {
                Log.e("RetrofitError", "Retrofit error: ${t.message}")



            }

        })}
    fun imagechangeaccording(conditions: String) {
        when(conditions){
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Haze", "Mist","Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource (R.drawable.rain_background)
                binding. lottieAnimationView.setAnimation(R.raw.rain)

            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource (R. drawable. snow_background)
                binding. lottieAnimationView.setAnimation(R.raw.snow)

            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)

                binding. lottieAnimationView.setAnimation(R.raw.sun)}
        }
        binding. lottieAnimationView.playAnimation()
    }


    fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName (timestamp: Long): String{
        val sdf = SimpleDateFormat (  "EEEE", Locale. getDefault ())
        return sdf. format ( (Date ()))}
}


