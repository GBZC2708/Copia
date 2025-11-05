package com.example.alphakids.ui.screens.tutor.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.CustomFAB
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import com.example.alphakids.ui.components.BottomNavItem
import com.example.alphakids.ui.components.MainBottomBar
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource

@Composable
fun StudentPetsStoreScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBottomNavClick: (String) -> Unit,
    currentRoute: String = "store",
    studentId: String,
    coins: Int = 123,
    @androidx.annotation.DrawableRes dogImageResId: Int,
    @androidx.annotation.DrawableRes catImageResId: Int
) {
    val items = listOf(
        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
        BottomNavItem("store", "Tienda", Icons.Rounded.Store),
        BottomNavItem("pets", "Mascotas", Icons.Rounded.Pets)
    )

    // Obtener el nombre del estudiante guiándonos de ProfileSelectionScreen (StudentViewModel)
    val studentViewModel: com.example.alphakids.ui.student.StudentViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val students by studentViewModel.students.collectAsState()
    val studentName = students.firstOrNull { it.id == studentId }?.nombre ?: "Estudiante"

    // Estado del diálogo de compra
    var showDialog by remember { mutableStateOf(false) }
    var dialogItemName by remember { mutableStateOf("") }
    var dialogItemResId by remember { mutableStateOf(dogImageResId) }

    // Estado del diálogo para elegir nombre de mascota
    var showNameDialog by remember { mutableStateOf(false) }
    var petName by remember { mutableStateOf("") }

    // Aviso de compra exitosa
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Tienda de Mascotas",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actionIcon = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        bottomBar = {
            MainBottomBar(
                items = items,
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado de saludo + ScoreChip de monedas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¡Hola, $studentName!",
                        fontFamily = com.example.alphakids.ui.theme.dmSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Tienes muchas monedas",
                        fontFamily = com.example.alphakids.ui.theme.dmSansFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                com.example.alphakids.ui.components.ScoreChip(
                    modifier = Modifier.width(120.dp),
                    score = coins
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero: Icono grande y textos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    com.example.alphakids.ui.components.IconContainer(
                        icon = Icons.Rounded.Pets,
                        contentDescription = "Mascotas",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    )

                    Column {
                        Text(
                            text = "Tienda de Mascotas",
                            fontFamily = com.example.alphakids.ui.theme.dmSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "¡Compra los animalitos que más te gusten!",
                            fontFamily = com.example.alphakids.ui.theme.dmSansFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección de Mascotas
            Text(
                text = "Mascotas",
                fontFamily = com.example.alphakids.ui.theme.dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Solo dos items: Perro y Gato
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                com.example.alphakids.ui.components.StoreItemCard(
                    title = "Perro",
                    price = 50,
                    itemImage = painterResource(id = dogImageResId),
                    onClickBuy = {
                        dialogItemName = "Perro"
                        dialogItemResId = dogImageResId
                        showDialog = true
                    }
                )

                com.example.alphakids.ui.components.StoreItemCard(
                    title = "Gato",
                    price = 50,
                    itemImage = painterResource(id = catImageResId),
                    onClickBuy = {
                        dialogItemName = "Gato"
                        dialogItemResId = catImageResId
                        showDialog = true
                    }
                )
            }

            if (showDialog) {
                com.example.alphakids.ui.components.PurchaseConfirmDialog(
                    image = painterResource(id = dialogItemResId),
                    message = "¿Quieres comprar un \"$dialogItemName\"?",
                    onAccept = {
                        showDialog = false
                        showNameDialog = true
                    },
                    onCancel = { showDialog = false }
                )
            }

            if (showNameDialog) {
                com.example.alphakids.ui.components.PetNameInputDialog(
                    onDismissRequest = { showNameDialog = false },
                    onConfirm = { name ->
                        petName = name
                        showNameDialog = false
                        showSuccessDialog = true
                        // Aquí podrías persistir el nombre o disparar una acción en tu ViewModel
                    },
                    petName = petName,
                    onNameChange = { petName = it },
                    image = painterResource(id = dialogItemResId),
                    petTypeLabel = dialogItemName
                )
            }

            // Aviso de éxito: Mascota comprada
            if (showSuccessDialog) {
                com.example.alphakids.ui.components.ActionDialog(
                    icon = androidx.compose.material.icons.Icons.Rounded.CheckCircle,
                    message = "Mascota comprada",
                    primaryButtonText = "Aceptar",
                    onPrimaryButtonClick = { showSuccessDialog = false },
                    onDismissRequest = { showSuccessDialog = false },
                    isError = false
                )
            }
        }
    }
}