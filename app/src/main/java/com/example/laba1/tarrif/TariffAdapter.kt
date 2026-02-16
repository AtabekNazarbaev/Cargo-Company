package com.example.laba1.tarrif

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.laba1.databinding.ItemTariffBinding
import com.example.laba1.helper.PercentageDiscount

class TariffAdapter(
    private val onItemLongClick: (Tariff) -> Unit,
    private val onItemClick: (Tariff) -> Unit
) : ListAdapter<Tariff, TariffAdapter.ViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Tariff>() {
        override fun areItemsTheSame(oldItem: Tariff, newItem: Tariff) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Tariff, newItem: Tariff) = oldItem == newItem
    }

    inner class ViewHolder(private val binding: ItemTariffBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Tariff) {
            binding.tvTariffName.text = item.name
            val calculator = PercentageDiscount()
            val finalPrice = calculator.calculate(item.discount.toDouble(), item.price)

            binding.tvTariffPrice.text = "Цена: $finalPrice руб."

            if (item.discount > 0) {
                binding.tvTariffDiscount.text = "Скидка: ${item.discount}%"
                binding.tvTariffDiscount.visibility = android.view.View.VISIBLE
            } else {
                binding.tvTariffDiscount.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener { onItemClick(item) }
            binding.root.setOnLongClickListener {
                onItemLongClick(item)
                true
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTariffBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}