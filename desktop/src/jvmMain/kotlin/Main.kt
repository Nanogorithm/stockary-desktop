// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.helloanwar.common.ui.theme.AppTheme
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.stockary.common.ui.app.AppScreenView
import com.stockary.common.di.appModule
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.di.injector.ComposeDesktopInjectorImpl
import okio.Path.Companion.toOkioPath
import org.koin.core.context.GlobalContext.startKoin
import java.io.File


fun main() = application {
    startKoin {
        modules(appModule())
    }

    val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

    val applicationScope = rememberCoroutineScope()
    val injector: ComposeDesktopInjector = remember(applicationScope) { ComposeDesktopInjectorImpl(applicationScope) }

    Window(onCloseRequest = { exitApplication() }, title = "Stockary", state = windowState) {
        AppTheme {
            CompositionLocalProvider(
                LocalImageLoader provides generateImageLoader(),
            ) {
                AppScreenView(injector).App()
            }
        }
    }
}

private fun generateImageLoader(): ImageLoader {
    return ImageLoader {
        components {
            // add(ImageIODecoder.Factory())
            setupDefaultComponents(imageScope)
        }
        interceptor {
            memoryCacheConfig {
                // Set the max size to 25% of the app's available memory.
                maxSizePercent(0.25)
            }
            diskCacheConfig {
                directory(getCacheDir().resolve("image_cache").toOkioPath())
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }
}

enum class OperatingSystem {
    Android, IOS, Windows, Linux, MacOS, Unknown
}

private val currentOperatingSystem: OperatingSystem
    get() {
        val operSys = System.getProperty("os.name").lowercase()
        return if (operSys.contains("win")) {
            OperatingSystem.Windows
        } else if (operSys.contains("nix") || operSys.contains("nux") ||
            operSys.contains("aix")
        ) {
            OperatingSystem.Linux
        } else if (operSys.contains("mac")) {
            OperatingSystem.MacOS
        } else {
            OperatingSystem.Unknown
        }
    }

private fun getCacheDir() = when (currentOperatingSystem) {
    OperatingSystem.Windows -> File(System.getenv("AppData"), "$ApplicationName/cache")
    OperatingSystem.Linux -> File(System.getProperty("user.home"), ".cache/$ApplicationName")
    OperatingSystem.MacOS -> File(System.getProperty("user.home"), "Library/Caches/$ApplicationName")
    else -> throw IllegalStateException("Unsupported operating system")
}

private val ApplicationName = "stockary"
