package com.tcsappdev.ubiquitous.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.tcsappdev.ubiquitous.ui.navigation.Screen
import com.tcsappdev.ubiquitous.ui.theme.Spacing
import com.tcsappdev.ubiquitous.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName ?: "No name"
    val userEmail = currentUser?.email ?: "No email"
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(Spacing.large),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.small))
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.medium))
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = Spacing.large)
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.large, vertical = Spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = "Dark Mode",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = "Dark Theme",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Switch(
                checked = isDarkTheme,
                onCheckedChange = {onThemeToggle()}
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.large, vertical = Spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = {notificationsEnabled = it}
            )
        }

        Spacer(modifier = Modifier.height(Spacing.medium))

        Text(
            text = "Account",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = Spacing.large)
        )
        Spacer(modifier = Modifier.height(Spacing.small))

        OutlinedButton(
            onClick = {showPasswordDialog = true},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.large)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Change Password",
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(Spacing.small))
            Text("Change Password")
        }

        Spacer(modifier = Modifier.height(Spacing.small))

        Button(
            onClick = {
                authViewModel.logout()
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(0) {inclusive = true}
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.large),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.small))
            Text("Logout")
        }
    }

    if(showPasswordDialog){
        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = {showPasswordDialog = false},
            title = {Text("Change Password")},
            text = {
                Column {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = {currentPassword = it},
                        label = {Text("Current Password")},
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(Spacing.small))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {newPassword = it},
                        label = {Text("New Password")},
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(Spacing.small))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {confirmPassword = it},
                        label = {Text("Confirm Password")},
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if(newPassword == confirmPassword){
                            authViewModel.changePassword(currentPassword, newPassword)
                            showPasswordDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick ={showPasswordDialog=false}) {
                    Text("Cancel")
                }
            }
        )
    }
}