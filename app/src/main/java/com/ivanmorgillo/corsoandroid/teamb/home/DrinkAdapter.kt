package com.ivanmorgillo.corsoandroid.teamb.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.apperol.databinding.DrinkItemBinding

// l'adapter ha bisogno di un viewHolder che creeremo (cocktailViewHolder)
class DrinkAdapter(private val onClick: (DrinkUI, View) -> Unit) : Adapter<CocktailViewHolder>() {
    // lista di cocktail, inizializzata a empty
    private var drinkList: List<DrinkUI> = emptyList()

    // da xml a kotlin per ogni elemento della lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        val binding = DrinkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailViewHolder(binding)
    }

    // Con questo metodo un elemento della lista è connesso a un viewHolder.
    // La position è l'indice dell'elemento nella lista
    override fun onBindViewHolder(holder: CocktailViewHolder, position: Int) {
        holder.bind(drinkList[position], onClick)
    }

    // ritorna il numero di elementi nella lista
    override fun getItemCount(): Int {
        return drinkList.size
    }

    // inizializza la lista di cocktail con quella passata come argomento
    fun setDrinksList(items: List<DrinkUI>) {
        drinkList = items
        notifyDataSetChanged()
    }
}

// View Holder è un elemento della lista. Per ogni elemento della lista visibile viene creato un viewHolder
// l'oggetto view è la rappresentazione in cocktail di un layout XML
class CocktailViewHolder(private val binding: DrinkItemBinding) : ViewHolder(binding.root) {
    fun bind(item: DrinkUI, onClick: (DrinkUI, View) -> Unit) {
        binding.drinkName.text = item.drinkName // imposta lil text dell'elemento al nome del cocktail
        binding.drinkImage.load(item.image) // imposta l'immagine all'elemento
        binding.drinkImage.contentDescription = item.drinkName
        binding.drinkRoot.setOnClickListener { onClick(item, it) }
        binding.drinkRoot.transitionName = "cocktail_transition_item${item.id}"
    }
}
