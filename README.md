# GOSONG
"𝑨𝒉 𝒔𝒖𝒂𝒕𝒖 𝒘𝒆𝒆𝒌𝒆𝒏𝒅 𝒚𝒂𝒏𝒈 𝒕𝒆𝒏𝒂𝒏𝒈, 𝒔𝒂𝒂𝒕𝒏𝒚𝒂 𝒕𝒊𝒅𝒖𝒓 𝒔𝒊𝒂𝒏𝒈"

<div align="center">
<img width="800" alt="GOSONG Game Banner" src="https://github.com/user-attachments/assets/1dbb6c91-235c-4e5f-bbb5-0af76675d519" />

![Java](https://img.shields.io/badge/Java-24-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Java Swing](https://img.shields.io/badge/Java%20Swing-GUI-5382a1?style=for-the-badge&logo=java&logoColor=white)
![Java AWT](https://img.shields.io/badge/Java%20AWT-Graphics-007396?style=for-the-badge&logo=java&logoColor=white)

</div>

---

## Development Team

<div align="center">

<table>
<tr>
    <td align="center" width="150">
        <a href="https://github.com/tyawaa">
            <img src="https://github.com/tyawaa.png" width="80px;" alt="Tyawaa"/>
            <br />
            <sub><b>Tyawaa</b></sub>
        </a>
    </td>
    <td align="center" width="150">
        <a href="https://github.com/ibr4him-ctrl">
            <img src="https://github.com/ibr4him-ctrl.png" width="80px;" alt="Ibrahim"/>
            <br />
            <sub><b>Ibrahim</b></sub>
        </a>
    </td>
    <td align="center" width="150">
        <a href="https://github.com/aldinanti">
            <img src="https://github.com/aldinanti.png" width="80px;" alt="Aldinanti"/>
            <br />
            <sub><b>Aldinanti</b></sub>
        </a>
    </td>
      <td align="center" width="150">
        <a href="https://github.com/larashtm">
            <img src="https://github.com/larashtm.png" width="80px;" alt="Larashtm"/>
            <br />
            <sub><b>Larashtm</b></sub>
        </a>
    </td>
    <td align="center" width="150">
        <a href="https://github.com/firafasya">
            <img src="https://github.com/firafasya.png" width="80px;" alt="Firafasya"/>
            <br />
            <sub><b>Firafasya</b></sub>
        </a>
    </td>
</tr>
</table>

</div>

## Overview

**GOSONG** adalah game simulasi manajemen dapur berbasis desktop yang dikembangkan dengan Java dan Swing, mengusung konsep local co-op di mana dua pemain bekerja sama mengelola dapur pizza dalam batas waktu tertentu. Aplikasi ini menerapkan prinsip OOP dan berbagai design pattern seperti MVC, Singleton, dan Strategy dengan arsitektur modular yang memisahkan model, view, dan controller. GOSONG dilengkapi game loop real-time 60 FPS, sistem manajemen state, rendering sprite dengan animasi 8 arah, serta sistem audio untuk musik latar dan efek suara.

---

## Gameplay Mechanics

### Station System

### Pizza Recipes

| Pizza Type | Ingredients | Cooking Time |
|------------|-------------|--------------|
| **Margherita** | Dough (chopped) + Tomato (chopped) + Cheese (chopped) | 12 seconds |
| **Sosis** | Margherita + Sausage (chopped) | 12 seconds |
| **Ayam** | Margherita + Chicken (chopped) | 12 seconds |

### Controls

| Player | Movement | Actions |
|--------|----------|---------|
| **Both Players** | W, A, S, D | Movement (8 directions) |
| **Active Player** | TAB | Switch active player |
| | C | Pick Up / Drop / Serve / Trash |
| | V | Use Station (Cut, Wash, etc.) |
| | E | Throw ingredient (2-4 tiles) |
| | Shift + WASD | Dash (3 tiles, 3s cooldown) |

---
## Setup Instructions

1. Clone Repository dari GitHub
Buka terminal atau Command Prompt, lalu jalankan perintah berikut:
```bash
git clone https://github.com/ibr4him-ctrl/GOSONG.git
```
2. Masuk ke Folder Repository
Setelah proses clone selesai, masuk ke folder project dengan perintah:
```bash
cd GOSONG
```
3. Buka Project Menggunakan Visual Studio Code
- Jalankan Visual Studio Code
- Pilih menu **File > Open Folder**
- Pilih folder repository `GOSONG` yang telah di-clone
- Tunggu hingga VS Code selesai memuat project

4. Pastikan Java Development Kit (JDK) Terinstall
Project ini menggunakan **JDK 24**. Pastikan JDK sudah terinstall di sistem Anda:
```bash
java --version
```
Jika belum terinstall, download dari [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) atau [OpenJDK](https://openjdk.org/).

5. Install Extension Java di VS Code (Opsional)
Untuk pengalaman development yang lebih baik, install extension:
- **Extension Pack for Java** (dari Microsoft)
- **Debugger for Java**

6. Buka Terminal di VS Code
Gunakan shortcut **Ctrl + `** (backtick) atau pilih menu **Terminal > New Terminal**.

7. Jalankan Program Java
