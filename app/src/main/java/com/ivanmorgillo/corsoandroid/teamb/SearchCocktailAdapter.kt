package com.ivanmorgillo.corsoandroid.teamb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import timber.log.Timber

class SearchCocktailAdapter : RecyclerView.Adapter<SearchCocktailViewHolder>() {
    var searchCocktailsList: List<SearchCocktailUI> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCocktailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_cocktails_item, parent, false)
        return SearchCocktailViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchCocktailViewHolder, position: Int) {
        holder.bind(searchCocktailsList[position])
    }

    override fun getItemCount(): Int {
        Timber.d("${searchCocktailsList.size}")
        return searchCocktailsList.size
    }

    fun setSearchList(items: List<SearchCocktailUI>) {
        searchCocktailsList = items
        Timber.d("$searchCocktailsList")
        notifyDataSetChanged()
    }
}

class SearchCocktailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.search_cocktail_name)
    val image = itemView.findViewById<ImageView>(R.id.search_cocktail_image)
    val category = itemView.findViewById<TextView>(R.id.search_cocktail_category)
    val alcoholic = itemView.findViewById<TextView>(R.id.search_cocktail_alcoholic)
    fun bind(item: SearchCocktailUI) {
        name.text = item.cocktailName
        image.load(item.image)
        category.text = item.category
        alcoholic.text = item.alcoholic.toString()
    }
}
