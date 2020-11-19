package com.example.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val LAT: String = "-23.547501"
    val LON: String = "-46.636108"
    val api: String = BuildConfig.CONSUMER_KEY
    val myLocale = Locale("pt", "BR")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherTask().execute()

    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute(){
            super.onPreExecute()
            binding.loader.visibility = View.VISIBLE
            binding.mainContainer.visibility = View.GONE
            binding.errorText.visibility = View.GONE
        }

       override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?lat=$LAT&lon=$LON&units=metric&appid=$api&lang=pt_br").readText(
                    Charsets.UTF_8
                )
            } catch (e: Exception){
                response = null
            }
            return response
        }


        override fun onPostExecute(result: String?){
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updateAt: Long = jsonObj.getLong("dt")
                val updateAtText =
                    "Atualizado em: " + SimpleDateFormat("dd/MM/yyyy k:mm ", myLocale).format(Date(updateAt * 1000))
                val temp = main.getString("temp") + "°C"
                val tempMin = "Temp Mínima: " + main.getString("temp_min") + "°C"
                val tempMax = "Temp Máxima:  " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name") + ", " + sys.getString("country")
            //    val feelslike = main.getString("feels_like")
            //     binding.feelslike.text = feelslike

                binding.address.text = address
                binding.updatedAt.text = updateAtText
                binding.status.text = weatherDescription.capitalize()
                binding.temp.text = temp
                binding.tempMin.text = tempMin
                binding.tempMax.text = tempMax
                binding.sunrise.text = SimpleDateFormat("k:mm ", myLocale).format(
                    Date(
                        sunrise * 1000
                    )
                )
                binding.sunset.text = SimpleDateFormat("k:mm ", myLocale).format(Date(sunset * 1000))
                binding.wind.text = windSpeed + " km/h"
                binding.pressure.text = pressure + " hPa"
                binding.humidity.text = humidity + "%"

                binding.loader.visibility = View.GONE
                binding.mainContainer.visibility = View.VISIBLE


            } catch (e: Exception){
                binding.loader.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
            }
        }

    }
}