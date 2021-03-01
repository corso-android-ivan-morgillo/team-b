package com.ivanmorgillo.corsoandroid.teamb.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teamb.R

class CategoryAdapter(private val onClick: (CategoryUI, View) -> Unit) : RecyclerView.Adapter<CategoryViewHolder>() {
    private var categoryUIList: List<CategoryUI> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryUIList.get(position), onClick)
    }

    override fun getItemCount(): Int {
        return categoryUIList.size
    }

    fun setCategoryList(items: List<CategoryUI>) {
        categoryUIList = items
        notifyDataSetChanged()
    }
}

data class CategoryUI(
    val nameCategory: String,
    val imageCategory: String
)

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val text = itemView.findViewById<TextView>(R.id.category_text)
    val image = itemView.findViewById<ImageView>(R.id.category_image)
    val categoryItem = itemView.findViewById<ConstraintLayout>(R.id.category_item)
    fun bind(item: CategoryUI, onClick: (CategoryUI, View) -> Unit) {
        text.text = item.nameCategory
        image.load(item.imageCategory)
        categoryItem.setOnClickListener { onClick(item, it) }
    }
}
