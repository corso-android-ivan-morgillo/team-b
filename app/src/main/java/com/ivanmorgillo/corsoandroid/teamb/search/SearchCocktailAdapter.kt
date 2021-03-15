package com.ivanmorgillo.corsoandroid.teamb.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.apperol.databinding.SearchCocktailsItemBinding
import timber.log.Timber

class SearchCocktailAdapter
    (private val onClick: (SearchCocktailUI, View) -> Unit) : Adapter<SearchCocktailViewHolder>() {
    var searchCocktailsList: List<SearchCocktailUI> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCocktailViewHolder {
        val binding = SearchCocktailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchCocktailViewHolder(binding)
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

class SearchCocktailViewHolder(private val binding: SearchCocktailsItemBinding) : ViewHolder(binding.root) {
    fun bind(item: SearchCocktailUI, onClick: (SearchCocktailUI, View) -> Unit) {
        binding.searchCocktailName.text = item.cocktailName
        binding.searchCocktailImage.load(item.image)
        binding.searchCocktailCategory.text = item.category
        binding.searchCocktailAlcoholic.text = item.alcoholic
        binding.searchCocktailRoot.setOnClickListener { onClick(item, it) }
        binding.searchCocktailRoot.transitionName = "cocktail_transition_item${item.id}"
    }
}
