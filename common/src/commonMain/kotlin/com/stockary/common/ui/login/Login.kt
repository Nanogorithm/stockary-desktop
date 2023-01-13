package com.stockary.common.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen
import kotlinx.coroutines.launch

@Composable
fun Login(
    router: Router<AppScreen>
) {

    val loginScope = rememberCoroutineScope()

    val vm = remember(loginScope) {
        LoginViewModel(
            loginScope,
            configBuilder = BallastViewModelConfiguration.Builder(),
            eventHandler = LoginEventHandler(router)
        )
    }

    val vmState by vm.observeStates().collectAsState()

    LaunchedEffect(vm) {
        vm.trySend(LoginContract.Inputs.Initialize)
    }

    Column(modifier = Modifier.fillMaxSize().padding(start = 64.dp)) {
        Text("Hello there, Shaad", fontSize = 32.sp, fontWeight = FontWeight.W600)
        Spacer(modifier = Modifier.height(38.dp))
        var username by remember { mutableStateOf("anwar@programming-hero.com") }
        var password by remember { mutableStateOf("34722645") }

        TextField(value = username, onValueChange = { username = it }, placeholder = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, placeholder = { Text("Password") })
        vmState.error?.let {
            Text(it)
        }
        Button(onClick = {
            loginScope.launch {
                vm.trySend(
                    LoginContract.Inputs.LoginByEmail(
                        email = username, password = password
                    )
                )
            }
        }) {
            Text("Login")
        }
    }
}