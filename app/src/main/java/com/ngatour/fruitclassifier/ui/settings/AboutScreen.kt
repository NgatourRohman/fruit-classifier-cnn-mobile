package com.ngatour.fruitclassifier.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Tentang Aplikasi", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Text("Nama Aplikasi: Klasifikasi Buah Tropis")
        Text("Versi: 1.0.0")
        Text("Dibuat oleh: Arthur")
        Text("Tahun: 2025")

        Spacer(modifier = Modifier.height(8.dp))

        Text("Teknologi yang digunakan:")
        Text("• Kotlin + Jetpack Compose")
        Text("• PyTorch Mobile (TorchScript)")
        Text("• Android Studio")
        Text("• CameraX & Room Database")

        Spacer(modifier = Modifier.height(8.dp))

        Text("Aplikasi ini digunakan untuk mengidentifikasi jenis buah tropis dari gambar yang diunggah atau diambil pengguna menggunakan teknologi deep learning berbasis CNN.")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Tentang Peneliti", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Text("👤 Nama: [Nama Lengkap]")
        Text("🎓 NIM: [NIM Kamu]")
        Text("🏫 Prodi: Teknik Informatika")
        Text("📄 Judul: Klasifikasi Buah Tropis dengan CNN pada Platform Mobile")
        Text("🏢 Universitas: Universitas Indraprasta PGRI")
        Text("📅 Tahun: 2025")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Aplikasi ini dikembangkan sebagai bagian dari tugas akhir mahasiswa dalam mengimplementasikan teknologi deep learning berbasis CNN di perangkat Android.")
    }
}
