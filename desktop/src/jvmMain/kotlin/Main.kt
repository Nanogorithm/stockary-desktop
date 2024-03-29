// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.ResourceLoader
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.helloanwar.common.ui.theme.AppTheme
import com.stockary.common.ui.app.AppScreenView
import com.stockary.common.di.appModule
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.di.injector.ComposeDesktopInjectorImpl
import org.koin.core.context.GlobalContext.startKoin


@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    startKoin {
        modules(appModule())
    }

    val serviceAccount = ResourceLoader.Default.load("admin-sdk.json")
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setStorageBucket("stockary-33aef.appspot.com")
        .build()
    FirebaseApp.initializeApp(options)

    val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

    val applicationScope = rememberCoroutineScope()
    val injector: ComposeDesktopInjector = remember(applicationScope) { ComposeDesktopInjectorImpl(applicationScope) }

    Window(onCloseRequest = { exitApplication() }, title = "Stockary", state = windowState) {
        AppTheme {
            AppScreenView(injector).App()
        }
    }
}
