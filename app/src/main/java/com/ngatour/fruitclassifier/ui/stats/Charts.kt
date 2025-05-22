package com.ngatour.fruitclassifier.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun PieChart(data: Map<String, Int>) {
    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan,
        Color.Magenta, Color.LightGray
    )
    val total = data.values.sum().toFloat()

    Canvas(modifier = Modifier.size(200.dp)) {
        var startAngle = 0f
        data.entries.forEachIndexed { index, entry ->
            val sweep = (entry.value / total) * 360f
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true
            )
            startAngle += sweep
        }
    }
}

@Composable
fun BarChart(data: Map<String, Float>) {
    Column {
        data.forEach { (label, confidence) ->
            Text("$label: ${"%.2f".format(confidence)}%")
            Box(
                modifier = Modifier
                    .fillMaxWidth(confidence / 100f)
                    .height(20.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
