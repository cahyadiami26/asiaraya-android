# HRIS Asia Raya — Android Mobile App

Aplikasi Android native berbahasa **Kotlin & Jetpack Compose (Material 3)** untuk karyawan **PT Asia Raya Group**. Aplikasi ini terintegrasi penuh dengan **REST API `/api/v1`** (Batch 21) dari sistem HRIS Asia Raya.

---

## 📱 Fitur Utama

1. **Autentikasi & Sesi Perangkat**:
   - Login dengan Email / NIK / Username dan kata sandi.
   - Sesi token Bearer dengan refresh token otomatis.
   - Konfigurasi alamat server dinamis (bawaan: `https://hris.asiaraya.co.id`, dapat diganti via login atau profil).
   - Tombol cepat akun demo untuk pengujian instan.
2. **Beranda**:
   - Sapaan karyawan dan tanggal panjang WIB.
   - Kartu status presensi hari ini (jam masuk/keluar, status badge).
   - Ringkasan 4 KPI: Hadir bulan ini, Terlambat, Sisa cuti, dan Pengajuan aktif.
   - Kotak peringatan persetujuan masuk untuk atasan / HR / Finance.
   - Pintasan cepat ke slip gaji, lembur, klaim biaya, dan pajak.
   - Daftar 3 pengumuman terbaru dengan badge prioritas/kategori.
3. **Presensi Online (Geofence GPS & Selfie)**:
   - Jam digital berjalan real-time (`HH:mm:ss`).
   - Kamera selfie kamera depan dengan verifikasi resolusi 720 px.
   - Deteksi lokasi GPS dengan geofence radius 50 m Jakarta HQ (Pusat).
   - Tombol Presensi Masuk & Keluar dengan perlindungan Idempotency-Key.
   - Catatan presensi opsional dan status feedback langsung.
4. **Riwayat & Rekap Presensi**:
   - Pemilih bulan dengan navigasi chevron.
   - 4 KPI bulanan: Total Hadir, Terlambat, Absen/Alpha, dan Total Jam Kerja.
   - Daftar kartu kehadiran harian lengkap dengan status badge.
5. **Jadwal & Pengajuan Tukar Shift**:
   - Kartu shift hari ini dengan indikator tipe operasional (NORMAL, WFH, FIELD, SHIFT).
   - Strip jadwal 7 hari ke depan dengan penanda hari kerja vs libur.
   - Form pengajuan perubahan jadwal dengan pilihan shift, rentang tanggal, dan alasan.
   - Riwayat pengajuan dengan aksi pembatalan untuk status `PENDING`.
6. **Cuti**:
   - Tab Saldo Cuti (kuota, terpakai, sisa, progress bar indikator).
   - Tab Riwayat Pengajuan Cuti dengan status timeline keputusan.
   - FAB & Form Pengajuan Cuti lengkap dengan lampiran dokumen (PDF/gambar).
7. **Lembur**:
   - Ringkasan total lembur disetujui & estimasi kompensasi lembur.
   - Form pengajuan lembur dengan tanggal kerja, jam mulai & selesai (24 jam), dan alasan.
   - Riwayat lembur dengan status badge dan pembatalan status terbuka.
8. **Klaim / Reimbursement**:
   - Ringkasan total klaim menunggu persetujuan dan nominal disetujui.
   - Form pengajuan klaim dengan kategori berplafon (`max_amount`), tanggal transaksi, nominal Rupiah, keterangan, dan bukti struk.
9. **Slip Gaji**:
   - Daftar slip gaji final per periode (24 periode terakhir).
   - Bottom Sheet rincian komponen gaji (pendapatan, tunjangan, dan potongan BPJS/PPh 21).
   - Tombol unduh slip PDF resmi bertanda tangan.
10. **Bukti Potong Pajak 1721-A1**:
    - Ringkasan PPh 21 tahunan (penghasilan bruto, neto, PKP, pajak terutang dan telah dipotong).
    - Tombol unduh berkas 1721-A1 PDF resmi.
11. **Pengumuman Perusahaan**:
    - Daftar pengumuman dengan penanda 📌 dipin, badge kategori, pencarian, dan penanda belum dibaca.
    - Sheet pembaca isi pengumuman lengkap.
12. **Notifikasi In-App**:
    - Notifikasi bertipe (INFO, WARNING, SUCCESS, DANGER) dengan penanda belum dibaca dan aksi "Tandai Semua Dibaca".
13. **Kotak Masuk Persetujuan (Approval Inbox)**:
    - Khusus akun dengan hak persetujuan (`can_approve == true`).
    - Tab filter: Semua, Cuti, Lembur, Jadwal, Klaim.
    - Rincian pengajuan bawahan dan aksi **Setujui** / **Tolak** dengan dialog catatan keputusan.
14. **Profil & Pengaturan**:
    - Identitas karyawan lengkap (NIK, nama, jabatan, unit, atasan, email).
    - Konfigurasi server API & informasi rilis.
    - Konfirmasi keluar akun (logout) yang aman.

---

## 🎨 Design System

- **Warna Brand**:
  - Navy Primer: `#0B2135`, `#0D2739`, `#08192A`, `#0F2C45`
  - Teal Aksen: `#0D9488`, `#0F766E`, `#14B8A6`, `#E7FBF7`
  - Latar Belakang: `#F4F6FA` (kartu putih `#FFFFFF` dengan border 1 dp `#E5EAF2`)
- **Status Badge**:
  - `PENDING` → Amber (Menunggu)
  - `APPROVED` / `PRESENT` / `PAID` → Teal (Disetujui / Hadir / Lunas)
  - `REJECTED` / `ABSENT` → Rose (Ditolak / Tidak Hadir)
  - `LEAVE` / `WFH` / `SCHEDULED` → Indigo (Cuti / WFH / Terjadwal)
  - `CANCELLED` / `OFF` → Slate (Dibatalkan / Libur)
- **Bentuk & Radius**: Kartu 14 dp, Tombol & Input 9 dp, Badge 6 dp, Sheet & Dialog 20 dp.

---

## 👥 Akun Demo untuk Pengujian

Semua akun demo menggunakan kata sandi: `Password123!`

| Peran | Email |
|---|---|
| **Karyawan** | `dimas.aditya@asiaraya.co.id` |
| **Atasan Langsung** | `budi.santoso@asiaraya.co.id` |
| **HR Team** | `hr@asiaraya.co.id` |
| **Finance & Payroll** | `finance@asiaraya.co.id` |

---

## 🛠️ Stack & Arsitektur

- **Bahasa**: Kotlin 100%
- **UI Toolkit**: Jetpack Compose (Material 3)
- **HTTP Client**: Retrofit 2 + OkHttp 4 + kotlinx.serialization
- **Local Persistence**: AndroidX DataStore Preferences
- **Image Handling**: Coil Compose
- **Target SDK**: `compileSdk 35`, `minSdk 26`, `targetSdk 35`
- **Build System**: Gradle 9.3.1 (Kotlin DSL), AGP 8.8.2, JDK 21
