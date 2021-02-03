package com.ivanmorgillo.corsoandroid.teamb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

//l'adapter ha bisogno di un viewHolder che creeremo (cocktailViewHolder)
class CocktailAdapter : RecyclerView.Adapter<CocktailViewHolder>() {
    //lista di cocktail, inizializzata a empty
    var cocktailsList:List<CocktailUI> = emptyList();

    //da xml a kotlin per ogni elemento della lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cocktails_item, parent, false)
        return CocktailViewHolder(view)
    }

    //Con questo metodo un elemento della lista è connesso a un viewHolder. La position è l'indice dell'elemento nella lista
    override fun onBindViewHolder(holder: CocktailViewHolder, position: Int) {
        holder.bind(cocktailsList.get(position))
    }

    //ritorna il numero di elementi nella lista
    override fun getItemCount(): Int {
        return cocktailsList.size
    }

    //inizializza la lista di cocktail con quella passata come argomento
    fun setCocktails_List(items: List<CocktailUI>){
        cocktailsList = items
        notifyDataSetChanged()
    }
}

//View Holder è un elemento della lista. Per ogni elemento della lista visibile viene creato un viewHolder
// l'oggetto view è la rappresentazione in cocktail di un layout XML
class CocktailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    //recupera la textView dell'elemento della lista
    val name = itemView.findViewById<TextView>(R.id.cocktail_name)
    val image=itemView.findViewById<ImageView>(R.id.cocktail_image)
    fun bind(item: CocktailUI){
        //imposta lil text dell'elemento al nome del cocktail
        name.text=item.cocktailName;
        //imposta l'immagine all'elemento
        image.load(item.image)
    }
}