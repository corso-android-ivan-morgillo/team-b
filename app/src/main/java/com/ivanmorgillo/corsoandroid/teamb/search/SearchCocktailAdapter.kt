package com.ivanmorgillo.corsoandroid.teamb.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.google.android.material.card.MaterialCardView
import com.ivanmorgillo.corsoandroid.teamb.R
import timber.log.Timber

class SearchCocktailAdapter
    (private val onClick: (SearchCocktailUI, View) -> Unit) : Adapter<SearchCocktailViewHolder>() {
    var searchCocktailsList: List<SearchCocktailUI> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCocktailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_cocktails_item, parent, false)
        return SearchCocktailViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchCocktailViewHolder, position: Int) {
        holder.bind(searchCocktailsList[position], onClick)
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

class SearchCocktailViewHolder(itemView: View) : ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.search_cocktail_name)
    val image = itemView.findViewById<ImageView>(R.id.search_cocktail_image)
    val category = itemView.findViewById<TextView>(R.id.search_cocktail_category)
    val alcoholic = itemView.findViewById<TextView>(R.id.search_cocktail_alcoholic)
    val searchCardView = itemView.findViewById<MaterialCardView>(R.id.search_cocktail_root)
    fun bind(item: SearchCocktailUI, onClick: (SearchCocktailUI, View) -> Unit) {
        name.text = item.cocktailName
        image.load(item.image)
        category.text = item.category
        alcoholic.text = item.alcoholic
        searchCardView.setOnClickListener { onClick(item, it) }
    }
}
