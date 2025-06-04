package com.ngatour.fruitclassifier.ui.classify

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ngatour.fruitclassifier.R
import com.ngatour.fruitclassifier.data.model.ClassificationResult
import com.ngatour.fruitclassifier.data.viewmodel.HistoryViewModel
import com.ngatour.fruitclassifier.ui.classify.components.ResultItem
import com.ngatour.fruitclassifier.ui.classify.components.SupportedFruitIcons
import com.ngatour.fruitclassifier.ui.theme.Poppins
import com.ngatour.fruitclassifier.util.classifyBitmap
import com.ngatour.fruitclassifier.util.saveBitmapToCache
import com.ngatour.fruitclassifier.util.uploadImageToSupabaseStorage
import com.ngatour.fruitclassifier.util.uriToBitmap
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FruitClassifierScreen(viewModel: HistoryViewModel) {
    val context = LocalContext.current
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var result by remember { mutableStateOf<ClassificationResult?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        uri?.let {
            val bitmap = uriToBitmap(context, it)
            val res = classifyBitmap(context, bitmap, "model_fruit_mobile.pt")
            res?.let {
                result = it
            } ?: Toast.makeText(context, "Failed to classify image", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val uri = saveBitmapToCache(context, it)
            imageUri = uri
            val res = classifyBitmap(context, it, "model_fruit_mobile.pt")
            res?.let {
                result = it
            } ?: Toast.makeText(context, "Failed to classify image", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3E0))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Text("Fruit Classifier", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        if (result == null) {
            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.fruit_logo),
                contentDescription = "Fruit Logo",
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Let's identify your\ntropical fruit!",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFFFF6F00),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { cameraLauncher.launch(null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(6.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00))
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Take Picture", color = Color.White, fontFamily = Poppins)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(6.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD89A))
            ) {
                Icon(Icons.Default.Photo, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Choose from Gallery", color = Color.Black, fontFamily = Poppins)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Supported Fruits:", fontFamily = Poppins, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            SupportedFruitIcons()

        } else {
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = rememberAsyncImagePainter(model = imageUri),
                contentDescription = "Classified Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Classification Result",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ResultItem("Fruit Label", result!!.label, Icons.Default.Label, valueColor = Color(0xFFFF6F00))
                ResultItem("Confidence Score", "${"%.2f".format(result!!.confidence)}%", Icons.Default.Percent, valueColor = Color(0xFFFF6F00))
                ResultItem("Time Taken", "${result!!.processTimeMs} ms", Icons.Default.Timer, valueColor = Color(0xFFFF6F00))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                result!!.description,
                fontFamily = Poppins,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (result != null && imageUri != null) {
                            val imageUrl = uploadImageToSupabaseStorage(context, imageUri!!)
                            if (imageUrl != null) {
                                viewModel.saveToHistory(result!!, imageUrl)
                                viewModel.uploadToSupabaseSingle(result!!, imageUrl, context)
                                Toast.makeText(context, "Result saved & uploaded", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Result or image not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(6.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00))
            ) {
                Text("Save Result", color = Color.White, fontFamily = Poppins, fontWeight = FontWeight.Bold)
            }


            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    imageUri = null
                    result = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(6.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD89A))
            ) {
                Text("Try Another Image", color = Color.Black, fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}