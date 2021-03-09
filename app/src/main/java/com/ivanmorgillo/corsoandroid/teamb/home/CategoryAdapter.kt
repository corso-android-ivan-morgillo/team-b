package com.ivanmorgillo.corsoandroid.teamb.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.databinding.CategoryItemBinding

const val BORDER_WIDTH = 8

class CategoryAdapter(private val onClick: (CategoryUI, View) -> Unit) : RecyclerView.Adapter<CategoryViewHolder>() {
    private var categoryList: List<CategoryUI> = emptyList()
    private var selectedItemPosition: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position])
        holder.binding.categoryItem.setOnClickListener {
            onClick(categoryList[position], holder.itemView)
            selectedItemPosition = position
            notifyDataSetChanged()
        }
        if (selectedItemPosition == position) {
            holder.binding.categoryImage.borderWidth = BORDER_WIDTH
            holder.binding.categoryText.setTextColor(Color.parseColor("#03DAC5"))
        } else {
            holder.binding.categoryImage.borderWidth = 0
            holder.binding.categoryText.setTextColor(Color.parseColor("#7f7f7f"))
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    fun setCategoryList(items: List<CategoryUI>) {
        categoryList = items
        notifyDataSetChanged()
    }
}

class CategoryViewHolder(val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CategoryUI) {
        binding.categoryText.text = item.nameCategory
        when (item.nameCategory) {
            "Ordinary Drink" -> binding.categoryImage.load(R.drawable.ordinary_drink)
            "Cocktail" -> binding.categoryImage.load(R.drawable.cocktails)
            "Milk / Float / Shake" -> binding.categoryImage.load(R.drawable.milk)
            "Other/Unknown" -> binding.categoryImage.load(R.drawable.other)
            "Cocoa" -> binding.categoryImage.load(R.drawable.cocoa)
            "Shot" -> binding.categoryImage.load(R.drawable.shot)
            "Coffee / Tea" -> binding.categoryImage.load(R.drawable.coffee)
            "Homemade Liqueur" -> binding.categoryImage.load(R.drawable.homemade)
            "Punch / Party Drink" -> binding.categoryImage.load(R.drawable.punch)
            "Beer" -> binding.categoryImage.load(R.drawable.beer)
            "Soft Drink / Soda" -> binding.categoryImage.load(R.drawable.soda)
        }
    }
}
