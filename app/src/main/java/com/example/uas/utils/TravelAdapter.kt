package com.example.uas.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uas.databinding.ItemTravelBinding
import com.example.uas.model.Travel

typealias OnClickItem = (Travel) -> Unit

class TravelAdapter (
    private val listTravel: List<Travel>,
    private val onClickItem: OnClickItem
) : RecyclerView.Adapter<TravelAdapter.ItemTravelViewHolder>() {

    inner class ItemTravelViewHolder(private val binding: ItemTravelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Travel) {
            with(binding) {
                txtName.text = item.trainName
                txtClass.text = item.klass
                txtTimeOrigin.text = item.timeOrigin
                txtTimeDestination.text = item.timeDestination
                txtStationOrigin.text = item.stationOrigin
                txtStationDestination.text = item.stationDestination
                txtPrice.text = String.format("Rp%,d", item.price)
            }

            itemView.setOnClickListener{
                onClickItem(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTravelViewHolder {
        val binding = ItemTravelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemTravelViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTravel.size
    }

    override fun onBindViewHolder(holder: ItemTravelViewHolder, position: Int) {
        holder.bind(listTravel[position])
    }
}