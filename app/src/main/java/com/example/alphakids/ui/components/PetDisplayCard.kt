package com.example.alphakids.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun PetDisplayCard(
    modifier: Modifier = Modifier,
    petName: String,
    petImage: Painter,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(28.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor, shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 35.dp, vertical = 35.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp) // 5 dp de gap
    ) {
        // 1. Imagen de Mascota (127x84)
        Image(
            painter = petImage,
            contentDescription = petName,
            modifier = Modifier.size(127.dp, 84.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Nombre (DM Sans Bold 24)
        Text(
            text = petName,
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// --- PREVIEW ---

@Composable
private fun SamplePetImage(): Painter {
    return painterResource(id = android.R.drawable.ic_menu_gallery)
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PetDisplayCardPreview() {
    AlphakidsTheme {
        Column(Modifier.padding(16.dp)) {
            PetDisplayCard(
                petName = "Max",
                petImage = SamplePetImage(),
                modifier = Modifier.size(200.dp)
            )
        }
    }
}