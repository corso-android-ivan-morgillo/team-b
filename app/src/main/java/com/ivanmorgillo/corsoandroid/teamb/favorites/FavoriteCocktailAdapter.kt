package com.ivanmorgillo.corsoandroid.teamb.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.apperol.databinding.FavoriteCocktailsItemBinding

class FavoriteCocktailAdapter
    (private val onClick: (FavoriteCocktailUI, View) -> Unit) : Adapter<FavoriteCocktailViewHolder>() {
    var favoriteCocktailsList: List<FavoriteCocktailUI> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteCocktailViewHolder {
        val binding = FavoriteCocktailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteCocktailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteCocktailViewHolder, position: Int) {
        holder.bind(favoriteCocktailsList[position], onClick)
    }

    override fun getItemCount(): Int {
        return favoriteCocktailsList.size
    }

    fun setFavoriteList(items: List<FavoriteCocktailUI>) {
        favoriteCocktailsList = items
        notifyDataSetChanged()
    }
}

class FavoriteCocktailViewHolder(private val binding: FavoriteCocktailsItemBinding) : ViewHolder(binding.root) {
    fun bind(item: FavoriteCocktailUI, onClick: (FavoriteCocktailUI, View) -> Unit) {
        binding.favoriteCocktailName.text = item.cocktailName
        binding.favoriteCocktailImage.load(item.image)
        binding.favoriteCocktailCategory.text = item.category
        binding.favoriteCocktailRoot.setOnClickListener { onClick(item, it) }
        binding.favoriteCocktailDelete.setOnClickListener { onClick(item, it) }
    }
}
