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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.wewox.pagecurl.config.PageCurlConfig
import eu.wewox.pagecurl.page.PageCurl
import eu.wewox.pagecurl.ui.theme.PageCurlTheme
import eu.wewox.pagecurl.utils.Data

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PageCurlTheme {
                var current by remember { mutableStateOf(0) }
                val count = 6
                PageCurl(
                    current = current,
                    count = count,
                    onCurrentChange = {
                        current = it
                    },
                    config = PageCurlConfig(),
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
                            count - 1 -> {
                                Text(
                                    text = "The End",
                                    fontSize = 34.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center)
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
            }
        }
    }
}
