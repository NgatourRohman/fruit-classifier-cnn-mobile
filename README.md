# 🍌🥭🍊🍍🌰 Fruit Classifier CNN Mobile

Aplikasi Android untuk klasifikasi buah tropis menggunakan model CNN (MobileNetV2) yang dilatih dengan PyTorch dan diintegrasikan menggunakan TorchScript (PyTorch Mobile). Aplikasi ini ringan, cepat, dan dirancang untuk berjalan optimal di perangkat Android.

---

## 📌 Fitur Utama
- 🔍 Klasifikasi otomatis gambar buah dari kamera atau galeri
- 📸 Ambil gambar dari kamera
- 🖼 Pilih gambar dari galeri
- 📊 Menampilkan hasil klasifikasi lengkap:
  - Label prediksi
  - Confidence (%)
  - Waktu proses (ms)
  - Tanggal dan jam klasifikasi
  - Deskripsi buah
- ⚠️ Threshold confidence: jika prediksi < 75%, maka ditandai sebagai **"Tidak dikenali"**

---

## 🎨 Fitur UI/UX
- 🎬 Splash Screen (Android 12+)
- 🌀 Loading Screen saat klasifikasi berjalan
- 🖼 UI modern menggunakan Jetpack Compose + Material 3
- 👆 Kompatibel dengan gesture dan layout responsif

---

## 🧠 Model
- Arsitektur: MobileNetV2 (Transfer Learning)
- Framework: PyTorch
- Konversi: TorchScript (`.pt`)
- Dataset: Gabungan Fruit 262 + Fruit 360

---

## 📁 Struktur Folder
- `training/` — Kode pelatihan model dan konversi ke TorchScript
- `models/` — Model hasil training (`.pth`) dan hasil konversi (`.pt`)
- `android-app/` — Project Android Studio berbasis Kotlin + Compose

---

## 🚀 Cara Menjalankan Aplikasi

1. **Clone repositori**:
   ```bash
   git clone https://github.com/NgatourRohman/fruit-classifier-cnn-mobile.git
