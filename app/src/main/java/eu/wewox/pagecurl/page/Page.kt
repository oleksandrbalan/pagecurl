package eu.wewox.pagecurl.page

import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import eu.wewox.pagecurl.R
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

    var posA by remember { mutableStateOf<Offset?>(null) }
    var posB by remember { mutableStateOf<Offset?>(null) }

    SoftwareLayerComposable(
        Modifier
            .curlGesture(
                onCurl = { a, b ->
                    posA = a
                    posB = b
                },
                onCancel = {
                    posA = null
                    posB = null
                }
            )
            .drawCurl(CurlConfig(), posA, posB)
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

@Composable
fun SoftwareLayerComposable(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AndroidView(
        factory = { context ->
            ComposeView(context).apply {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }
        },
        update = { composeView ->
            composeView.setContent(content)
        },
        modifier = modifier,
    )
}
