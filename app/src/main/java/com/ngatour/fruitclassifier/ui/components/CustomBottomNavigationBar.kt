package com.ngatour.fruitclassifier.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngatour.fruitclassifier.R
import com.ngatour.fruitclassifier.ui.theme.Poppins

@Composable
fun CustomBottomNavigationBar(active: String, onItemClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(28.dp)
            )
            .height(110.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val items = listOf(
                    BottomNavItem("home", R.drawable.ic_home, R.drawable.ic_home_filled, "Home"),
                    BottomNavItem("history", R.drawable.ic_history, R.drawable.ic_history_filled, "History"),
                    BottomNavItem("scan", R.drawable.ic_scan, label = "", isCenter = true),
                    BottomNavItem("results", R.drawable.ic_results, R.drawable.ic_results_filled, "Results"),
                    BottomNavItem("profile", R.drawable.ic_profile, R.drawable.ic_profile_filled, "Profile"),
                )


                items.forEach { item ->
                    if (item.isCenter) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .offset(y = (-15).dp)
                                .background(
                                    color = Color(0xFFFF6F00),
                                    shape = RoundedCornerShape(32.dp)
                                )
                                .clickable { onItemClick(item.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = item.iconInactive),
                                contentDescription = "Scan",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onItemClick(item.id) }
                        ) {
                            Image(
                                painter = painterResource(id = if (active == item.id) item.iconActive else item.iconInactive),
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.label,
                                fontFamily = Poppins,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (active == item.id) Color(0xFFFF6F00) else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}


data class BottomNavItem(
    val id: String,
    val iconInactive: Int,
    val iconActive: Int = iconInactive,
    val label: String,
    val isCenter: Boolean = false
)


@Preview(showBackground = true)
@Composable
fun PreviewCustomBottomNavigationBar() {
    CustomBottomNavigationBar(active = "home") {}
}
