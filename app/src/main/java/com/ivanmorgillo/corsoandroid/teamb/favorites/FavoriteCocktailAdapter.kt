package com.ivanmorgillo.corsoandroid.teamb.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.ivanmorgillo.corsoandroid.teamb.databinding.FavoriteCocktailsItemBinding

class FavoriteCocktailAdapter : Adapter<FavoriteCocktailViewHolder>() {
    var favoriteCocktailsList: List<FavoriteCocktailUI> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteCocktailViewHolder {
        val binding = FavoriteCocktailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteCocktailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteCocktailViewHolder, position: Int) {
        holder.bind(favoriteCocktailsList[position])
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
    fun bind(item: FavoriteCocktailUI) {
        binding.favoriteCocktailName.text = item.cocktailName
        binding.favoriteCocktailImage.load(item.image)
        binding.favoriteCocktailCategory.text = item.category
    }
}

data class FavoriteCocktailUI(
    val cocktailName: String,
    val image: String,
    val id: Long,
    val category: String
)
