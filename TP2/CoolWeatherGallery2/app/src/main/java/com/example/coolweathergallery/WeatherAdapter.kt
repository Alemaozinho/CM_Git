package com.example.coolweathergallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeatherAdapter(private var items: List<WeatherItem>) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTime: TextView = view.findViewById(R.id.txtTime)
        val txtTemp: TextView = view.findViewById(R.id.txtTemp)
        val imgWeather: ImageView = view.findViewById(R.id.imgWeather)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtTime.text = item.time
        holder.txtTemp.text = item.temp
        holder.imgWeather.setImageResource(item.imageResId)
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<WeatherItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}