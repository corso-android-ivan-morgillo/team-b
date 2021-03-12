package com.ivanmorgillo.corsoandroid.teamb.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teamb.R
import com.ivanmorgillo.corsoandroid.teamb.databinding.DetailScreenGlasstypeBinding
import com.ivanmorgillo.corsoandroid.teamb.databinding.DetailScreenImageBinding
import com.ivanmorgillo.corsoandroid.teamb.databinding.DetailScreenIngredientsBinding
import com.ivanmorgillo.corsoandroid.teamb.databinding.DetailScreenInstructionsBinding
import com.ivanmorgillo.corsoandroid.teamb.databinding.DetailScreenVideoBinding
import com.ivanmorgillo.corsoandroid.teamb.databinding.IngredientBinding
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenItems.GlassType
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenItems.Image
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenItems.IngredientList
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenItems.Instructions
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenItems.Video
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenViewHolder.GlassTypeViewHolder
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenViewHolder.ImageViewHolder
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenViewHolder.IngredientListViewHolder
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenViewHolder.InstructionsViewHolder
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenViewHolder.VideoViewHolder
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import com.ivanmorgillo.corsoandroid.teamb.utils.gone
import com.ivanmorgillo.corsoandroid.teamb.utils.visible
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

sealed class DetailScreenItems {
    data class Image(val image: String, val title: String, val isFavorite: Boolean) : DetailScreenItems()
    data class GlassType(val glass: String, val isAlcoholic: Boolean) : DetailScreenItems()
    data class IngredientList(val ingredients: List<IngredientUI>) : DetailScreenItems()
    data class Instructions(val instructions: String) : DetailScreenItems()
    data class Video(val video: String?) : DetailScreenItems()
}

private const val GLASS_TYPE_VIEWTYPE = 1
private const val IMAGE_VIEWTYPE = 2
private const val INGREDIENT_LIST_VIEWTYPE = 3
private const val INSTRUCTIONS_VIEWTYPE = 4
private const val VIDEO_VIEWTYPE = 6
private const val START_SECONDS = 0f

class DetailScreenAdapter
    (private val onFavoriteClicked: () -> Unit) : RecyclerView.Adapter<DetailScreenViewHolder>() {
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
            is Video -> VIDEO_VIEWTYPE
        }.exhaustive
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailScreenViewHolder {
        return when (viewType) {
            GLASS_TYPE_VIEWTYPE -> {
                val binding = DetailScreenGlasstypeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                GlassTypeViewHolder(binding)
            }
            IMAGE_VIEWTYPE -> {
                val binding = DetailScreenImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ImageViewHolder(binding)
            }
            INGREDIENT_LIST_VIEWTYPE -> {
                val binding = DetailScreenIngredientsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                IngredientListViewHolder(binding)
            }
            INSTRUCTIONS_VIEWTYPE -> {
                val binding = DetailScreenInstructionsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                InstructionsViewHolder(binding)
            }
            VIDEO_VIEWTYPE -> {
                val binding = DetailScreenVideoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                VideoViewHolder(binding)
            }
            else -> error("ViewType not valid")
        }.exhaustive
    }

    override fun onBindViewHolder(holder: DetailScreenViewHolder, position: Int) {
        when (holder) {
            is GlassTypeViewHolder -> holder.bind(items[position] as GlassType)
            is ImageViewHolder -> holder.bind(items[position] as Image, onFavoriteClicked)
            is IngredientListViewHolder -> holder.bind(items[position] as IngredientList)
            is InstructionsViewHolder -> holder.bind(items[position] as Instructions)
            is VideoViewHolder -> holder.bind(items[position] as Video)
        }.exhaustive
    }

    override fun getItemCount(): Int = items.size
}

sealed class DetailScreenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class ImageViewHolder(private val binding: DetailScreenImageBinding) :
        DetailScreenViewHolder(binding.root) {
        fun bind(item: Image, onClick: () -> Unit) {
            binding.detailScreenImage.load(item.image)
            binding.detailScreenTitle.text = item.title
            binding.detailAddFavorite.setOnClickListener { onClick() }
            if (item.isFavorite) {
                binding.detailAddFavorite.setImageResource(R.drawable.ic_star)
            } else {
                binding.detailAddFavorite.setImageResource(R.drawable.ic_star_border)
            }
        }
    }

    class GlassTypeViewHolder(private val binding: DetailScreenGlasstypeBinding) :
        DetailScreenViewHolder(binding.root) {
        fun bind(glassType: GlassType) {
            binding.glassType.text = glassType.glass
            if (glassType.isAlcoholic) {
                binding.alcoholicType.text = itemView.context.getString(R.string.yes)
            } else {
                binding.alcoholicType.text = itemView.context.getString(R.string.no)
            }
        }
    }

    class IngredientListViewHolder(private val binding: DetailScreenIngredientsBinding) :
        DetailScreenViewHolder(binding.root) {
        fun bind(ingredientList: IngredientList) {
            val adapter = IngredientsListAdapter()
            binding.detailScreenIngredientList.adapter = adapter
            adapter.setIngredientList(ingredientList.ingredients)
        }
    }

    class InstructionsViewHolder(private val binding: DetailScreenInstructionsBinding) :
        DetailScreenViewHolder(binding.root) {
        fun bind(instructions: Instructions) {
            binding.detailScreenInstructions.text = instructions.instructions
        }
    }

    class VideoViewHolder(private val binding: DetailScreenVideoBinding) :
        DetailScreenViewHolder(binding.root) {
        fun bind(video: Video) {
            if (video.video.isNullOrEmpty()) { // se il video non è presente
                binding.youtubePlayerView.gone()
            } else {
                binding.youtubePlayerView.visible()
                binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        video.video
                            .split("v=")
                            .lastOrNull()
                            ?.run { youTubePlayer.loadVideo(this, START_SECONDS) }
                    }
                })
            }
        }
    }
}

class IngredientsListAdapter :
    RecyclerView.Adapter<IngredientsViewHolder>() {
    // lista di ingredienti, inizializzata a empty
    private var detailIngredientList: List<IngredientUI> = emptyList()

    // da xml a kotlin per ogni elemento della lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val binding = IngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientsViewHolder(binding)
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
    fun setIngredientList(items: List<IngredientUI>) {
        detailIngredientList = items
        Log.d("SIZE LIST", detailIngredientList.size.toString())
        notifyDataSetChanged()
    }
}

class IngredientsViewHolder(val binding: IngredientBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: IngredientUI) {
        val bullet = "• " + item.nomeIngr
        binding.ingredientItemName.text = bullet
        binding.ingredientItemQuantity.text = item.ingrQty
    }
}

data class IngredientUI(val nomeIngr: String, val ingrQty: String)
