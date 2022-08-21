[![](https://jitpack.io/v/oleksandrbalan/pagecurl.svg)](https://jitpack.io/#oleksandrbalan/pagecurl)

# Page Curl

Page Curl library for Jetpack Compose.

## Motivation

This library allows to create an effect of turning pages, which can be used in book reader applications, custom on-boarding screens or elsewhere.

## Usage

### Get a dependency

**Step 1.** Add the JitPack repository to your build file.
Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency.
Check latest version on the [releases page](https://github.com/oleksandrbalan/pagecurl/releases).
```
dependencies {
    implementation 'com.github.oleksandrbalan:pagecurl:$version'
}
```

### Use in Composable

The `PageCurl` has 2 mandatory arguments:
* **state** - The state of the PageCurl. Use it to programmatically change the current page or observe changes, and to configure shadow, back-page and interactions.
* **content** -  The content lambda to provide the page composable. Receives the page number.

```
val pages = listOf("One", "Two", "Three")
val state = rememberPageCurlState(max = pages.size)
PageCurl(state = state) { index ->
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

See Demo application and [examples](demo/src/main/kotlin/eu/wewox/pagecurl/screens) for more usage examples.

https://user-images.githubusercontent.com/20944869/185782671-2861c2ed-c033-4318-bf12-1d8db74fc8b5.mp4

https://user-images.githubusercontent.com/20944869/185782668-b52da2b9-be8d-49db-8729-88b6f9a8ee48.mp4

https://user-images.githubusercontent.com/20944869/185782663-4bd97a57-1a46-408d-a07b-34c193f01aba.mp4
