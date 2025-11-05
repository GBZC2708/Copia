package com.example.alphakids.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun StatProgressBar(
    modifier: Modifier = Modifier,
    progress: Float,
    statText: String? = null,
    barHeight: Int = 22,
    isWarning: Boolean = false
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    val progressColor = if (isWarning) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(trackColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(progressColor)
        )

        if (statText != null) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = statText,
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun PetStatusCard(
    modifier: Modifier = Modifier,
    petName: String,
    petType: String,
    petImage: Painter,
    hungerProgress: Float,
    happinessProgress: Float
) {
    val shape = RoundedCornerShape(28.dp)
    val containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(containerColor, shape)
            .border(2.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(all = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "¡Cuida a tu mascota!",
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Image(
            painter = petImage,
            contentDescription = petName,
            modifier = Modifier.size(160.dp, 110.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = petName,
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "⭐",
                fontSize = 22.sp
            )
        }

        Text(
            text = petType,
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Hambre",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            StatProgressBar(
                progress = hungerProgress,
                isWarning = hungerProgress < 0.3f,
                barHeight = 22
            )

            Text(
                text = "Felicidad",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            StatProgressBar(
                progress = happinessProgress,
                isWarning = happinessProgress < 0.3f,
                barHeight = 22
            )
        }
    }
}

@Composable
private fun SamplePetImage(): Painter {
    return painterResource(id = android.R.drawable.ic_menu_gallery)
}


@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun PetStatusCardPreview() {
    AlphakidsTheme {
        Column(Modifier.padding(16.dp)) {
            PetStatusCard(
                petName = "Jack",
                petType = "Tu perro",
                petImage = SamplePetImage(),
                hungerProgress = 0.8f,
                happinessProgress = 0.2f
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun StatProgressBarPreview() {
    AlphakidsTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            StatProgressBar(
                progress = 0.25f,
                statText = "25%",
                isWarning = false
            )

            StatProgressBar(
                progress = 0.85f,
                statText = "85%",
                isWarning = true
            )
        }
    }
}
