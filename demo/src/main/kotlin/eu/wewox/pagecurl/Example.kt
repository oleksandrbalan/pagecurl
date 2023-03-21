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
    PagingPageCurl(
        "PageCurl with lazy paging",
        "Example how component could be used with paging implementation"
    ),
    SettingsPageCurl(
        "Page Curl With Settings",
        "Showcases how individual interactions can be toggled on / off"
    ),
    StateInPageCurl(
        "Page Curl With State Management",
        "Example how state can be used to change current page (snap / animate)"
    ),
    InteractionConfigInPageCurl(
        "Interactions Configurations In Page Curl",
        "Example interactions (drag / tap) can be customized"
    ),
    ShadowPageCurl(
        "Shadow Configuration in Page Curl",
        "Example how to customize shadow of the page"
    ),
    BackPagePageCurl(
        "Back-Page Configuration in Page Curl",
        "Example how to customize the back-page (the back of the page user see during the drag or animation)"
    ),
}
