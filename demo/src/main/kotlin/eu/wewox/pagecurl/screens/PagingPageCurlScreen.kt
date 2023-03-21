@file:OptIn(ExperimentalPageCurlApi::class)

package eu.wewox.pagecurl.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.page.PageCurl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * PageCurl with lazy paging.
 * Example how component could be used with paging implementation.
 */
@Composable
fun PagingPageCurlScreen() {
    val items = rememberPager().collectAsLazyPagingItems()

    val extraItem = items.loadState.append is LoadState.Loading || items.loadState.refresh is LoadState.Loading
    val count = items.itemCount + (if (extraItem) 1 else 0)

    Box(Modifier.fillMaxSize()) {
        PageCurl(count = count) { index ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {
                if (index < items.itemCount) {
                    Text(items[index]?.content.orEmpty())
                } else {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun rememberPager(): Flow<PagingData<Item>> =
    remember {
        Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 3,
            ),
            pagingSourceFactory = { ItemPagingSource(BackendService()) }
        ).flow
    }

private class Item(val content: String)

private class Response(val items: List<Item>, val nextPageNumber: Int)

private class BackendService {

    suspend fun searchItems(page: Int): Response {
        delay(5_000L)
        return Response(
            items = List(10) { Item("Content #${page * 10 + it}") },
            nextPageNumber = page + 1
        )
    }
}

private class ItemPagingSource(private val backend: BackendService) : PagingSource<Int, Item>() {

    @Suppress("TooGenericExceptionCaught") // It is only for demo purpose
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> =
        try {
            val nextPageNumber = params.key ?: 0
            val response = backend.searchItems(nextPageNumber)
            LoadResult.Page(
                data = response.items,
                prevKey = null, // Only paging forward.
                nextKey = response.nextPageNumber
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
}
