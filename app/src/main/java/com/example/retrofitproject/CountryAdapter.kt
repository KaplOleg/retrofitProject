package com.example.retrofitproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.StateFlow

class CountryAdapter(private val listStateFlow: StateFlow<List<Country>>)
    : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    private var _countryList: List<Country> = emptyList()

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nameTextView = itemView.findViewById<TextView>(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.country, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = _countryList[position].name
    }

    override fun getItemCount(): Int {
        _countryList = listStateFlow.value
        return _countryList.size
    }
}