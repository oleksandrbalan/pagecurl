package eu.wewox.pagecurl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import eu.wewox.pagecurl.HowToPageData
import eu.wewox.pagecurl.ui.SpacingLarge
import eu.wewox.pagecurl.ui.SpacingMedium

@Composable
fun HowToPage(
    index: Int,
    page: HowToPageData,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(SpacingMedium, Alignment.CenterVertically),
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingLarge)
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.h4,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = page.message,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text(
            text = index.toString(),
            color = MaterialTheme.colors.background,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(MaterialTheme.colors.onBackground, RoundedCornerShape(topStartPercent = 100))
                .padding(SpacingMedium)
        )
    }
}