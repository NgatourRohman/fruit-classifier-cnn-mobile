# Fruit Classifier CNN Mobile 🍌🥭🍍

Aplikasi Android untuk klasifikasi buah tropis menggunakan model CNN (MobileNetV2) yang dilatih dengan PyTorch dan diintegrasikan dengan PyTorch Mobile (TorchScript).

## 📌 Fitur
- Klasifikasi otomatis gambar buah dari kamera/galeri
- Model ringan dan cepat (MobileNetV2 + TorchScript)
- Dataset: Fruit 262 + Fruit 360 (gabungan)

## 🧠 Model
- Arsitektur: MobileNetV2 (transfer learning)
- Framework: PyTorch
- Konversi: TorchScript `.pt`

## 📁 Struktur Folder
- `training/`: Kode pelatihan model dan konversi TorchScript
- `models/`: Model hasil training (`.pth`) dan hasil konversi (`.pt`)
- `android-app/`: Project Android Studio dengan PyTorch Mobile

## 🚀 Cara Menjalankan
### Training
```bash
cd training
pip install -r ../requirements.txt
python train.py
