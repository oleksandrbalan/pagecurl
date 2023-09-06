@file:OptIn(ExperimentalPageCurlApi::class)

package eu.wewox.pagecurl.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.config.PageCurlConfig

@Composable
internal fun SettingsPopup(
    config: PageCurlConfig,
    onDismiss: () -> Unit,
) {
    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(focusable = true),
        onDismissRequest = onDismiss,
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .padding(8.dp)
            ) {
                val switchRowModifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                SwitchRow(
                    text = "Forward drag enabled",
                    enabled = config.dragForwardEnabled,
                    onChanged = { config.dragForwardEnabled = it },
                    modifier = switchRowModifier
                )
                SwitchRow(
                    text = "Backward drag enabled",
                    enabled = config.dragBackwardEnabled,
                    onChanged = { config.dragBackwardEnabled = it },
                    modifier = switchRowModifier
                )
                SwitchRow(
                    text = "Forward tap enabled",
                    enabled = config.tapForwardEnabled,
                    onChanged = { config.tapForwardEnabled = it },
                    modifier = switchRowModifier
                )
                SwitchRow(
                    text = "Backward tap enabled",
                    enabled = config.tapBackwardEnabled,
                    onChanged = { config.tapBackwardEnabled = it },
                    modifier = switchRowModifier
                )
            }
        }
    }
}

@Composable
private fun SwitchRow(
    text: String,
    enabled: Boolean,
    onChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(text = text)
        Switch(
            checked = enabled,
            onCheckedChange = onChanged
        )
    }
}
