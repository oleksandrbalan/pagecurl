@file:OptIn(ExperimentalPageCurlApi::class)

package eu.wewox.pagecurl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import eu.wewox.pagecurl.components.SettingsAction
import eu.wewox.pagecurl.components.SettingsPopup
import eu.wewox.pagecurl.config.CurlConfig
import eu.wewox.pagecurl.config.InteractionConfig
import eu.wewox.pagecurl.config.PageCurlConfig
import eu.wewox.pagecurl.config.ShadowConfig
import eu.wewox.pagecurl.config.copy
import eu.wewox.pagecurl.page.PageCurl
import eu.wewox.pagecurl.page.PageCurlState
import eu.wewox.pagecurl.page.rememberPageCurlState
import eu.wewox.pagecurl.ui.theme.PageCurlTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PageCurlTheme {
                Box {
                    val state = rememberPageCurlState(max = 6)
                    var showPopup by remember { mutableStateOf(false) }
                    var interaction by remember {
                        mutableStateOf(
                            InteractionConfig(
                                tap = InteractionConfig.Tap(
                                    custom = InteractionConfig.Tap.CustomInteraction(true) { size, position ->
                                        if ((position - size.center.toOffset()).getDistance() < 64.dp.toPx()) {
                                            showPopup = true
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                )
                            )
                        )
                    }

                    val curlConfig by derivedStateOf {
                        val offset = if (state.progress > 0f) 0f else 1f
                        val shadowAlpha = (offset + state.progress) * 0.5f

                        CurlConfig(
                            shadow = ShadowConfig(
                                alpha = shadowAlpha
                            )
                        )
                    }

                    PageCurl(
                        state = state,
                        config = PageCurlConfig(
                            curl = curlConfig,
                            interaction = interaction
                        )
                    ) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                        ) {
                            when (index) {
                                0 -> {
                                    Image(
                                        painter = painterResource(R.drawable.img_sleep),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                                else -> {
                                    Text(
                                        text = if (index % 2 == 1) Data.Lorem1 else Data.Lorem2,
                                        fontSize = 22.sp,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }

                            Text(
                                text = index.toString(),
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(Color.Black, RoundedCornerShape(topStartPercent = 100))
                                    .padding(16.dp)
                            )
                        }
                    }

                    if (showPopup) {
                        SettingsPopup(
                            state = state,
                            interaction = interaction,
                            onConfigChange = {
                                interaction = it
                            },
                            onDismiss = {
                                showPopup = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsPopup(
    state: PageCurlState,
    interaction: InteractionConfig,
    onConfigChange: (InteractionConfig) -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    SettingsPopup(
        interactionConfig = interaction,
        onAction = { action ->
            when (action) {
                SettingsAction.GoToFirst -> {
                    scope.launch {
                        state.snapTo(0)
                    }
                }
                SettingsAction.GoToLast -> {
                    scope.launch {
                        state.snapTo(state.max - 1)
                    }
                }
                is SettingsAction.ForwardDragEnabled -> {
                    onConfigChange(interaction.copy(dragForwardEnabled = action.value))
                }
                is SettingsAction.BackwardDragEnabled -> {
                    onConfigChange(interaction.copy(dragBackwardEnabled = action.value))
                }
                is SettingsAction.ForwardTapEnabled -> {
                    onConfigChange(interaction.copy(tapForwardEnabled = action.value))
                }
                is SettingsAction.BackwardTapEnabled -> {
                    onConfigChange(interaction.copy(tapBackwardEnabled = action.value))
                }
            }
        },
        onDismiss = onDismiss
    )
}

private object Data {
    val Lorem1 =
        "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aliquam erat volutpat. Phasellus enim erat, vestibulum vel, aliquam a, posuere eu, velit. Et harum quidem rerum facilis est et expedita distinctio. In sem justo, commodo ut, suscipit at, pharetra vitae, orci. Vestibulum fermentum tortor id mi. Sed elit dui, pellentesque a, faucibus vel, interdum nec, diam. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat. Nunc tincidunt ante vitae massa. Maecenas fermentum, sem in pharetra pellentesque, velit turpis volutpat ante, in pharetra metus odio a lectus. Proin in tellus sit amet nibh dignissim sagittis. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos. Etiam posuere lacus quis dolor. Class aptent taciti sociosqu ad litora."
    val Lorem2 =
        "Phasellus enim erat, vestibulum vel, aliquam a, posuere eu, velit. Duis ante orci, molestie vitae vehicula venenatis, tincidunt ac pede. Aliquam in lorem sit amet leo accumsan lacinia. Morbi imperdiet, mauris ac auctor dictum, nisl ligula egestas nulla, et sollicitudin sem purus in lacus. Ut tempus purus at lorem. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Integer malesuada. Sed vel lectus. Donec odio tempus molestie, porttitor ut, iaculis quis, sem. Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Etiam egestas wisi a erat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Maecenas lorem. Mauris dolor felis, sagittis at, luctus sed, aliquam non, tellus. Aenean id metus id velit ullamcorper pulvinar. Integer malesuada."
}
