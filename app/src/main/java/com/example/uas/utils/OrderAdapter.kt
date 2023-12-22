package com.example.uas.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uas.databinding.ItemTravelBinding
import com.example.uas.model.Order
import com.example.uas.network.TravelCollection

private typealias OnClickOrder = (Order) -> Unit
class OrderAdapter(
    private val listOrder: List<Order>,
    private val onclickOrder: OnClickOrder
) : RecyclerView.Adapter<OrderAdapter.ItemOrderViewHolder>() {

    inner class ItemOrderViewHolder(private val binding: ItemTravelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            with(binding) {
                val travelCollection = TravelCollection()
                travelCollection.getItemById(order.travelId) { item ->
                    if (item != null) {
                        txtName.text = item.trainName
                        txtClass.text = item.klass
                        txtTimeOrigin.text = item.timeOrigin
                        txtTimeDestination.text = item.timeDestination
                        txtStationOrigin.text = item.stationOrigin
                        txtStationDestination.text = item.stationDestination
                        txtPriceOrDate.text = order.date
                        txtPerson.text = "${order.seat} Seat"
                    }
                }
            }

            itemView.setOnClickListener {
                onclickOrder(order)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ItemOrderViewHolder {
        val binding = ItemTravelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemOrderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listOrder.size
    }

    override fun onBindViewHolder(holder: OrderAdapter.ItemOrderViewHolder, position: Int) {
        holder.bind(listOrder[position])
    }
}