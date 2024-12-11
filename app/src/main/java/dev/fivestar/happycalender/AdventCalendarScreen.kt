package dev.fivestar.happycalender

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

data class AdventCalendarItem(
    val day: Int,
    val imageResId: Int,
    var isUnlocked: Boolean = false
)

@Composable
fun AdventCalendar(
    modifier: Modifier = Modifier,
    items: List<AdventCalendarItem>,
    onDoorClicked: (AdventCalendarItem) -> Unit
) {
    Image(
        painter = painterResource(id = R.drawable.bg),
        contentDescription = "background",
        modifier = Modifier
            .fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(6),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {

        }

        items(items) { item ->
            AdventCalendarDoor(
                item = item,
                onClick = { onDoorClicked(item) }
            )
        }
    }
}

@Composable
fun AdventCalendarDoor(
    item: AdventCalendarItem,
    onClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(Color(255,255,255, 15))
            .clickable {
                onClick.invoke()
            }
    ) {
        // Door number
        Text(
            text = item.day.toString(),
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(if (item.isUnlocked) 0f else 1f),
            color = Color.White
        )

        // Revealed image
        AnimatedVisibility(
            visible = item.isUnlocked,
            enter = slideInHorizontally(
                animationSpec = tween(600)
            ) + fadeIn()
        ) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = "Day ${item.day} image",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        showDialog = true
                    },
                contentScale = ContentScale.Crop
            )
        }
    }

    if (showDialog) {
        val imageBitmap = ImageBitmap.imageResource(
            LocalContext.current.resources,
            item.imageResId
        )
        AlertDialog(
            onDismissRequest = { showDialog = false },
            modifier = Modifier
                .width(imageBitmap.width.dp)
                .fillMaxHeight(),
            text = {
                Box {
                    ZoomableImage(item.imageResId)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Schließen")
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }
}

// Example Usage
@Composable
fun AdventCalendarScreen(modifier: Modifier, viewModel: CalendarViewModel) {
    viewModel.uiState.collectAsState(null).let {
        Log.d("AdventCalendarScreen", "list plz $it")
        it.value?.let { state ->
            AdventCalendar(
                modifier = modifier,
                items = state.items,
                onDoorClicked = { item ->
                    viewModel.onDoorClicked(item)
                },
            )
        }
    }
}

@Composable
fun ZoomableImage(resId: Int) {

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += panChange
    }

    Image(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .transformable(state)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .padding(16.dp),
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true, name = "Advent Calendar")
@Composable
fun AdventCalendarPreview() {
    MaterialTheme {
        val previewItems = List(24) { day ->
            AdventCalendarItem(
                day = day + 1,
                imageResId = android.R.drawable.ic_dialog_info, // Use a system drawable for preview
                isUnlocked = day < 5 // First 5 doors unlocked for preview
            )
        }

        AdventCalendar(
            items = previewItems,
            onDoorClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "Single Door")
@Composable
fun AdventCalendarDoorPreview() {
    MaterialTheme {
        AdventCalendarDoor(
            item = AdventCalendarItem(
                day = 1,
                imageResId = android.R.drawable.ic_dialog_info
            ),
            onClick = {}
        )
    }
}