package com.ivanmorgillo.corsoandroid.teamb

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DetailCocktailAdapter :
    RecyclerView.Adapter<DetailCocktailViewHolder>() {
    // lista di ingredienti, inizializzata a empty
    private var detailIngredientList: List<Ingredient> = emptyList()

    // da xml a kotlin per ogni elemento della lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailCocktailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient, parent, false)
        return DetailCocktailViewHolder(view)
    }

    // Con questo metodo un elemento della lista è connesso a un viewHolder.
    // La position è l'indice dell'elemento nella lista
    override fun onBindViewHolder(holder: DetailCocktailViewHolder, position: Int) {
        holder.bind(detailIngredientList.get(position))
    }

    // ritorna il numero di elementi nella lista
    override fun getItemCount(): Int {

        return detailIngredientList.size
    }

    // inizializza la lista di ingredienti con quella passata come argomento
    fun setCocktailsList(items: List<Ingredient>) {
        detailIngredientList = items
        Log.d("SIZE LIST", detailIngredientList.size.toString())
        notifyDataSetChanged()
    }
}

// View Holder è un elemento della lista. Per ogni elemento della lista visibile viene creato un viewHolder
// l'oggetto view è la rappresentazione in ingredient di un layout XML
class DetailCocktailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    // recupera la textView dell'elemento della lista di ingredienti
    val ingredientName = itemView.findViewById<TextView>(R.id.ingredient_item_name)
    val ingredientQty = itemView.findViewById<TextView>(R.id.ingredient_item_quantity)
    fun bind(item: Ingredient) {
        ingredientName.text = item.nomeIngr // imposta lil text dell'elemento al nome dell'ingrediente
        ingredientQty.text = item.ingrQty // imposta la quantità dell'ingrediente
    }
}

data class DetailCocktailUI(
    val title: String,
    val image: String,
    val alcoholic: Boolean,
    val glass: String,
    val ingredient: Ingredient,
    val id: Long
)

data class Ingredient(val nomeIngr: String, val ingrQty: String)
