package com.example.uas.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uas.databinding.ItemTravelBinding
import com.example.uas.model.Favorite
import com.example.uas.model.Travel
import com.example.uas.network.TravelCollection

private typealias OnClickFavorite = (Favorite) -> Unit
private typealias OnLongClickFavorite = (Favorite) -> Unit

class FavoriteAdapter(
    private val listFavorite: List<Favorite>,
    private val onClickFavorite: OnClickFavorite,
    private val onLongClickFavorite: OnLongClickFavorite
) : RecyclerView.Adapter<FavoriteAdapter.ItemFavoriteViewHolder>() {

    inner class ItemFavoriteViewHolder(private val binding: ItemTravelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: Favorite) {
            with(binding) {
                val travelCollection = TravelCollection()
                travelCollection.getItemById(favorite.travelId) { item ->
                    if (item != null) {
                        txtName.text = item.trainName
                        txtClass.text = item.klass
                        txtTimeOrigin.text = item.timeOrigin
                        txtTimeDestination.text = item.timeDestination
                        txtStationOrigin.text = item.stationOrigin
                        txtStationDestination.text = item.stationDestination
                        txtPriceOrDate.text = String.format("Rp%,d", item.price)
                    }
                }
            }

            itemView.setOnClickListener {
                onClickFavorite(favorite)
            }

            itemView.setOnLongClickListener {
                onLongClickFavorite(favorite)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFavoriteViewHolder {
        val binding = ItemTravelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemFavoriteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listFavorite.size
    }

    override fun onBindViewHolder(holder: ItemFavoriteViewHolder, position: Int) {
        holder.bind(listFavorite[position])
    }
}