package eu.wewox.pagecurl.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
internal fun SettingsPopup(
    onSnapToFirst: () -> Unit,
    onSnapToLast: () -> Unit,
    onDismiss: () -> Unit,
) {
    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss,
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            backgroundColor = MaterialTheme.colors.primary,
            elevation = 16.dp,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                TextButton(
                    onClick = onSnapToFirst
                ) {
                    Text(
                        text = "Go to first",
                        color = MaterialTheme.colors.onPrimary
                    )
                }
                TextButton(
                    onClick = onSnapToLast
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
