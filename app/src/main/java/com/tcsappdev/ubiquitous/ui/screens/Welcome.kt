package com.tcsappdev.ubiquitous.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tcsappdev.ubiquitous.ui.navigation.Screen
import com.tcsappdev.ubiquitous.ui.theme.Spacing
import com.tcsappdev.ubiquitous.R
import com.tcsappdev.ubiquitous.ui.theme.White
import com.tcsappdev.ubiquitous.ui.theme.WhiteOverlay

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier, navController: NavController) {

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_welcome),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.large),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(Spacing.huge))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "RunMap",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(Spacing.medium))
                Text(
                    text = "Every run, mapped.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier.padding(bottom = Spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate(Screen.Login.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
                Spacer(modifier = Modifier.height(Spacing.medium))
                OutlinedButton(
                    onClick = { navController.navigate((Screen.Register.route)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = WhiteOverlay,
                        contentColor = White
                    ),
                    border = BorderStroke(1.dp, White)
                ) {
                    Text("Signup")
                }
            }
        }
    }
}