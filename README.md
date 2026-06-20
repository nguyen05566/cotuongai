# ♟️ CoTuongAI - Chinese Chess AI for Android

<div align="center">
<img src="https://img.shields.io/badge/platform-Android-brightgreen" alt="Platform">
<img src="https://img.shields.io/badge/engine-Pikafish-blue" alt="Engine">
<img src="https://img.shields.io/badge/AI-ONNX-orange" alt="ONNX">
<img src="https://img.shields.io/badge/language-Java-b07219" alt="Java">
<br>
<strong>AI-powered Chinese Chess (Xiangqi) with neural network board recognition</strong>
</div>

---

## 📖 About

CoTuongAI is a Chinese Chess (Xiangqi / Cờ Tướng) application for Android that uses the **Pikafish engine** — a powerful Stockfish fork adapted for Xiangqi — combined with **ONNX neural network models** for automatic board recognition.

### ✨ Features

- 🧠 **Pikafish NNUE Engine** — State-of-the-art AI opponent
- 👁️ **ONNX Board Recognition** — Automatically detects pieces on a physical board via camera
- 🎯 **Auto-Click** — AI automatically plays on supported chess apps
- 📱 **Android Native** — Pure Java/Kotlin, optimized for mobile
- ♟️ **Full Xiangqi Rules** — All standard Chinese Chess rules supported

---

## 🚀 Installation

### Download APK (Ready-to-Use)
Go to [Releases](https://github.com/nguyen05566/cotuongai/releases) and download the latest APK.

### Build from Source
```bash
# Requirements: Java 17+, Android SDK 34+
git clone https://github.com/nguyen05566/cotuongai.git
cd cotuongai
./gradlew assembleDebug
```

### Required Files
- **Pikafish engine binary** — place in `assets/engine/`
- **ONNX models** — `piece_detector.onnx` and `piece_classifier.onnx` in `assets/models/`

---

## 📂 Project Structure

```
cotuong_src/
├── com/zaro/cotuong/
│   ├── MainActivity.java    — Main UI & game controller
│   ├── Board.java           — Board state & Xiangqi logic  
│   ├── BoardView.java       — Custom board rendering
│   ├── PikafishEngine.java  — Pikafish engine integration
│   └── MoveValidator.java   — Move validation & rules
```

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| **AI Engine** | Pikafish (Stockfish for Xiangqi) |
| **Board Recognition** | ONNX Runtime Mobile 1.20 |
| **Neural Network** | NNUE evaluation |
| **UI** | Android Custom Views |
| **Language** | Java |

---

## ⭐ Support This Project

If you find CoTuongAI useful, please consider:

- ⭐ **Star this repository**
- 💖 **[Sponsor on GitHub](https://github.com/sponsors/nguyen05566)** — Get early access to new releases
- 📢 **Share** with Xiangqi friends

### Sponsor Tiers (Coming Soon)
| Tier | Price | Benefits |
|------|-------|----------|
| 🥉 Supporter | $5/mo | Name in README |
| 🥈 Pro | $10/mo | Early access + priority support |
| 🥇 VIP | $25/mo | Custom builds + private Discord |

---

## 📸 Screenshots

*(Coming soon)*

---

## 📄 License

MIT License — See [LICENSE](LICENSE) file

---

## 👤 Author

**nguyen05566** — [GitHub](https://github.com/nguyen05566)

---

## 🙏 Acknowledgments

- **Pikafish** — Chinese Chess engine based on Stockfish
- **ONNX Runtime** — Cross-platform ML inference
- **Xiangqi PWA** — Open-source board recognition inspiration

---

*Made with ❤️ for the Xiangqi community* ♟️
