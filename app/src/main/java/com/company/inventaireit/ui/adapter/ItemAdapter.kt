package com.company.inventaireit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.company.inventaireit.R
import com.company.inventaireit.data.entity.ScannedItemEntity
import com.company.inventaireit.databinding.ItemScannedBinding
import java.text.SimpleDateFormat
import java.util.*

class ItemAdapter : ListAdapter<ScannedItemEntity, ItemAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemScannedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(private val binding: ItemScannedBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        fun bind(item: ScannedItemEntity) {
            binding.tvCodeValue.text = item.codeValue
            binding.tvLocation.text = "${item.floor} - ${item.room}"
            binding.tvTime.text = dateFormat.format(Date(item.scannedAt))
            
            if (item.isDuplicate) {
                binding.root.setCardBackgroundColor(binding.root.context.getColor(R.color.errorContainer))
                binding.tvCodeValue.setTextColor(binding.root.context.getColor(R.color.error))
            } else {
                binding.root.setCardBackgroundColor(binding.root.context.getColor(R.color.surface))
                binding.tvCodeValue.setTextColor(binding.root.context.getColor(R.color.onSurface))
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ScannedItemEntity>() {
        override fun areItemsTheSame(oldItem: ScannedItemEntity, newItem: ScannedItemEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ScannedItemEntity, newItem: ScannedItemEntity) = oldItem == newItem
    }
}