package com.momotracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.momotracker.R
import com.momotracker.databinding.FragmentHistoryBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()
    // private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupMenu()
        setupRecyclerView()
        setupSwipeToDelete()
        setupFilters()
        observeViewModel()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_history, menu)
                
                // Setup search functionality
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem?.actionView as? SearchView
                searchView?.apply {
                    queryHint = "Search history..."
                    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            query?.let { viewModel.searchHistory(it) }
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            newText?.let { viewModel.searchHistory(it) }
                            return true
                        }
                    })
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_filter -> {
                        showFilterDialog()
                        true
                    }
                    R.id.action_sort -> {
                        showSortDialog()
                        true
                    }
                    R.id.action_clear_all -> {
                        showClearAllDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        // historyAdapter = HistoryAdapter(
        //     onItemClick = { item -> navigateToDetail(item) },
        //     onItemLongClick = { item -> showItemOptions(item) }
        // )

        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            // adapter = historyAdapter
            setHasFixedSize(true)
        }

        // Setup empty state
        binding.emptyStateGroup.visibility = View.GONE
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // val item = historyAdapter.currentList[position]
                // viewModel.deleteItem(item)
                
                // Show undo snackbar
                Snackbar.make(
                    binding.root,
                    "Item deleted",
                    Snackbar.LENGTH_LONG
                ).setAction("UNDO") {
                    // viewModel.undoDelete()
                }.show()
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.historyRecyclerView)
    }

    private fun setupFilters() {
        binding.apply {
            // Date range filter chips
            chipAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.filterByDateRange(DateRange.ALL)
            }
            
            chipToday.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.filterByDateRange(DateRange.TODAY)
            }
            
            chipWeek.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.filterByDateRange(DateRange.WEEK)
            }
            
            chipMonth.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.filterByDateRange(DateRange.MONTH)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe history items
                launch {
                    viewModel.historyItems.collect { items ->
                        updateHistoryList(items)
                    }
                }

                // Observe loading state
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = 
                            if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                // Observe summary statistics
                launch {
                    viewModel.historySummary.collect { summary ->
                        updateSummary(summary)
                    }
                }
            }
        }
    }

    private fun updateHistoryList(items: List<Any>) {
        if (items.isEmpty()) {
            binding.emptyStateGroup.visibility = View.VISIBLE
            binding.historyRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateGroup.visibility = View.GONE
            binding.historyRecyclerView.visibility = View.VISIBLE
            // historyAdapter.submitList(items)
        }
    }

    private fun updateSummary(summary: HistorySummary) {
        binding.apply {
            summaryTotalItems.text = "Total Items: ${summary.totalItems}"
            summaryTotalAmount.text = "Total: ${String.format("%.2f", summary.totalAmount)}"
        }
    }

    private fun showFilterDialog() {
        // TODO: Show filter dialog with options
    }

    private fun showSortDialog() {
        // TODO: Show sort options dialog
    }

    private fun showClearAllDialog() {
        // TODO: Show confirmation dialog for clearing all history
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Enum for date range filtering
enum class DateRange {
    ALL, TODAY, WEEK, MONTH, CUSTOM
}

// Data class for history summary
data class HistorySummary(
    val totalItems: Int = 0,
    val totalAmount: Double = 0.0,
    val avgAmount: Double = 0.0
)
