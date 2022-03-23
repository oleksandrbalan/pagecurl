package eu.wewox.pagecurl.page

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.wewox.pagecurl.utils.Data

@Composable
fun Page() {
    Text(
        text = Data.Lorem2,
        fontSize = 22.sp,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    )

    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        Modifier
            .drawWithContent {
                val curlWidth = size.width * progress

                val midX = size.width - curlWidth
                val path = Path()
                path.lineTo(midX, 0f)
                path.lineTo(midX, size.height)
                path.lineTo(0f, size.height)
                clipPath(path) { this@drawWithContent.drawContent() }

                drawLine(
                    Brush.horizontalGradient(
                        listOf(Color.Black.copy(alpha = 0.5f), Color.Black.copy(alpha = 0f)),
                        midX - curlWidth * 0.8f + 40f,
                        midX - curlWidth * 0.8f - 40f
                    ),
                    Offset(midX - curlWidth * 0.8f, size.height),
                    Offset(midX - curlWidth * 0.8f, 0f),
                    80f
                )

                drawLine(
                    Brush.horizontalGradient(
                        listOf(Color.Black.copy(alpha = 0.5f), Color.Black.copy(alpha = 0f)),
                        midX - 30f,
                        midX + 30f
                    ),
                    Offset(midX, size.height),
                    Offset(midX, 0f),
                    60f
                )

                drawRect(
                    Color.White,
                    topLeft = Offset(midX - curlWidth * 0.8f, 0f),
                    size = Size(curlWidth * 0.8f, size.height)
                )


                withTransform({
                    scale(-1f, 1f)
                    clipRect(size.width - midX + curlWidth * 0.8f, 0f, size.width - midX, size.height)
                    translate(-midX + curlWidth * 0.8f)
                }) {
                    this@drawWithContent.drawContent()
                    drawRect(Color.White.copy(alpha = 0.8f))
                }
            }
    ) {
        Text(
            text = Data.Lorem1,
            fontSize = 22.sp,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        )
        //        Image(
        //            painter = painterResource(R.drawable.img_sleep),
        //            contentDescription = null,
        //            contentScale = ContentScale.Crop,
        //            modifier = Modifier
        //                .fillMaxSize()
        //        )
    }
}
