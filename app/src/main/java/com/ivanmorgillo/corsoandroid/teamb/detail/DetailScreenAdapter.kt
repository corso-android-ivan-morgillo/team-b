package com.ivanmorgillo.corsoandroid.teamb.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenItems.GlassType
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenItems.Image
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenItems.IngredientList
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenItems.Instructions
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenItems.Title
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenViewHolder.GlassTypeViewHolder
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenViewHolder.ImageViewHolder
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenViewHolder.IngredientListViewHolder
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenViewHolder.InstructionsViewHolder
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenViewHolder.TitleViewHolder
import com.ivanmorgillo.corsoandroid.teamb.exhaustive

sealed class DetailScreenItems {
    data class Title(val title: String) : DetailScreenItems()
    data class Image(val image: String) : DetailScreenItems()
    data class GlassType(val glass: String, val isAlcoholic: Boolean) : DetailScreenItems()
    data class IngredientList(val ingredients: List<Ingredient>) : DetailScreenItems()
    data class Instructions(val instructions: String) : DetailScreenItems()
}

private const val GLASS_TYPE_VIEWTYPE = 1
private const val IMAGE_VIEWTYPE = 2
private const val INGREDIENT_LIST_VIEWTYPE = 3
private const val INSTRUCTIONS_VIEWTYPE = 4
private const val TITLE_VIEWTYPE = 5

class DetailScreenAdapter : RecyclerView.Adapter<DetailScreenViewHolder>() {
    var items: List<DetailScreenItems> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is GlassType -> GLASS_TYPE_VIEWTYPE
            is Image -> IMAGE_VIEWTYPE
            is IngredientList -> INGREDIENT_LIST_VIEWTYPE
            is Instructions -> INSTRUCTIONS_VIEWTYPE
            is Title -> TITLE_VIEWTYPE
        }.exhaustive
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailScreenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            GLASS_TYPE_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_glasstype, parent, false)
                GlassTypeViewHolder(view)
            }
            IMAGE_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_image, parent, false)
                ImageViewHolder(view)
            }
            INGREDIENT_LIST_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_ingredients, parent, false)
                IngredientListViewHolder(view)
            }
            INSTRUCTIONS_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_instructions, parent, false)
                InstructionsViewHolder(view)
            }
            TITLE_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_title, parent, false)
                TitleViewHolder(view)
            }
            else -> error("ViewType not valid")
        }.exhaustive
    }

    override fun onBindViewHolder(holder: DetailScreenViewHolder, position: Int) {
        when (holder) {
            is GlassTypeViewHolder -> holder.bind(items[position] as GlassType)
            is ImageViewHolder -> holder.bind(items[position] as Image)
            is IngredientListViewHolder -> holder.bind(items[position] as IngredientList)
            is InstructionsViewHolder -> holder.bind(items[position] as Instructions)
            is TitleViewHolder -> holder.bind(items[position] as Title)
        }.exhaustive
    }

    override fun getItemCount(): Int = items.size
}

sealed class DetailScreenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class TitleViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {
        private val cocktailTitle = itemView.findViewById<TextView>(R.id.detail_screen_title)
        fun bind(title: Title) {
            cocktailTitle.text = title.title
        }
    }

    class ImageViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {
        private val cocktailImage: ImageView = itemView.findViewById(R.id.detail_screen_image)
        fun bind(image: Image) {
            cocktailImage.load(image.image)
        }
    }

    class GlassTypeViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {
        private val cocktailGlassType = itemView.findViewById<TextView>(R.id.glass_type)
        private val cocktailAlcoholicType = itemView.findViewById<TextView>(R.id.alcoholic_type)
        fun bind(glassType: GlassType) {
            cocktailGlassType.text = glassType.glass
            if (glassType.isAlcoholic) {
                cocktailAlcoholicType.text = itemView.context.getString(R.string.yes)
            } else {
                cocktailAlcoholicType.text = itemView.context.getString(R.string.no)
            }
        }
    }

    class IngredientListViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {
        private val recyclerView = itemView.findViewById<RecyclerView>(R.id.detail_screen_ingredient_list)
        fun bind(ingredientList: IngredientList) {
            val adapter = IngredientsListAdapter()
            recyclerView.adapter = adapter
            adapter.setIngredientList(ingredientList.ingredients)
        }
    }

    class InstructionsViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {
        private val cocktailInstructions = itemView.findViewById<TextView>(R.id.detail_screen_instructions)
        fun bind(instructions: Instructions) {
            cocktailInstructions.text = instructions.instructions
        }
    }
}

class IngredientsListAdapter :
    RecyclerView.Adapter<IngredientsViewHolder>() {
    // lista di ingredienti, inizializzata a empty
    private var detailIngredientList: List<Ingredient> = emptyList()

    // da xml a kotlin per ogni elemento della lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient, parent, false)
        return IngredientsViewHolder(view)
    }

    // Con questo metodo un elemento della lista è connesso a un viewHolder.
    // La position è l'indice dell'elemento nella lista
    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        holder.bind(detailIngredientList[position])
    }

    // ritorna il numero di elementi nella lista
    override fun getItemCount(): Int {
        return detailIngredientList.size
    }

    // inizializza la lista di ingredienti con quella passata come argomento
    fun setIngredientList(items: List<Ingredient>) {
        detailIngredientList = items
        Log.d("SIZE LIST", detailIngredientList.size.toString())
        notifyDataSetChanged()
    }
}

// View Holder è un elemento della lista. Per ogni elemento della lista visibile viene creato un viewHolder
// l'oggetto view è la rappresentazione in ingredient di un layout XML
class IngredientsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    // recupera la textView dell'elemento della lista di ingredienti
    private val ingredientName = itemView.findViewById<TextView>(R.id.ingredient_item_name)
    private val ingredientQty = itemView.findViewById<TextView>(R.id.ingredient_item_quantity)
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
