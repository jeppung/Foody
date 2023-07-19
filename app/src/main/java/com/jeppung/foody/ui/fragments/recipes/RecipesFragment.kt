package com.jeppung.foody.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeppung.foody.MainViewModel
import com.jeppung.foody.adapters.RecipesAdapter
import com.jeppung.foody.databinding.FragmentRecipesBinding
import com.jeppung.foody.util.Constants
import com.jeppung.foody.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private lateinit var binding: FragmentRecipesBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter: RecipesAdapter = RecipesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recipesRv.adapter = adapter
        binding.recipesRv.layoutManager = LinearLayoutManager(requireContext())

        viewModel.recipesResponse.observe(viewLifecycleOwner) {
            when(it) {
                is NetworkResult.Loading -> {
                    binding.itemShimmerRecipes.itemShimmerRecipes.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    binding.itemShimmerRecipes.itemShimmerRecipes.visibility = View.GONE
                }
                is NetworkResult.Success -> {
                    binding.itemShimmerRecipes.itemShimmerRecipes.visibility = View.GONE
                    adapter.setData(it.data!!)
                }
            }
        }
    }

}