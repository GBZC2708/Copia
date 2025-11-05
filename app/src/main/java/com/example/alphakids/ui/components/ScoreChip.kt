package com.example.alphakids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun ScoreChip(
    modifier: Modifier = Modifier,
    score: Int,
    icon: ImageVector = Icons.Rounded.Savings,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    val shape = RoundedCornerShape(28.dp)
    val fixedHeight = 44.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(fixedHeight)
            .clip(shape)
            .background(backgroundColor, shape)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Puntuación",
            modifier = Modifier.size(24.dp),
            tint = iconColor
        )

        Text(
            text = score.toString(),
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            color = iconColor
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ScoreChipPreview() {
    AlphakidsTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ScoreChip(
                modifier = Modifier.fillMaxWidth(0.5f),
                score = 99
            )
            ScoreChip(
                modifier = Modifier.fillMaxWidth(0.4f),
                score = 4
            )
        }
    }
}
