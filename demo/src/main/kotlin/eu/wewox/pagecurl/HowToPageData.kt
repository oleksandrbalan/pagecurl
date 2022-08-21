@file:Suppress("MaxLineLength", "UndocumentedPublicProperty")

package eu.wewox.pagecurl

/**
 * The model for data for the page.
 *
 * @property title The title to show on the page.
 * @property message The message to show on the page.
 */
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

        val stateHowToPages = listOf(
            HowToPageData(
                "State example",
                "This example demonstrates how state object can be used to change current page.",
            ),
            HowToPageData(
                "Custom tap",
                "This example has a custom tap configured to show a settings row below. Try it and tap somewhere near the center of the page. Tap on the PageCurl to zoom back in.",
            ),
            HowToPageData(
                "Settings",
                "Use buttons to go to the first / last page, or try to snap / animate forward or backward. The .snapTo() method changes the current page immediately, but .next() and .prev() methods changes the current page with a default animation. This animation could be customized, see DefaultNext and DefaultPrev in library sources.",
            ),
            HowToPageData(
                "End",
                "That is the last page, you cannot go further \uD83D\uDE09",
            )
        )

        val interactionSettingsHowToPages = listOf(
            HowToPageData(
                "Another interaction example",
                "This example demonstrates how drag & tap gestures zones can be configured. By default it is right half to go forward and left half to go backward.",
            ),
            HowToPageData(
                "Custom tap",
                "This example has a custom tap configured to show a settings row below. Try it and tap somewhere near the center of the page. Tap on the PageCurl to zoom back in.",
            ),
            HowToPageData(
                "Settings",
                "Try to change the slider value and see how it changes the gesture zones. For example if you set 0.25f on the tap gesture this means, that the first 25% of the width will be dedicated for backward tap, and other 75% will be used for forward tap.",
            ),
            HowToPageData(
                "End region in drag",
                "Keep in mind, that drag gestures have 'end' region (where gesture should be ended to complete a drag), so if you set 0f for drag gesture, the only forward gesture will be allowed, but it could not be completed. You can modify the 'end' region as needed for your use-case.",
            ),
            HowToPageData(
                "End",
                "That is the last page, you cannot go further \uD83D\uDE09",
            )
        )

        val shadowHowToPages = listOf(
            HowToPageData(
                "Shadow configuration",
                "This example demonstrates how shadow can be configured.",
            ),
            HowToPageData(
                "Custom tap",
                "This example has a custom tap configured to show a settings row below. Try it and tap somewhere near the center of the page. Tap on the PageCurl to zoom back in.",
            ),
            HowToPageData(
                "Settings",
                "Try to change different sliders. Settings should be self-descriptive. You may also change the shadow color (not present in the example).",
            ),
            HowToPageData(
                "End",
                "That is the last page, you cannot go further \uD83D\uDE09",
            )
        )

        val backPageHowToPages = listOf(
            HowToPageData(
                "Back-page configuration",
                "This example demonstrates how back-page can be configured.",
            ),
            HowToPageData(
                "Custom tap",
                "This example has a custom tap configured to show a settings row below. Try it and tap somewhere near the center of the page. Tap on the PageCurl to zoom back in.",
            ),
            HowToPageData(
                "Settings",
                "The alpha slider configures how much content is visible on the back page. Bigger value means that page is more see-through.",
            ),
            HowToPageData(
                "Settings",
                "The color value defines a color of the back-page. It is more visible is the alpha value is low.",
            ),
            HowToPageData(
                "End",
                "That is the last page, you cannot go further \uD83D\uDE09",
            )
        )
    }
}
