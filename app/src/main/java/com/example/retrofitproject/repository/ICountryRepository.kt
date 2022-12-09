package com.example.retrofitproject.repository

import com.example.retrofitproject.Country

interface ICountryRepository {
    suspend fun getCountry(currency: String): List<Country>
}