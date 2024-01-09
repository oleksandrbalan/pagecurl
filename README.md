[![Maven Central](https://img.shields.io/maven-central/v/io.github.oleksandrbalan/pagecurl.svg?label=Maven%20Central)](https://mvnrepository.com/artifact/io.github.oleksandrbalan/pagecurl)

<img align="right" src="https://user-images.githubusercontent.com/20944869/200791917-a2436c9a-d062-4c14-9c71-c94fe8703061.png">

# Page Curl

Page Curl library for Jetpack Compose.

## Motivation

This library allows to create an effect of turning pages, which can be used in book reader applications, custom on-boarding screens or elsewhere.

## Usage

### Get a dependency

**Step 1.** Add the MavenCentral repository to your build file.
Add it in your root `build.gradle.kts` at the end of repositories:
```kotlin
allprojects {
    repositories {
        ...
        mavenCentral()
    }
}
```

Or in `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        ...
        mavenCentral()
    }
}
```

**Step 2.** Add the dependency.
Check latest version on the [releases page](https://github.com/oleksandrbalan/pagecurl/releases).
```kotlin
dependencies {
    implementation("io.github.oleksandrbalan:pagecurl:$version")
}
```

### Use in Composable

The `PageCurl` has 2 mandatory arguments:
* **count** - The count of pages.
* **content** - The content lambda to provide the page composable. Receives the page number.

```
val pages = listOf("One", "Two", "Three")
PageCurl(count = pages.size) { index ->
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
    ) {
        Text(
            text = pages[index],
            style = MaterialTheme.typography.h1,
        )
    }
}
```

Optionally `state` could be provided to observe and manage PageCurl state.
* **state** - The state of the PageCurl. Use it to programmatically change the current page or observe changes, and to configure shadow, back-page and interactions.
```
Column {
    val scope = rememberCoroutineScope()
    val state = rememberPageCurlState()
    Button(onClick = { scope.launch { state.next() } }) {
        Text(text = "Next")
    }

    val pages = listOf("One", "Two", "Three")
    PageCurl(
        count = pages.size,
        state = state,
    ) { index ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxSize()
        ) {
            Text(
                text = pages[index],
                style = MaterialTheme.typography.h1,
            )
        }
    }
}
```

Optionally `key` lambda could be provided with stable key for each item. PageCurl with keys for each page will correctly preserve a current position when items are added or removed.
* **key** - The lambda to provide stable key for each item. Useful when adding and removing items before current page.
```
Column {
    var pages by remember { mutableStateOf(listOf("Four", "Five", "Six")) }
    Button(onClick = { pages = listOf("One", "Two", "Three") + pages }) {
        Text(text = "Prepend new pages")
    }

    PageCurl(
        count = pages.size,
        key = { pages[it].hashCode() },
    ) { index ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxSize()
        ) {
            Text(
                text = pages[index],
                style = MaterialTheme.typography.h1,
            )
        }
    }
}
```

See Demo application and [examples](demo/src/main/kotlin/eu/wewox/pagecurl/screens) for more usage examples.

https://user-images.githubusercontent.com/20944869/185782671-2861c2ed-c033-4318-bf12-1d8db74fc8b5.mp4

https://user-images.githubusercontent.com/20944869/185782668-b52da2b9-be8d-49db-8729-88b6f9a8ee48.mp4

https://user-images.githubusercontent.com/20944869/185782663-4bd97a57-1a46-408d-a07b-34c193f01aba.mp4
