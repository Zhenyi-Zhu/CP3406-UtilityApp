package au.edu.jcu.assessment.utilityapp.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude")
        latitude: Double,

        @Query("longitude")
        longitude: Double,

        @Query("current")
        current: String = "temperature_2m,wind_speed_10m",

        @Query("hourly")
        hourly: String = "precipitation_probability",

        @Query("forecast_hours")
        forecastHours: Int = 2,

        @Query("temperature_unit")
        temperatureUnit: String = "celsius",

        @Query("wind_speed_unit")
        windSpeedUnit: String = "kmh",

        @Query("timezone")
        timezone: String = "Asia/Singapore",

        @Query("timeformat")
        timeFormat: String = "unixtime"
    ): WeatherResponse
}