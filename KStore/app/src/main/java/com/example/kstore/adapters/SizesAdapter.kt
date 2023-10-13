package com.example.kstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.kstore.databinding.SizeRvItemBinding

class SizesAdapter: RecyclerView.Adapter<SizesAdapter.SizeAdapterViewHolder>(){

    private var selectedPosition = -1

    inner class SizeAdapterViewHolder(private val binding: SizeRvItemBinding):
            ViewHolder(binding.root){
                fun bind(size: String, position: Int){
                    binding.tvSize.text = size
                    if (position == selectedPosition){// Size is selected
                        binding.apply {
                            imageShadow.visibility = View.VISIBLE
                        }
                    }else{ // Size isn't Selected
                        binding.apply {
                            imageShadow.visibility = View.INVISIBLE
                        }
                    }
                }
            }

    val differCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeAdapterViewHolder {
        return SizeAdapterViewHolder(
            SizeRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SizeAdapterViewHolder, position: Int) {
       val size = differ.currentList[position]
        holder.bind(size, position)

        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0)
                notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(size)
        }
    }
    // Lem-eda
    var onItemClick:((String) -> Unit)? = null
}