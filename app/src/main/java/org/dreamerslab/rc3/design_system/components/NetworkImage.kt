package org.dreamerslab.rc3.design_system.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: (@Composable BoxScope.() -> Unit)? = null,
    overlay: (@Composable BoxScope.() -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Crop,
    @DrawableRes editModePlaceholderRes: Int? = null
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        placeholder?.let { it() }

        val isInEditMode = LocalView.current.isInEditMode
        when {
            isInEditMode && editModePlaceholderRes != null -> Image(
                painter = painterResource(editModePlaceholderRes),
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier.matchParentSize()
            )

            isInEditMode && placeholder == null -> Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            else -> AsyncImage(
                model = url,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.matchParentSize()
            )
        }

        overlay?.let { it() }
    }
}
