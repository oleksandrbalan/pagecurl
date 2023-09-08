@file:OptIn(ExperimentalPageCurlApi::class)

package eu.wewox.pagecurl.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState.Loading
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.page.PageCurl
import eu.wewox.pagecurl.page.rememberPageCurlState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * PageCurl with lazy paging.
 * Example how component could be used with paging implementation.
 */
@Composable
fun PagingPageCurlScreen() {
    val startPage = 4 // in range 0..9
    val startPageOffset = 5 // in range 0..9

    val items = rememberPager(startPage).collectAsLazyPagingItems()

    val loading = with(items.loadState) { refresh is Loading || append is Loading || prepend is Loading }

    Box(Modifier.fillMaxSize()) {
        if (items.loadState.refresh is Loading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                CircularProgressIndicator()
            }
        } else {
            PageCurl(
                count = items.itemCount,
                key = { index -> items.peek(index).hashCode() },
                state = rememberPageCurlState(initialCurrent = startPageOffset),
            ) { index ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Text(items[index]?.content.orEmpty())
                    Text(if (loading) "Loading..." else "")
                }
            }
        }
    }
}

@Composable
private fun rememberPager(startPage: Int): Flow<PagingData<Item>> =
    remember {
        Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 3,
            ),
            pagingSourceFactory = { ItemPagingSource(startPage, BackendService()) }
        ).flow
    }

private class Item(val content: String)

private class Response(val items: List<Item>, val prePageNumber: Int, val nextPageNumber: Int)

private class BackendService {

    suspend fun searchItems(page: Int): Response {
        delay(1_000L)
        return Response(
            items = List(10) { Item("Content #${page * 10 + it}") },
            prePageNumber = page - 1,
            nextPageNumber = page + 1,
        )
    }
}

private class ItemPagingSource(
    private val startPage: Int,
    private val backend: BackendService
) : PagingSource<Int, Item>() {

    @Suppress("TooGenericExceptionCaught") // It is only for demo purpose
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> =
        try {
            val nextPageNumber = params.key ?: startPage
            val response = backend.searchItems(nextPageNumber)
            LoadResult.Page(
                data = response.items,
                prevKey = response.prePageNumber.takeIf { it >= 0 },
                nextKey = response.nextPageNumber.takeIf { it < 10 }
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
