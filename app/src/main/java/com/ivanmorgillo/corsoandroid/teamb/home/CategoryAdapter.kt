package com.ivanmorgillo.corsoandroid.teamb.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teamb.R

class CategoryAdapter : RecyclerView.Adapter<CategoryViewHolder>() {
    private var category_list: List<Category> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(category_list.get(position))
    }

    override fun getItemCount(): Int {
        return category_list.size
    }

    fun setCategoryList(items: List<Category>) {
        category_list = items
        notifyDataSetChanged()
    }
}

data class Category(
    val nameCategory: String,
    val imageCategory: String
)

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val text = itemView.findViewById<TextView>(R.id.category_text)
    val image = itemView.findViewById<ImageView>(R.id.category_image)
    fun bind(item: Category) {
        text.text = item.nameCategory
        image.load(item.imageCategory)
    }
}
