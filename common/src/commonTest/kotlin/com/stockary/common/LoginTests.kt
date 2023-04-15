package com.stockary.common

import com.copperleaf.ballast.test.viewModelTest
import com.stockary.common.di.appModule
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.ui.login.LoginContract
import com.stockary.common.ui.login.LoginEventHandler
import com.stockary.common.ui.login.LoginInputHandler
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.time.ExperimentalTime


@RunWith(JUnit4::class)
class LoginTests: KoinTest {

    val injector by inject<ComposeDesktopInjector>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(appModule())
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testExampleViewModel() = runBlocking<Unit> {
        viewModelTest(
            inputHandler = LoginInputHandler(),
            eventHandler = LoginEventHandler(),
            filter = null,
        ) {
            defaultInitialState { LoginContract.State() }

            scenario("update string value only") {
                running {
                    +LoginContract.Inputs.UpdateStringValue("one")
                }
                resultsIn {
                    assertEquals("one", latestState.stringValue)
                    assertEquals(0, latestState.intValue)
                }
            }

            scenario("increment int value only") {
                running {
                    +Inputs.Increment
                    +Inputs.Increment
                }
                resultsIn {
                    assertEquals(2, latestState.intValue)
                }
            }
        }
    }
}