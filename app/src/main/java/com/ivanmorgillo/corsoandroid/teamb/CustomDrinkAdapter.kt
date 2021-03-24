package com.ivanmorgillo.corsoandroid.teamb

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.apperol.databinding.FragmentCustomDrinkItemBinding

class CustomDrinkAdapter(
    private val onDeleteClick: (CustomDrinkUI) -> Unit,
    private val onDrinkClick: (CustomDrinkUI) -> Unit
) : Adapter<CustomDrinkViewHolder>() {
    var customDrinkList: List<CustomDrinkUI> = emptyList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CustomDrinkViewHolder {
        val binding = FragmentCustomDrinkItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomDrinkViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CustomDrinkViewHolder,
        position: Int,
    ) {
        holder.bind(customDrinkList[position], onDeleteClick, onDrinkClick)
    }

    override fun getItemCount(): Int {
        return customDrinkList.size
    }

    fun setCustomList(items: List<CustomDrinkUI>) {
        customDrinkList = items
        notifyDataSetChanged()
    }
}

class CustomDrinkViewHolder(private val binding: FragmentCustomDrinkItemBinding) : ViewHolder(binding.root) {
    fun bind(item: CustomDrinkUI, onDeleteClick: (CustomDrinkUI) -> Unit, onDrinkClick: (CustomDrinkUI) -> Unit) {
        binding.customDrinkName.text = item.drinkName
        binding.customDrinkImage.load(item.drinkImage)
        binding.customDrinkType.text = item.drinkType
        binding.customDrinkRoot.setOnClickListener { onDrinkClick(item) }
        binding.customDrinkDelete.setOnClickListener { onDeleteClick(item) }
    }
}
