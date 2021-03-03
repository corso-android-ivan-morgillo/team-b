package com.ivanmorgillo.corsoandroid.teamb.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SectionIndexer
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.ivanmorgillo.corsoandroid.teamb.databinding.DrinkItemBinding
import java.util.*
import kotlin.collections.ArrayList

const val INITIAL_CAPACITY = 26

// l'adapter ha bisogno di un viewHolder che creeremo (cocktailViewHolder)
class DrinkAdapter(private val onClick: (DrinksUI, View) -> Unit) : Adapter<CocktailViewHolder>(), SectionIndexer {
    // lista di cocktail, inizializzata a empty
    private var drinksList: List<DrinksUI> = emptyList()
    private var mSectionPositions = mutableListOf<Int>()

    // da xml a kotlin per ogni elemento della lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        val binding = DrinkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailViewHolder(binding)
    }

    // Con questo metodo un elemento della lista è connesso a un viewHolder.
    // La position è l'indice dell'elemento nella lista
    override fun onBindViewHolder(holder: CocktailViewHolder, position: Int) {
        holder.bind(drinksList[position], onClick)
    }

    // ritorna il numero di elementi nella lista
    override fun getItemCount(): Int {
        return drinksList.size
    }

    // inizializza la lista di cocktail con quella passata come argomento
    fun setDrinksList(items: List<DrinksUI>) {
        drinksList = items
        notifyDataSetChanged()
    }

    override fun getSections(): Array<String> {
        val sections: MutableList<String> = ArrayList(INITIAL_CAPACITY)

        var i = 0
        val size: Int = drinksList.size
        while (i < size) {
            val section: String = (drinksList[i].drinkName.take(1)).toUpperCase(Locale.getDefault())
            if (!sections.contains(section)) {
                existInSectionList(section, sections, i)
            }
            i++
        }

        return sections.toTypedArray()
    }

    private fun existInSectionList(section: String, sections: MutableList<String>, i: Int) {
        if (section.first() in '0'..'9') {
            if (!sections.contains("#")) {
                println(section)
                sections.add("#")
                mSectionPositions.add(i)
            }
        } else {
            sections.add(section)
            mSectionPositions.add(i)
        }
    }

    override fun getPositionForSection(sectionIndex: Int): Int {
        return mSectionPositions[sectionIndex]
    }

    override fun getSectionForPosition(position: Int): Int {
        return 0
    }
}

// View Holder è un elemento della lista. Per ogni elemento della lista visibile viene creato un viewHolder
// l'oggetto view è la rappresentazione in cocktail di un layout XML
class CocktailViewHolder(private val binding: DrinkItemBinding) : ViewHolder(binding.root) {
    fun bind(item: DrinksUI, onClick: (DrinksUI, View) -> Unit) {
        binding.drinkName.text = item.drinkName // imposta lil text dell'elemento al nome del cocktail
        binding.drinkImage.load(item.image) // imposta l'immagine all'elemento
        binding.drinkImage.contentDescription = item.drinkName
        binding.drinkRoot.setOnClickListener { onClick(item, it) }
        binding.drinkRoot.transitionName = "cocktail_transition_item${item.id}"
    }
}
