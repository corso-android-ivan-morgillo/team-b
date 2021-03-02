package com.ivanmorgillo.corsoandroid.teamb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SectionIndexer
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.google.android.material.card.MaterialCardView

const val INITIAL_CAPACITY = 26

// l'adapter ha bisogno di un viewHolder che creeremo (cocktailViewHolder)
class DrinkAdapter(private val onClick: (DrinksUI, View) -> Unit) : Adapter<CocktailViewHolder>(), SectionIndexer {
    // lista di cocktail, inizializzata a empty
    private var drinksList: List<DrinksUI> = emptyList()
    private var mSectionPositions = mutableListOf<Int>()

    // da xml a kotlin per ogni elemento della lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.drink_item, parent, false)
        return CocktailViewHolder(view)
    }

    // Con questo metodo un elemento della lista è connesso a un viewHolder.
    // La position è l'indice dell'elemento nella lista
    override fun onBindViewHolder(holder: CocktailViewHolder, position: Int) {
        holder.bind(drinksList.get(position), onClick)
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
            val section: String = (drinksList.get(i).drinkName.take(1)).toUpperCase()
            if (!sections.contains(section)) {
                existInSectionList(section, sections, i)
            }
            i++
        }

        return sections.toTypedArray<String>()
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
        return mSectionPositions.get(sectionIndex)
    }

    override fun getSectionForPosition(position: Int): Int {
        return 0
    }
}

// View Holder è un elemento della lista. Per ogni elemento della lista visibile viene creato un viewHolder
// l'oggetto view è la rappresentazione in cocktail di un layout XML
class CocktailViewHolder(itemView: View) : ViewHolder(itemView) {
    // recupera la textView dell'elemento della lista
    val name = itemView.findViewById<TextView>(R.id.drink_name)
    val image = itemView.findViewById<ImageView>(R.id.drink_image)
    val drinkCardView = itemView.findViewById<MaterialCardView>(R.id.drink_root)

    fun bind(item: DrinksUI, onClick: (DrinksUI, View) -> Unit) {
        name.text = item.drinkName // imposta lil text dell'elemento al nome del cocktail
        image.load(item.image) // imposta l'immagine all'elemento
        image.contentDescription = item.drinkName
        drinkCardView.setOnClickListener { onClick(item, it) }
        drinkCardView.transitionName = "cocktail_transition_item${item.id}"
    }
}

data class DrinksUI(
    val drinkName: String,
    val image: String,
    val id: Long
)
