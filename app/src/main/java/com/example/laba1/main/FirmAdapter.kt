package com.example.laba1.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.laba1.main.Firm
import com.example.laba1.databinding.MainItemBinding

class FirmAdapter(
    private val onTonnageChanged: (Int, Double) -> Unit,
    private val onItemLongClick: (Firm) -> Unit
) : ListAdapter<Firm, FirmAdapter.ViewHolder>(Diff) {

    object Diff : DiffUtil.ItemCallback<Firm>() {
        override fun areItemsTheSame(o: Firm, n: Firm) = o.id == n.id
        override fun areContentsTheSame(o: Firm, n: Firm) = o == n
    }

    inner class ViewHolder(val binding: MainItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Firm) {
            binding.tvName.text = "Фирма: ${item.name}"
            binding.tvId.text = "ID: ${item.id}"

            binding.tvTonnage.setText("${item.tonnage} т", TextView.BufferType.EDITABLE)

            binding.tvTonnage.addTextChangedListener {
                val value = it.toString().toDoubleOrNull() ?: return@addTextChangedListener
                onTonnageChanged(adapterPosition, value)
            }

            binding.root.setOnLongClickListener {
                onItemLongClick(item)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MainItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}