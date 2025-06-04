package com.ngatour.fruitclassifier.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ngatour.fruitclassifier.ui.components.AboutCard
import com.ngatour.fruitclassifier.ui.components.LabeledRow
import com.ngatour.fruitclassifier.ui.theme.Poppins

@Composable
fun AboutDeveloperScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3E0))
            .padding(20.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFFF6F00)
                )
            }
            Text(
                text = "About Developer",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About Researcher
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2)),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "About Researcher",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    fontFamily = Poppins
                )
                Spacer(modifier = Modifier.height(8.dp))

                LabeledRow("Name", "Ngatour Rohman")
                LabeledRow("NIM", "202143501481")
                LabeledRow("Program", "Informatics Engineering")
                LabeledRow("University", "Indraprasta PGRI University")
                LabeledRow("Year", "2025")

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Title:\nTropical Fruit Classification with CNN on Mobile Platform.",
                    fontFamily = Poppins,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        AboutCard(
            title = "Description",
            content = listOf(
                "This application was developed as part of the student's final project in implementing CNN-based deep learning technology on Android devices."
            )
        )
    }
}
