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
