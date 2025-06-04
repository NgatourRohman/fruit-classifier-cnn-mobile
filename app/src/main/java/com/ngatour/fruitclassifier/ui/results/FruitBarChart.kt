package com.ngatour.fruitclassifier.ui.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngatour.fruitclassifier.ui.theme.Poppins

@Composable
fun FruitBarChart(data: Map<String, Int>) {
    val colorMap = mapOf(
        "Banana" to Color(0xFFFFEB3B),
        "Durian" to Color(0xFF9E9D24),
        "Guava" to Color(0xFFE53935),
        "Mango" to Color(0xFFFF9800),
        "Mangosteen" to Color(0xFF6A1B9A),
        "Orange" to Color(0xFFFFA000),
        "Papaya" to Color(0xFFFF7043),
        "Pineapple" to Color(0xFFFFC107),
        "Rambutan" to Color(0xFFD32F2F),
        "Salak" to Color(0xFF5D4037)
    )

    val sortedLabels = listOf(
        "Banana", "Durian", "Guava", "Mango", "Mangosteen",
        "Orange", "Papaya", "Pineapple", "Rambutan", "Salak"
    )

    val grouped = sortedLabels.chunked(5)

    Column(modifier = Modifier.fillMaxWidth()) {
        grouped.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                rowItems.forEach { label ->
                    val count = data[label] ?: 0
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Box(
                            modifier = Modifier
                                .height((count * 6).dp.coerceAtMost(120.dp))
                                .width(20.dp)
                                .background(colorMap[label] ?: Color.Gray)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontFamily = Poppins,
                            color = Color.Black
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

