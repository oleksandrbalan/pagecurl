package eu.wewox.pagecurl

data class HowToPageData(
    val title: String,
    val message: String,
) {
    companion object {
        val simpleHowToPages = listOf(
            HowToPageData(
                "Welcome \uD83D\uDC4B",
                "This is a simple demo of the PageCurl. Swipe to the left to turn the page.",
            ),
            HowToPageData(
                "Forward & backward",
                "Nice, now try another direction to go backward.",
            ),
            HowToPageData(
                "Taps",
                "You may also just tap in the right half of the screen to go forward and tap on the left one to go backward.",
            ),
            HowToPageData(
                "End",
                "That is the last page, you cannot go further \uD83D\uDE09",
            )
        )

        val interactionHowToPages = listOf(
            HowToPageData(
                "Interaction example",
                "This example demonstrates how drag & tap gestures can be toggled on or off. By default all gestures are allowed.",
            ),
            HowToPageData(
                "Custom tap",
                "This example has a custom tap configured to show a settings popup. Try it and tap somewhere near the center of the page.",
            ),
            HowToPageData(
                "Settings",
                "Try to disable forward / backward drags and / or forward / backward taps.",
            ),
            HowToPageData(
                "End",
                "That is the last page, you cannot go further \uD83D\uDE09",
            )
        )
    }
}
