package com.jeppung.foody.ui.fragments.recipes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeppung.foody.viewmodels.MainViewModel
import com.jeppung.foody.adapters.RecipesAdapter
import com.jeppung.foody.databinding.FragmentRecipesBinding
import com.jeppung.foody.util.NetworkResult
import com.jeppung.foody.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private lateinit var binding: FragmentRecipesBinding
    private val viewModel: MainViewModel by viewModels()
    private val recipesViewModel: RecipesViewModel by viewModels()
    private val adapter by lazy { RecipesAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        requestApiData()
    }

    private fun requestApiData() {
        viewModel.getRecipes(recipesViewModel.applyQueries())
        viewModel.recipesResponse.observe(viewLifecycleOwner) {
            when(it) {
                is NetworkResult.Loading -> {
                    binding.itemShimmerRecipes.itemShimmerRecipes.visibility = View.VISIBLE
                    binding.recipesRv.visibility = View.GONE
                }
                is NetworkResult.Error -> {
                    binding.itemShimmerRecipes.itemShimmerRecipes.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Success -> {
                    binding.recipesRv.visibility = View.VISIBLE
                    binding.itemShimmerRecipes.itemShimmerRecipes.visibility = View.GONE
                    adapter.setData(it.data!!)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recipesRv.adapter = adapter
        binding.recipesRv.layoutManager = LinearLayoutManager(requireContext())
    }

}