package com.jeppung.foody.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.load
import com.jeppung.foody.R
import com.jeppung.foody.databinding.ItemRecipesBinding
import com.jeppung.foody.models.FoodRecipe
import com.jeppung.foody.models.Result
import com.jeppung.foody.util.RecipesDiffUtil

class RecipesAdapter: RecyclerView.Adapter<RecipesAdapter.ViewHolder>() {
    private var recipe = emptyList<Result>()

    inner class ViewHolder(private val binding: ItemRecipesBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Result, context: Context){
            binding.itemTitleView.text = item.title
            binding.itemImageView.load(item.image) {
                crossfade(600)
            }
            if(item.vegan) {
                binding.itemLeafView.setColorFilter(context.getColor(R.color.green))
                binding.itemLeafText.setTextColor(context.getColor(R.color.green))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipesBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recipe[position], holder.itemView.context)
    }

    override fun getItemCount(): Int = recipe.size


    fun setData(newData: FoodRecipe) {
        val recipesDiffUtil = RecipesDiffUtil(recipe, newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipe = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }
}