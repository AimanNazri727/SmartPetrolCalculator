[README.md](https://github.com/user-attachments/files/27899381/README.md)
# ⛽ Smart Petrol Calculator — BUDI MADANI

![Android](https://img.shields.io/badge/Platform-Android-green)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue)
![API](https://img.shields.io/badge/API-24%2B-orange)
![Version](https://img.shields.io/badge/Version-1.0-red)

Aplikasi Android untuk mengira kos petrol di Malaysia dan mengaplikasikan rebat subsidi BUDI MADANI untuk pengguna yang layak. Harga minyak diambil secara langsung daripada **Malaysia Open Data API** (data.gov.my).

---

## 📱 Skrin Utama

| Home | Calculator | About |
|------|------------|-------|
| Logo BUDI MADANI | Kalkulator kos | Maklumat pembangun |
| Syarat kelayakan | Harga semasa | GitHub link |
| Line chart harga | Keputusan pengiraan | Copyright |

---

## ✨ Ciri-ciri Aplikasi

- **📡 Harga Minyak Live** — Harga RON95, RON97 dan Diesel diambil terus dari `api.data.gov.my`
- **📈 Line Chart** — Graf harga minyak 8 minggu terkini
- **🧮 Kalkulator** — Kira kos petrol dengan atau tanpa rebat BUDI MADANI
- **🎁 Rebat BUDI MADANI** — Rebat RM1.99/liter automatik untuk RON95 yang layak
- **🇲🇾 Tema Malaysia** — Warna bendera Malaysia (Merah, Biru, Kuning)

---

## 🧮 Formula Pengiraan

| Langkah | Formula |
|---------|---------|
| Jumlah Kos Petrol | Liter × Harga/Liter |
| Rebat BUDI MADANI | Liter × RM1.99 (RON95 sahaja) |
| Jumlah Penjimatan | Kos Petrol − Rebat BUDI |
| Jumlah Perlu Dibayar | Kos Petrol − Rebat BUDI |

### Contoh Pengiraan
- Jenis Petrol: RON95
- Harga: RM2.05/liter
- Penggunaan: 40 liter
- BUDI MADANI: Ya

> Kos Petrol = 40 × RM2.05 = **RM82.00**
>
> Rebat BUDI = 40 × RM1.99 = **RM79.60**
>
> Jumlah Perlu Dibayar = RM82.00 − RM79.60 = **RM2.40**

---

## 🌐 API

**Endpoint:**
```
GET https://api.data.gov.my/data-catalogue?id=fuelprice&limit=8&sort=-date
```

**Fields:**

| Field | Keterangan |
|-------|-----------|
| `date` | Tarikh harga |
| `ron95` | Harga RON95 (RM/liter) |
| `ron97` | Harga RON97 (RM/liter) |
| `diesel` | Harga Diesel (RM/liter) |

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| Bahasa | Kotlin |
| UI | Material Components 3 |
| Navigation | Jetpack Navigation Component |
| HTTP Client | Retrofit 2 + OkHttp |
| JSON | Gson |
| Async | Kotlin Coroutines |
| Architecture | MVVM (ViewModel + LiveData) |

---

## 🚀 Cara Install

1. Clone repository ini:

```bash
git clone https://github.com/yourusername/SmartPetrolCalculator.git
```

2. Buka dalam **Android Studio**

3. Sync Gradle dependencies

4. Run pada emulator atau telefon Android (Min SDK 24)

---

## ✅ Syarat Kelayakan BUDI MADANI

Semua warganegara Malaysia dengan lesen memandu aktif layak:

**a. Mempunyai lesen memandu aktif:**
- Lesen Memandu Kompeten (CDL) — Kelas A, A1, B, B1, B2, C, D dan DA
- Lesen Memandu Percubaan (PDL) — Kelas A, A1, B, B1, B2, C, D dan DA
- Lesen Belajar Memandu (LDL) — Kelas A, B, B1, B2 dan C

**b. Pemegang Kad Pengenalan warganegara Malaysia**

> ⚠️ Rebat BUDI MADANI hanya untuk **RON95** sahaja

---

## 👨‍💻 Pembangun

| | |
|--|--|
| **Nama** | Muhammad Aiman Bin Mohd Nazri |
| **Student ID** | 2025158881 |
| **Kursus** | ICT602 - Mobile Technology and Development |
| **Institusi** | UITM |

---

## 📄 Lesen

© 2026 Developed by Aiman Nazri. All Rights Reserved.
