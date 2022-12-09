package com.example.retrofitproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofitproject.repository.CountryDB
import com.example.retrofitproject.repository.CountryLayerRepository
import com.example.retrofitproject.repository.ICountryRepository
import com.example.retrofitproject.repository.RoomDecoratorCountryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class MainActivityMode {
    NONE, ERROR, LOADING, LOADED
}

class MainActivityViewModel: ViewModel() {

    private lateinit var db: CountryDB
    private lateinit var _countryRepository : ICountryRepository

    private val _modeStateFlow = MutableStateFlow(MainActivityMode.NONE)
    val modeStateFlow = _modeStateFlow.asStateFlow()

    private val _countryListStateFlow = MutableStateFlow(listOf<Country>())
    val countryListStateFlow = _countryListStateFlow.asStateFlow()

    fun getCountry(currency: String) {
        viewModelScope.launch {
            _modeStateFlow.emit(MainActivityMode.LOADING)
            try {
                val listCountry = _countryRepository.getCountry(currency)
                 _countryListStateFlow.emit(listCountry)
                _modeStateFlow.emit(MainActivityMode.LOADED)
            } catch (exception: Exception) {
                _modeStateFlow.emit(MainActivityMode.ERROR)
            }
        }
    }

    fun setDb(db: CountryDB){
        this.db = db
        _countryRepository = RoomDecoratorCountryRepository(CountryLayerRepository(), db)
    }
}