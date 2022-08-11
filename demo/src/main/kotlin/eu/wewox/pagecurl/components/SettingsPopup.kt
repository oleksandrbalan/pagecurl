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
import androidx.compose.material.Card
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.config.InteractionConfig
import eu.wewox.pagecurl.config.copy

@Composable
internal fun SettingsPopup(
    interaction: InteractionConfig,
    onConfigChange: (InteractionConfig) -> Unit,
    onDismiss: () -> Unit,
) {
    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(focusable = true),
        onDismissRequest = onDismiss,
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = 16.dp,
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
                    enabled = interaction.drag.forward.enabled,
                    onChanged = { onConfigChange(interaction.copy(dragForwardEnabled = it)) },
                    modifier = switchRowModifier
                )
                SwitchRow(
                    text = "Backward drag enabled",
                    enabled = interaction.drag.backward.enabled,
                    onChanged = { onConfigChange(interaction.copy(dragBackwardEnabled = it)) },
                    modifier = switchRowModifier
                )
                SwitchRow(
                    text = "Forward tap enabled",
                    enabled = interaction.tap.forward.enabled,
                    onChanged = { onConfigChange(interaction.copy(tapForwardEnabled = it)) },
                    modifier = switchRowModifier
                )
                SwitchRow(
                    text = "Backward tap enabled",
                    enabled = interaction.tap.backward.enabled,
                    onChanged = { onConfigChange(interaction.copy(tapBackwardEnabled = it)) },
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
