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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun AccessoryCard(
    modifier: Modifier = Modifier,
    title: String,
    effectMessage: String,
    accessoryImage: Painter
) {
    val shape = RoundedCornerShape(28.dp)
    val fixedWidth = 172.dp
    val fixedHeight = 171.dp

    Column(
        modifier = modifier
            .width(fixedWidth)
            .height(fixedHeight)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .padding(all = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // 1. Imagen (91x60, centrada)
        Image(
            painter = accessoryImage,
            contentDescription = title,
            modifier = Modifier.size(91.dp, 60.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Nombre del Accesorio (DM Sans Bold 12)
        Text(
            text = title,
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        // 3. Mensaje de Efecto (DM Sans Regular 10)
        Text(
            text = effectMessage,
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun SampleAccessoryImage(): Painter {
    return painterResource(id = android.R.drawable.ic_menu_gallery)
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun AccessoryCardPreview() {
    AlphakidsTheme {
        AccessoryCard(
            title = "Croquetas",
            effectMessage = "❤️ +25% de felicidad",
            accessoryImage = SampleAccessoryImage()
        )
    }
}
