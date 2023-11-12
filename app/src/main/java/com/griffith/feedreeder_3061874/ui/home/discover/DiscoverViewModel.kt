package com.griffith.feedreeder_3061874.ui.home.discover

import androidx.lifecycle.ViewModel
import com.griffith.feedreeder_3061874.Graph
import com.griffith.feedreeder_3061874.data.Category
import com.griffith.feedreeder_3061874.data.CategoryStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DiscoverViewModel(
    private val categoryStore: CategoryStore = Graph.categoryStore
) : ViewModel() {
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    private val _state = MutableStateFlow(DiscoverViewState())

    val state: StateFlow<DiscoverViewState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(
                categoryStore.categoriesSortedByFeedCount()
                    .onEach { categories ->
                        if (categories.isEmpty() && _selectedCategory.value == null) {
                            _selectedCategory.value = categories[0]
                        }
                    },
                _selectedCategory
            ) { categories, selectedCategories ->
                DiscoverViewState(
                    categories  = categories,
                    selectedCategory = selectedCategories
                )
            }.collect{ _state.value = it}
        }
    }
    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category
    }
}

data class DiscoverViewState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null
)
