package com.ngatour.fruitclassifier.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngatour.fruitclassifier.ui.theme.Poppins

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = Poppins
    )
}

@Composable
fun ProfileItem(icon: ImageVector, label: String, labelColor: Color = Color.Black) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontFamily = Poppins, color = labelColor)
    }
}

@Composable
fun ClickableRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, fontFamily = Poppins, fontSize = 16.sp)
            subtitle?.let {
                Text(it, fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
            }
        }
        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFFFF6F00))
    }
}

@Composable
fun AboutCard(title: String, content: List<String>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2)),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                fontFamily = Poppins,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            content.forEach {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun LabeledRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = Color.Black
        )
        Text(
            text = value,
            fontFamily = Poppins,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

