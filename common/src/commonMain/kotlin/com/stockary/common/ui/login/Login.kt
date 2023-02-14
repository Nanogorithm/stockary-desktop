package com.stockary.common.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    val viewModelScope = rememberCoroutineScope()

    val vm = remember(viewModelScope) {
        LoginViewModel(
            viewModelScope,
            configBuilder = BallastViewModelConfiguration.Builder(),
            eventHandler = LoginEventHandler(router)
        )
    }

    val uiState by vm.observeStates().collectAsState()

    LaunchedEffect(vm) {
        vm.trySend(LoginContract.Inputs.Initialize)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(start = 64.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hello there, Shaad", fontSize = 32.sp, fontWeight = FontWeight.W600)
        Spacer(modifier = Modifier.height(38.dp))
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        TextField(value = username, onValueChange = { username = it }, placeholder = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, placeholder = { Text("Password") })
        uiState.error?.let {
            Text(it)
        }
        Button(onClick = {
            viewModelScope.launch {
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