package com.jeppung.foody.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jeppung.foody.models.FoodRecipe
import com.jeppung.foody.util.Constants

@Entity(tableName = Constants.RECIPES_TABLE)
class RecipesEntity(
    var foodRecipe: FoodRecipe
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}