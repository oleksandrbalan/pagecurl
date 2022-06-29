package eu.wewox.pagecurl.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
internal fun SettingsPopup(
    onSnapToFirst: () -> Unit,
    onSnapToLast: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPopup by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(100.dp)
            .clickableWithoutRipple {
                showPopup = true
            }
    )

    if (showPopup) {
        Box(Modifier.fillMaxSize()) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { showPopup = false }
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = MaterialTheme.colors.primary,
                    elevation = 16.dp
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        TextButton(
                            onClick = {
                                onSnapToFirst()
                                showPopup = false
                            }
                        ) {
                            Text(
                                text = "Go to first",
                                color = MaterialTheme.colors.onPrimary
                            )
                        }
                        TextButton(
                            onClick = {
                                onSnapToLast()
                                showPopup = false
                            }
                        ) {
                            Text(
                                text = "Go to last",
                                color = MaterialTheme.colors.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.clickableWithoutRipple(onClick: () -> Unit) = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
