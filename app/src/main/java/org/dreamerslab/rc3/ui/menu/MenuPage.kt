package org.dreamerslab.rc3.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.dreamerslab.rc3.data.models.MenuItem
import org.dreamerslab.rc3.design_system.components.LoadingIndicator
import org.dreamerslab.rc3.design_system.components.NetworkImage
import org.dreamerslab.rc3.design_system.components.PrimaryButton
import org.dreamerslab.rc3.design_system.components.SearchField
import org.dreamerslab.rc3.design_system.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPage(
    onNavigateUp: () -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    viewModel: MenuPageViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()
    val state = uiState.value

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Navigate Up"
                        )
                    }
                },
                title = {
                    title?.let { Text(it) }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onCartClick) {
                Box {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart"
                    )

                    Text(
                        text = (state as? MenuPageState.Success)?.cartItems?.size?.toString() ?: "0",
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface).align(Alignment.TopEnd),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
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

            when (state) {
                MenuPageState.Loading -> LoadingIndicator(
                    modifier = Modifier.fillMaxSize()
                )

                is MenuPageState.Success -> MenuItemsList(
                    menuItems = state.menuItems,
                    onAddToCartClick = viewModel::addToCart,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun MenuItemsList(
    menuItems: List<MenuItem>,
    onAddToCartClick: (MenuItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        contentPadding = PaddingValues(MaterialTheme.spacing.large)
    ) {
        items(
            items = menuItems
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
                    Text("Rs ${it.price}")
                },
                trailingContent = {
                    PrimaryButton(
                        label = "Add to cart",
                        onClick = { onAddToCartClick(it) }
                    )
                },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { },
                shadowElevation = 5.dp,
                tonalElevation = 3.dp,
            )
        }
    }
}
