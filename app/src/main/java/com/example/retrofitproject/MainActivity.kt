package com.example.retrofitproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.retrofitproject.repository.CountryDB
import kotlinx.coroutines.launch

enum class ToastMessage(val message: Int) {
    ERROR_MESSAGE(R.string.error_message),
    LOADING_MESSAGE(R.string.loading_message),
    LOADED_MESSAGE(R.string.loaded_message)
}

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(
            applicationContext,
            CountryDB::class.java, "country"
        ).build()

        viewModel.setDb(db)

        val btnLoad = findViewById<Button>(R.id.btnLoad)
        val editCurrency = findViewById<EditText>(R.id.editCurrency)

        val rvCountry = findViewById<RecyclerView>(R.id.recyclerViewCountry)
        rvCountry.layoutManager = LinearLayoutManager(this)
        val countryAdapter = CountryAdapter(viewModel.countryListStateFlow)
        rvCountry.adapter = countryAdapter

        btnLoad.setOnClickListener {
            val currency = editCurrency.text.toString()

            viewModel.getCountry(currency)
        }

        lifecycleScope.launch{
            viewModel.countryListStateFlow.collect{
                countryAdapter.notifyDataSetChanged()
            }
        }

        lifecycleScope.launch {
            viewModel.modeStateFlow.collect {
                when(it){
                    MainActivityMode.NONE -> {}
                    MainActivityMode.ERROR -> Toast.makeText(this@MainActivity, getString(ToastMessage.ERROR_MESSAGE.message), Toast.LENGTH_SHORT).show()
                    MainActivityMode.LOADING -> Toast.makeText(this@MainActivity, getString(ToastMessage.LOADING_MESSAGE.message), Toast.LENGTH_SHORT).show()
                    MainActivityMode.LOADED -> Toast.makeText(this@MainActivity, getString(ToastMessage.LOADED_MESSAGE.message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}