package au.edu.jcu.assessment.utilityapp.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityApi {

    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude")
        latitude: Double,

        @Query("longitude")
        longitude: Double,

        @Query("current")
        current: String = "us_aqi",

        @Query("domains")
        domains: String = "cams_global",

        @Query("timezone")
        timezone: String = "Asia/Singapore",

        @Query("timeformat")
        timeFormat: String = "unixtime"
    ): AirQualityResponse
}