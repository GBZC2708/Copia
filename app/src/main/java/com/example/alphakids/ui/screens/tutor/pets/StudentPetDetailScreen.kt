package com.example.alphakids.ui.screens.tutor.pets

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.BottomNavItem
import com.example.alphakids.ui.components.CustomFAB
import com.example.alphakids.ui.components.MainBottomBar
import com.example.alphakids.ui.theme.dmSansFamily
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun StudentPetDetailScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBottomNavClick: (String) -> Unit,
    currentRoute: String = "pets",
    petName: String = "Mi Mascota"
) {
    val bottomItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
        BottomNavItem("store", "Tienda", Icons.Rounded.Store),
        BottomNavItem("pets", "Mascotas", Icons.Rounded.Pets)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = petName,
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actionIcon = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        },
        bottomBar = {
            MainBottomBar(
                items = bottomItems,
                currentRoute = currentRoute,
                onNavigate = onBottomNavClick
            )
        },
        floatingActionButton = {
            CustomFAB(
                icon = Icons.Rounded.Settings,
                contentDescription = "Configuración",
                onClick = onSettingsClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val isDog = petName.equals("Max", ignoreCase = true)
            val petImage = androidx.compose.ui.res.painterResource(
                id = if (isDog) com.example.alphakids.R.drawable.ic_sad_dog
                else com.example.alphakids.R.drawable.ic_happy_cat
            )
            val petTypeLabel = if (isDog) "Tu perro" else "Tu gato"

            com.example.alphakids.ui.components.PetStatusCard(
                petName = petName,
                petType = petTypeLabel,
                petImage = petImage,
                hungerProgress = 0.7f,
                happinessProgress = 0.25f
            )

            Text(
                text = "Alimentar",
                fontFamily = com.example.alphakids.ui.theme.dmSansFamily,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                com.example.alphakids.ui.components.AccessoryCard(
                    modifier = Modifier.weight(1f),
                    title = "Croquetas",
                    effectMessage = "❤️ +25% de felicidad",
                    accessoryImage = androidx.compose.ui.res.painterResource(
                        id = com.example.alphakids.R.drawable.ic_kibble_dog_cat
                    )
                )
                com.example.alphakids.ui.components.AccessoryCard(
                    modifier = Modifier.weight(1f),
                    title = "Hueso",
                    effectMessage = "❤️ +25% de felicidad",
                    accessoryImage = androidx.compose.ui.res.painterResource(
                        id = com.example.alphakids.R.drawable.ic_bone_dog
                    )
                )
            }
        }
    }
}