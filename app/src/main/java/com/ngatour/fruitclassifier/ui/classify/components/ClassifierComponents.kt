package com.ngatour.fruitclassifier.ui.classify.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngatour.fruitclassifier.R
import com.ngatour.fruitclassifier.ui.theme.Poppins

@Composable
fun ResultItem(title: String, value: String, icon: ImageVector, valueColor: Color = Color.Black) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFFFE0B2), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF6D4C41), modifier = Modifier.size(20.dp))
        }
        Column {
            Text(title, fontFamily = Poppins, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
            Text(value, fontFamily = Poppins, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
fun SupportedFruitIcons() {
    val icons = listOf(
        R.drawable.ic_banana, R.drawable.ic_durian, R.drawable.ic_guava, R.drawable.ic_mango, R.drawable.ic_mangosteen,
        R.drawable.ic_orange, R.drawable.ic_papaya, R.drawable.ic_rambutan, R.drawable.ic_pineapple, R.drawable.ic_salak
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icons.take(5).forEach {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icons.takeLast(5).forEach {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewResultItem() {
    ResultItem(
        title = "Fruit Label",
        value = "Mango",
        icon = Icons.Default.Label
    )
}
