package com.mertg.weatherapp.view



import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide

import com.mertg.weatherapp.R
import com.mertg.weatherapp.databinding.ActivityMainBinding
import com.mertg.weatherapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    //http://api.openweathermap.org/data/2.5/weather?q=izmir&APPID=04a42b96398abc8e4183798ed22f9485

    private lateinit var binding : ActivityMainBinding

    private lateinit var viewModel : MainViewModel
    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        var cName = GET.getString("cityName","ankara")
        binding.edtCityName.setText(cName)

        viewModel.refreshData(cName!!)

        getLiveData()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.llData.visibility = View.GONE
            binding.tvError.visibility = View.GONE
            binding.pbLoading.visibility = View.GONE

            var cityName = GET.getString("cityName",cName)
            binding.edtCityName.setText(cityName)
            viewModel.refreshData(cityName!!)
            binding.swipeRefreshLayout.isRefreshing = false

        }

        binding.imgSearchCity.setOnClickListener{
            val cityName = binding.edtCityName.text.toString()
            SET.putString("cityName",cityName)
            SET.apply()
            viewModel.refreshData(cityName)
            getLiveData()
        }
    }

    private fun getLiveData() {
        viewModel.weather_data.observe(this, Observer {data ->
            data?.let {
                binding.llData.visibility = View.VISIBLE
                binding.pbLoading.visibility = View.GONE
                binding.tvDegree.text = data.main.temp.toString()
                binding.tvCityCode.text = data.sys.country.toString()
                binding.tvCityName.text = data.name.toString()
                binding.tvHumidity.text = data.main.humidity.toString()
                binding.tvWindSpeed.text = data.wind.speed.toString()
                binding.tvLat.text = data.coord.lat.toString()
                binding.tvLon.text = data.coord.lon.toString()

                Glide.with(this)
                    .load("http:://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png")
                    .into(binding.imgWeatherPictures)
            }
        })

        viewModel.weather_load.observe(this, Observer { load ->
          load?.let {
              if(it){
                  binding.pbLoading.visibility = View.VISIBLE
                  binding.tvError.visibility = View.GONE
                  binding.llData.visibility = View.GONE
              }else{
                  binding.pbLoading.visibility = View.GONE
              }
          }
        })

        viewModel.weather_error.observe(this, Observer { error ->
          error?.let {
              if(it){
                  binding.pbLoading.visibility = View.GONE
                  binding.tvError.visibility = View.VISIBLE
                  binding.llData.visibility = View.GONE
              }else{
                  binding.tvError.visibility = View.GONE
              }
          }
        })
    }


}