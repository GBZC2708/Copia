package com.example.alphakids.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun PetNameInputDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (name: String) -> Unit,
    petName: String,
    onNameChange: (String) -> Unit
    // Asumimos que la imagen del perro se carga aquí. Usamos Painter como placeholder.
    // Necesitas pasar el ID real del drawable o el Painter.
    // petImage: Painter
) {
    // Simulando el Painter para el preview ya que no tengo acceso al recurso
    val petImage = painterResource(id = android.R.drawable.ic_menu_gallery) // Reemplazar con el recurso real

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 15.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                // 1. Imagen de Mascota
                Image(
                    painter = petImage,
                    contentDescription = "Mascota comprada",
                    modifier = Modifier.size(126.dp, 83.dp) // Tamaño 126x83
                )

                // 2. Título de Felicitaciones
                Text(
                    text = "⭐ ¡Felicidades! ⭐",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp, // DM Sans Bold 24
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                // 3. Mensaje Secundario
                Text(
                    text = "Ahora tienes un perro como mascota",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp, // DM Sans Regular 12
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp)) // Espaciador adicional

                // 4. Instrucción de Nombre
                Text(
                    text = "Elige un nombre para tu mascota",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp, // DM Sans Bold 14
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                // 5. Campo de Texto (LabeledTextField)
                // Nota: Usando el BaseTextField directo para replicar el diseño simple de la imagen
                BaseTextField( // Asumiendo que BaseTextField existe y maneja el diseño de 28dp
                    value = petName,
                    onValueChange = onNameChange,
                    placeholderText = "Escribe aquí...",
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(5.dp))
                // 6. Botón Confirmar (PrimaryButton)
                PrimaryButton(
                    text = "Confirmar",
                    onClick = { onConfirm(petName) },
                    modifier = Modifier
                        .width(98.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
fun PetNameInputDialogPreview() {
    var name by remember { mutableStateOf("") }
    AlphakidsTheme {
        Column(Modifier.fillMaxSize().padding(32.dp)) {
            PetNameInputDialog(
                onDismissRequest = {},
                onConfirm = { name = it },
                petName = name,
                onNameChange = { name = it }
            )
        }
    }
}
