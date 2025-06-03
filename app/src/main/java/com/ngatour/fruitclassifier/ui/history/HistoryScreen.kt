package com.ngatour.fruitclassifier.ui.history

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel
import com.ngatour.fruitclassifier.ui.components.SupabaseImage
import com.ngatour.fruitclassifier.ui.theme.Poppins

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val history by viewModel.history.collectAsState()
    val context = LocalContext.current
    var expandedItemId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.syncFromSupabase(context)
        viewModel.loadHistory()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF3E0)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "Classification History",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history) { item ->
                    val isExpanded = item.id == expandedItemId

                    val imageSize by animateDpAsState(
                        targetValue = if (isExpanded) 100.dp else 56.dp,
                        label = "ImageSizeAnim"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedItemId = if (isExpanded) null else item.id
                            },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(imageSize)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    SupabaseImage(
                                        imageUrl = item.imageUrl,
                                        contentDescription = item.label,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Classified: ${item.timestamp}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = Poppins,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = item.label,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = Poppins,
                                        color = Color(0xFFFF6F00)
                                    )

                                    if (isExpanded) {
                                        Text(
                                            text = "Confidence: ${"%.2f".format(item.confidence)}%",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Normal,
                                            fontFamily = Poppins,
                                            color = Color(0xFFFFA000)
                                        )
                                        Text(
                                            text = "Time Taken: ${item.processTimeMs} ms",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Normal,
                                            fontFamily = Poppins,
                                            color = Color(0xFF0277BD)
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { viewModel.deleteById(item) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
