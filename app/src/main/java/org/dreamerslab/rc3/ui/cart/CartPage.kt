package org.dreamerslab.rc3.ui.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import org.dreamerslab.rc3.data.models.CartItem
import org.dreamerslab.rc3.design_system.components.NetworkImage
import org.dreamerslab.rc3.design_system.components.PrimaryButton
import org.dreamerslab.rc3.design_system.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPage(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartPageViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                    Text("Cart Page")
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.large)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Total = Rs ${state.price}",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    PrimaryButton(
                        label = "Proceed To Checkout",
                        onClick = {}
                    )
                }
            }
        }
    ) { innerPadding ->
        CartItemsList(
            items = state.items,
            onAddToCartClick = viewModel::addItem,
            onRemoveFromCartClick = viewModel::removeItem,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun CartItemsList(
    items: List<CartItem>,
    onAddToCartClick: (CartItem) -> Unit,
    onRemoveFromCartClick: (CartItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        contentPadding = PaddingValues(MaterialTheme.spacing.large)
    ) {
        items(
            items = items
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
                    ) {
                        IconButton(
                            onClick = {
                                onRemoveFromCartClick(it)
                            }
                        ) {
                            Text("-", style = MaterialTheme.typography.labelLarge)
                        }

                        Text(it.quantity.toString(), style = MaterialTheme.typography.labelLarge)

                        IconButton(
                            onClick = {
                                onAddToCartClick(it)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add"
                            )
                        }
                    }
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
