package com.example.alphakids.ui.screens.tutor.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.BottomNavItem
import com.example.alphakids.ui.components.CustomFAB
import com.example.alphakids.ui.components.MainBottomBar

@Composable
fun StudentAccessoriesStoreScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBottomNavClick: (String) -> Unit,
    currentRoute: String = "store",
    studentId: String = "default",
    coins: Int = 123,
    @androidx.annotation.DrawableRes croquetasImageResId: Int = android.R.drawable.ic_menu_help,
    @androidx.annotation.DrawableRes huesoImageResId: Int = android.R.drawable.ic_menu_help,
    @androidx.annotation.DrawableRes pescadoImageResId: Int = android.R.drawable.ic_menu_help
) {
    val items = listOf(
        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
        BottomNavItem("store", "Tienda", Icons.Rounded.Store),
        BottomNavItem("pets", "Mascotas", Icons.Rounded.Pets)
    )

    val studentViewModel: com.example.alphakids.ui.student.StudentViewModel = hiltViewModel()
    val students by studentViewModel.students.collectAsState()
    val studentName = students.firstOrNull { it.id == studentId }?.nombre ?: "Estudiante"

    var selectedFilter by remember { mutableStateOf("Todo") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogItemName by remember { mutableStateOf("") }
    var dialogItemResId by remember { mutableStateOf(croquetasImageResId) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Tienda de Accesorios",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actionIcon = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
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
                contentDescription = null,
                onClick = onSettingsClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¡Hola, $studentName!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Tienes muchas monedas",
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
                        icon = Icons.Rounded.Store,
                        contentDescription = null,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Tienda de Accesorios",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "¡Compra accesorios para tu mascotita!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Comida",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                com.example.alphakids.ui.components.InfoChip(
                    text = "Todo",
                    isSelected = selectedFilter == "Todo",
                    onClick = { selectedFilter = "Todo" }
                )
                com.example.alphakids.ui.components.InfoChip(
                    text = "Perro",
                    isSelected = selectedFilter == "Perro",
                    onClick = { selectedFilter = "Perro" }
                )
                com.example.alphakids.ui.components.InfoChip(
                    text = "Gato",
                    isSelected = selectedFilter == "Gato",
                    onClick = { selectedFilter = "Gato" }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val cards = when (selectedFilter) {
                "Perro" -> listOf(
                    Triple("Hueso", 25, huesoImageResId),
                    Triple("Croquetas", 30, croquetasImageResId)
                )
                "Gato" -> listOf(
                    Triple("Pescado", 25, pescadoImageResId),
                    Triple("Croquetas", 30, croquetasImageResId)
                )
                else -> listOf(
                    Triple("Croquetas", 30, croquetasImageResId),
                    Triple("Hueso", 25, huesoImageResId),
                    Triple("Pescado", 25, pescadoImageResId)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                cards.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { (title, price, resId) ->
                            com.example.alphakids.ui.components.StoreItemCard(
                                title = title,
                                price = price,
                                itemImage = painterResource(id = resId),
                                onClickBuy = {
                                    dialogItemName = title
                                    dialogItemResId = resId
                                    showDialog = true
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Diálogo de confirmación
            if (showDialog) {
                com.example.alphakids.ui.components.PurchaseConfirmDialog(
                    image = painterResource(id = dialogItemResId),
                    message = "¿Quieres comprar \"$dialogItemName\"?",
                    onAccept = {
                        showDialog = false
                        showSuccessDialog = true
                    },
                    onCancel = { showDialog = false }
                )
            }

            // Aviso de compra exitosa
            if (showSuccessDialog) {
                com.example.alphakids.ui.components.ActionDialog(
                    icon = Icons.Rounded.CheckCircle,
                    message = "Accesorio comprado",
                    primaryButtonText = "Aceptar",
                    onPrimaryButtonClick = { showSuccessDialog = false },
                    onDismissRequest = { showSuccessDialog = false },
                    isError = false
                )
            }
        }
    }
}
