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
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.config.InteractionConfig

internal sealed interface SettingsAction {
    object GoToFirst : SettingsAction
    object GoToLast : SettingsAction
    class ForwardDragEnabled(val value: Boolean) : SettingsAction
    class BackwardDragEnabled(val value: Boolean) : SettingsAction
    class ForwardTapEnabled(val value: Boolean) : SettingsAction
    class BackwardTapEnabled(val value: Boolean) : SettingsAction
}

@Composable
internal fun SettingsPopup(
    interactionConfig: InteractionConfig,
    onAction: (SettingsAction) -> Unit,
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
                TextButton(onClick = { onAction(SettingsAction.GoToFirst) }) {
                    Text("Go to first")
                }
                TextButton(onClick = { onAction(SettingsAction.GoToLast) }) {
                    Text("Go to last")
                }

                val switchRowModifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                SwitchRow(
                    text = "Forward drag enabled",
                    enabled = interactionConfig.drag.forward.enabled,
                    onChanged = { onAction(SettingsAction.ForwardDragEnabled(it)) },
                    modifier = switchRowModifier
                )
                SwitchRow(
                    text = "Backward drag enabled",
                    enabled = interactionConfig.drag.backward.enabled,
                    onChanged = { onAction(SettingsAction.BackwardDragEnabled(it)) },
                    modifier = switchRowModifier
                )
                SwitchRow(
                    text = "Forward tap enabled",
                    enabled = interactionConfig.tap.forward.enabled,
                    onChanged = { onAction(SettingsAction.ForwardTapEnabled(it)) },
                    modifier = switchRowModifier
                )
                SwitchRow(
                    text = "Backward tap enabled",
                    enabled = interactionConfig.tap.backward.enabled,
                    onChanged = { onAction(SettingsAction.BackwardTapEnabled(it)) },
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
