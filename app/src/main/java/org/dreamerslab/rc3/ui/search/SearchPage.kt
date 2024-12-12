package org.dreamerslab.rc3.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.dreamerslab.rc3.data.models.Restaurant
import org.dreamerslab.rc3.design_system.components.NetworkImage
import org.dreamerslab.rc3.design_system.components.SearchField
import org.dreamerslab.rc3.design_system.theme.spacing

@Composable
fun SearchPage(
    onRestaurantClick: (Restaurant) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = modifier,
        topBar = { SearchScreenAppBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            val query by viewModel.query.collectAsStateWithLifecycle()

            SearchField(
                query = query,
                onChange = viewModel::updateQuery,
                onSearchAction = {},
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.spacing.large,
                    vertical = MaterialTheme.spacing.small,
                ),
            )

            val uiState by viewModel.state.collectAsStateWithLifecycle()

            when (val state = uiState) {
                SearchScreenState.Initial -> Unit
                is SearchScreenState.Data -> RestaurantsList(
                    restaurants = state.restaurants,
                    onRestaurantClick = onRestaurantClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = { Text("Search") }
    )
}

@Composable
private fun RestaurantsList(
    restaurants: List<Restaurant>,
    onRestaurantClick: (Restaurant) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        contentPadding = PaddingValues(MaterialTheme.spacing.large)
    ) {
        items(
            items = restaurants
        ) {
            ListItem(
                leadingContent = {
                    NetworkImage(
                        url = it.image,
                        contentDescription = it.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(MaterialTheme.shapes.small),
                    )
                },
                headlineContent = {
                    Text(it.name)
                },
                supportingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(it.rating.toString())

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xffFFD700),
                            modifier = Modifier.size(20.dp).padding(start = 2.dp)
                        )
                    }
                },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onRestaurantClick(it) },
                shadowElevation = 5.dp,
                tonalElevation = 3.dp,
            )
        }
    }
}
