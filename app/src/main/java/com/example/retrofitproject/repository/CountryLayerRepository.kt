package com.example.retrofitproject.repository

import com.example.retrofitproject.Country
import com.example.retrofitproject.repository.CountryLayerRepository.Companion.API_KEY
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class CountryLayerDTO(
    val name: String,
    val currency: String
)

interface CountryLayerService {
    @GET("currency/{currency}?access_key=${API_KEY}")
    suspend fun getCountry(@Path("currency") currency: String): List<CountryLayerDTO>
}

class CountryLayerRepository: ICountryRepository {
    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    private val service = retrofit.create(CountryLayerService::class.java)

    override suspend fun getCountry(currency: String): List<Country> =
        service.getCountry(currency).map{Country(it.name)}

    companion object {
        const val BASE_URL = "http://api.countrylayer.com/v2/"
        const val API_KEY = "67cb97beeb2645c6ae74d3b9aaafcb79"
    }
}