package eu.wewox.pagecurl

/**
 * Enumeration of available demo examples.
 *
 * @param label Example name.
 * @param description Brief description.
 */
enum class Example(
    val label: String,
    val description: String,
) {
    SimplePageCurl(
        "Simple Page Curl",
        "Basic PageCurl usage"
    ),
    SettingsPageCurl(
        "Page Curl With Settings",
        "Showcases how individual interactions can be toggled on / off"
    ),
    CustomPageCurl(
        "Page Curl With State Management",
        "Example how state can be used to change current page (snap / animate)"
    ),
    AnimatePageCurl(
        "Page Curl With Custom Animation",
        "Example how custom animation can be provided for forward / backward taps"
    ),
}
