# Board Fun Helper (Android)

Aplikasi pendamping strategi untuk domino offline. Versi awal ini tidak mengubah APK Board Fun, tidak membaca memori game, dan tidak menekan tombol game otomatis.

## Yang sudah ada
- Input kartu yang sedang dipegang, misalnya `6-5, 3-2, 0-0`
- Input angka ujung kiri dan kanan papan
- Mencari kartu legal dan merekomendasikan langkah dasar

## Batasan
- Versi ini mengasumsikan domino double-six (angka 0–6) dan aturan sambung berdasarkan angka ujung.
- Kartu dan ujung papan harus dimasukkan manual.
- Belum ada pembacaan screenshot, overlay di atas game, atau otomatisasi sentuhan.
- Skor rekomendasi adalah heuristik sederhana, bukan jaminan menang. Strategi perlu disesuaikan dengan mode/rule Board Fun yang dipakai.

## Build
1. Buka folder ini lewat Android Studio.
2. Tunggu Gradle sync.
3. Build > Build APK(s).
4. Pasang APK debug yang dihasilkan di HP Android.

Proyek ini dibuat sebagai source starter; belum diklaim sudah diuji build di perangkat.
