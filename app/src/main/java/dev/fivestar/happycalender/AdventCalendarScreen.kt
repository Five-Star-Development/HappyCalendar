package dev.fivestar.happycalender

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class AdventCalendarItem(
    val day: Int,
    val imageResId: Int,
    val isUnlocked: Boolean = false
)

private fun validateItem(item: AdventCalendarItem): Boolean {
    val startDate = LocalDate.of(2024, 12, 1)
    val today = LocalDate.now()
    return (item.day - 1) <= ChronoUnit.DAYS.between(startDate, today)
}

@Composable
fun AdventCalendar(
    modifier: Modifier = Modifier,
    items: List<AdventCalendarItem>,
    onDoorClicked: (AdventCalendarItem) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(6),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
    var isOpen by remember { mutableStateOf(item.isUnlocked) }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(Color.LightGray)
            .clickable {
                if (!isOpen && validateItem(item)) {
                    isOpen = true
                }
            }
    ) {
        // Door number
        Text(
            text = item.day.toString(),
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(if (isOpen) 0f else 1f),
            color = Color.Black
        )

        // Revealed image
        AnimatedVisibility(
            visible = isOpen,
            enter = slideInHorizontally(
                animationSpec = tween(600)
            ) + fadeIn()
        ) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = "Day ${item.day} image",
                modifier = Modifier.fillMaxSize().clickable {
                    showDialog = true
                },
                contentScale = ContentScale.Crop
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = {
                Image(
                    painter = painterResource(id = item.imageResId),
                    contentDescription = "Day ${item.day} image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit // Adjust content scale as needed
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Schließen")
                }
            }
        )
    }
}

// Example Usage
@Composable
fun AdventCalendarScreen(modifier: Modifier, viewModel: CalendarViewModel) {

    var selectedItem by remember { mutableStateOf<AdventCalendarItem?>(null) }

    AdventCalendar(
        modifier = modifier,
        items = calendarItems.shuffled(),
        onDoorClicked = { item ->
            viewModel.onDoorClicked(item)
        },
    )

    viewModel.openDoorEvent.collectAsState(null).let { item ->
        selectedItem = item.value
    }
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