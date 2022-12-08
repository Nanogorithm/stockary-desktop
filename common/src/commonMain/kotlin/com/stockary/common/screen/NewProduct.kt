package com.stockary.common.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stockary.common.currencySymbol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProduct() {
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }
    var costPrice by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp)) {
        Text("New Product", fontSize = 32.sp, fontWeight = FontWeight.W600)
        Spacer(modifier = Modifier.height(38.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(34.dp)) {
            Card(
                modifier = Modifier.weight(4f), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F8F8)
                )
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                    Text("Information", fontSize = 24.sp, fontWeight = FontWeight.W600)
                    Spacer(modifier = Modifier.height(36.dp))
                    TextField(
                        value = productName,
                        onValueChange = { productName = it },
                        placeholder = { Text("Product Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            textColor = contentColorFor(Color.White),
                            unfocusedIndicatorColor = Color.Transparent,
                            placeholderColor = Color(0xFF676767)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = productDescription,
                        onValueChange = { productDescription = it },
                        placeholder = { Text("Description") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 80.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            textColor = contentColorFor(Color.White),
                            unfocusedIndicatorColor = Color.Transparent,
                            placeholderColor = Color(0xFF676767)
                        )
                    )
                }
            }
            Card(
                modifier = Modifier.weight(3f), colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F8F8)
                ), shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                    Text("Organize", fontSize = 24.sp, fontWeight = FontWeight.W600)
                    Spacer(modifier = Modifier.height(36.dp))
                    TextField(
                        value = productName,
                        onValueChange = { productName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            textColor = contentColorFor(Color.White),
                            unfocusedIndicatorColor = Color.Transparent,
                            placeholderColor = Color(0xFF676767)
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(value = sellingPrice,
                            onValueChange = { sellingPrice = it },
                            placeholder = { Text("Selling Price") },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White,
                                textColor = contentColorFor(Color.White),
                                unfocusedIndicatorColor = Color.Transparent,
                                placeholderColor = Color(0xFF676767)
                            ),
                            leadingIcon = {
                                Text(
                                    currencySymbol, color = Color(0xFF1F1F1F), fontWeight = FontWeight.W500
                                )
                            },
                            modifier = Modifier.weight(1f),
                            label = {
                                Text("Selling Price")
                            })
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(value = costPrice,
                            onValueChange = { costPrice = it },
                            placeholder = { Text("Cost Price") },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White,
                                textColor = contentColorFor(Color.White),
                                unfocusedIndicatorColor = Color.Transparent,
                                placeholderColor = Color(0xFF676767)
                            ),
                            leadingIcon = {
                                Text(
                                    currencySymbol, color = Color(0xFF1F1F1F), fontWeight = FontWeight.W500
                                )
                            },
                            modifier = Modifier.weight(1f),
                            label = {
                                Text("Cost Price")
                            })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {

                }, shape = RoundedCornerShape(15.dp)
            ) {
                Text("PUBLISH")
            }
            Button(
                onClick = {

                }, shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Save & Continue")
            }
            TextButton(onClick = {

            }) {
                Text("BACK")
            }
        }
    }
}