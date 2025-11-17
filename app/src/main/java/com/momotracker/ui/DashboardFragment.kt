package com.momotracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.momotracker.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Setup RecyclerView for recent items
        binding.recentItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            // adapter = RecentItemsAdapter() // Uncomment when adapter is created
        }

        // Setup refresh listener
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshDashboard()
        }

        // Setup action buttons
        binding.addButton.setOnClickListener {
            // Navigate to add new item screen
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe dashboard state
                launch {
                    viewModel.dashboardState.collect { state ->
                        updateUI(state)
                    }
                }

                // Observe loading state
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.swipeRefreshLayout.isRefreshing = isLoading
                    }
                }
            }
        }
    }

    private fun updateUI(state: DashboardState) {
        binding.apply {
            // Update summary statistics
            totalCountText.text = state.totalCount.toString()
            totalAmountText.text = String.format("%.2f", state.totalAmount)
            
            // Update recent items
            // recentItemsAdapter.submitList(state.recentItems)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Data class for dashboard state
data class DashboardState(
    val totalCount: Int = 0,
    val totalAmount: Double = 0.0,
    val recentItems: List<Any> = emptyList() // Replace Any with your data model
)
