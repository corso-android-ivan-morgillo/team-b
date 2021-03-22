package com.ivanmorgillo.corsoandroid.teamb.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.apperol.databinding.CategoryItemBinding

const val BORDER_WIDTH = 8

class CategoryAdapter(private val onClick: (CategoryUI, View) -> Unit) : RecyclerView.Adapter<CategoryViewHolder>() {
    private var categoryList: List<CategoryUI> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position], onClick)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    fun setCategoryList(items: List<CategoryUI>) {
        categoryList = items
        notifyDataSetChanged()
    }
}

class CategoryViewHolder(private val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CategoryUI, onClick: (CategoryUI, View) -> Unit) {
        binding.categoryText.text = item.nameCategory
        binding.categoryImage.load(item.imageCategory)
       
        binding.categoryItem.setOnClickListener {
            onClick(item, itemView)
        }
        if (item.isSelected) {
            binding.categoryImage.borderWidth = BORDER_WIDTH
            binding.categoryText.setTextColor(Color.parseColor("#03DAC5"))
        } else {
            binding.categoryImage.borderWidth = 0
            binding.categoryText.setTextColor(Color.parseColor("#7f7f7f"))
        }
    }
}
