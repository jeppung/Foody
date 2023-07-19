package com.jeppung.foody

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jeppung.foody.data.Repository
import com.jeppung.foody.models.FoodRecipe
import com.jeppung.foody.util.Constants
import com.jeppung.foody.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
): AndroidViewModel(application) {

    private val _recipesResponse = MutableLiveData<NetworkResult<FoodRecipe>>()
    val recipesResponse: LiveData<NetworkResult<FoodRecipe>> = _recipesResponse

    fun getRecipes(queries: Map<String, String>) {
        viewModelScope.launch {
            getRecipesSafeCall(queries)
        }
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        _recipesResponse.value = NetworkResult.Loading()
        delay(1000)
        if(hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                _recipesResponse.value = handleFoodRecipesResponse(response)
            }catch (e: Exception) {
                _recipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        }else{
            _recipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.code() == 401 -> {
                return NetworkResult.Error("Not Authenticated")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Check internet connection availability
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilites = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilites.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilites.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilites.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    init {
        getRecipes(hashMapOf(
            "apiKey" to Constants.API_KEY,
            "query" to "pasta"
        ))
    }
}