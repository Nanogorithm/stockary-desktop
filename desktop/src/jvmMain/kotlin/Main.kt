// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.helloanwar.common.ui.theme.AppTheme
import com.stockary.common.App
import com.stockary.common.di.appModule
import org.koin.core.context.GlobalContext.startKoin


fun main() = application {
    startKoin {
        modules(appModule())
    }
    Window(onCloseRequest = ::exitApplication, title = "Stockary") {
        AppTheme {
            App()
        }
    }
}
