# ⚓ StabilityLoadingPlanner

**Maritime Cargo & Stability Planning App for Android**

> Final Project — Computação Móvel | ENIDH | 2025/2026  
> Author: Lucas Alemão · Nº 15052 · Engenharia Informática e de Computadores

---

## 📊 Project Documents

| Document | View |
|---|---|
| Wire-frames (all 12 screens) | [Open](https://htmlpreview.github.io/?https://raw.githubusercontent.com/Alemaozinho/CM_Git/main/Projeto/docs/wireframes.html) |
| Navigation Map | [Open](https://htmlpreview.github.io/?https://raw.githubusercontent.com/Alemaozinho/CM_Git/main/Projeto/docs/navigation_map.html) |
| Entity-Association Diagram (Firebase) | [Open](https://htmlpreview.github.io/?https://raw.githubusercontent.com/Alemaozinho/CM_Git/main/Projeto/docs/entity_association.html) |
| Application Concept Document (ACD) | [docs/ACD.md](docs/ACD.md) |

---

## 📱 Overview

StabilityLoadingPlanner is an Android application designed to assist maritime officers and students in planning cargo loading operations and calculating vessel stability. The app integrates real-time vessel data, global port databases, live marine weather conditions, and AI-powered vessel identification into a single professional tool.

The app follows a **freemium business model**: core features are available to all users, while PDF report export is locked behind a PRO subscription (€1/month).

---

## ✨ Features

### Core (Free)
| Feature | Description |
|---|---|
| 🚢 Vessel Search | Search any vessel worldwide by IMO number via VesselAPI |
| 📐 Dimension Estimation | Automatic LOA/Beam/DWT estimation from vessel type when API data is unavailable |
| 🤖 AI Vessel Lookup | Gemini AI as fallback when VesselAPI and shared database have no data |
| 📦 Cargo Planning | Dynamic hold allocation with cargo type selection and weight input |
| ⚖️ Stability Calculation | Real-time GM/KG calculation using KB + BM/V naval engineering formulas |
| 🌊 Marine Conditions | Live sea state from Open-Meteo Marine API (wave height, period, temperature) |
| 🗺️ Port Search | 3,700+ ports worldwide via NGA World Port Index — browse by country or name |
| 📸 Vessel Photos | Auto-fetched from Wikimedia Commons / Wikipedia by IMO number |
| 🔐 Authentication | Firebase Authentication (email/password) |
| 🌍 Multilingual | Português, English, Español — automatic (device language) or manual via ⋮ menu |
| ℹ️ Help & About | Step-by-step usage guide and author identification |

### PRO (€1/month)
| Feature | Description |
|---|---|
| 📄 PDF Export | Full stability and cargo loading report saved to Downloads |

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (ViewModel + mutableStateOf)
- **Navigation:** Jetpack Navigation Compose
- **HTTP:** Retrofit 2 + OkHttp 3
- **Image Loading:** Coil
- **Auth:** Firebase Authentication
- **Database:** Firebase Firestore
- **PDF Generation:** Android PdfDocument + MediaStore API
- **AI:** Google Gemini API (gemini-2.0-flash)

---

## 🌐 APIs

| API | Purpose | Auth |
|---|---|---|
| [VesselAPI](https://api.vesselapi.com) | Vessel data by IMO | Bearer token (free tier) |
| [NGA World Port Index](https://services9.arcgis.com/j1CY4yzWfwptbTWN/arcgis/rest/services/WorldPortIndex_WFL1/FeatureServer/0/) | 3,700+ global ports | None (public) |
| [Open-Meteo Marine](https://marine-api.open-meteo.com) | Live sea conditions | None (public) |
| [Wikimedia Commons](https://commons.wikimedia.org) | Vessel photos | None (User-Agent required) |
| [Wikipedia](https://en.wikipedia.org) | Vessel article photos | None (User-Agent required) |
| [Google Gemini](https://ai.google.dev) | AI vessel identification | API Key |

---

## 🧮 Stability Calculation

```
Draft  = Displacement / (LOA × Beam × 0.75)
KB     = Draft / 2
BM     = (LOA × Beam³ / 12) / (Displacement / 1.025)
KMT    = KB + BM
KG     = Σ(weight_i × VCG_i) / Σ(weight_i)
GM     = KMT − KG
```

A vessel is considered **stable** when GM ≥ 0.15 m.

---

## 🗂️ Project Structure

```
app/src/main/java/com/example/stabilityloadingplanner/
├── api/
│   ├── GeminiApiService.kt      ← Google Gemini AI (vessel identification)
│   ├── OpenMeteoApi.kt          ← Retrofit clients (weather, ports, wiki, vessel)
│   ├── PortApiService.kt        ← NGA World Port Index endpoints
│   ├── VesselApiService.kt      ← VesselAPI endpoint
│   └── VesselPhotoApiService.kt ← Wikipedia + Wikimedia Commons endpoints
├── data/
│   └── models/
│       ├── MarineModels.kt
│       ├── PortModels.kt
│       ├── Tank.kt
│       ├── Vessel.kt
│       ├── VesselApiModels.kt
│       ├── VesselDatabase.kt
│       ├── VesselPhotoModels.kt
│       ├── Voyage.kt
│       ├── User.kt
│       └── LocalUserDatabase.kt
└── ui/
    └── theme/
        ├── Color.kt             ← Industrial maritime palette
        ├── Theme.kt
        ├── Type.kt
        ├── Components.kt        ← ExactBottomNav, AppMenuActions, language dialog
        ├── AppNavigation.kt
        ├── VesselViewModel.kt   ← Central ViewModel (AI lookup, marine data, cargo)
        ├── AuthViewModel.kt
        ├── LoginScreen.kt
        ├── RegisterScreen.kt
        ├── VesselSetupScreen.kt
        ├── VesselRegistrationScreen.kt
        ├── CargoPlanScreen.kt
        ├── StabilityScreen.kt
        ├── MarineConditionsScreen.kt
        ├── VoyageSettingsScreen.kt
        ├── ReportsScreen.kt
        ├── ProfileScreen.kt
        ├── AboutScreen.kt
        └── HelpScreen.kt

app/src/main/res/
├── values/strings.xml           ← English (default)
├── values-pt/strings.xml        ← Português
└── values-es/strings.xml        ← Español
```

---

## ⚙️ Setup

### Prerequisites
- Android Studio Meerkat or newer
- Android SDK 24+
- JDK 17

### API Keys

Create `local.properties` in the project root:

```properties
sdk.dir=/path/to/your/Android/sdk
VESSEL_API_KEY=your_vessel_api_key_here
GEMINI_API_KEY=your_gemini_api_key_here
```

- **VesselAPI key:** [vesselapi.com](https://vesselapi.com) → free account
- **Gemini API key:** [aistudio.google.com](https://aistudio.google.com) → Get API key → Create API key

> ⚠️ `local.properties` is git-ignored and never committed.

### Build

```bash
git clone https://github.com/Alemaozinho/CM_Git.git
cd CM_Git/Projeto
# Open in Android Studio → Build → Run 'app'
```

### Test Accounts

| Email | Password | Plan |
|---|---|---|
| `free@test.com` | `free123` | FREE |
| `pro@test.com` | `pro123` | PRO |

---

## 🔒 Security

- API keys in `local.properties` (git-ignored)
- Keys accessed at runtime via `BuildConfig`
- `buildConfig = true` in `build.gradle.kts`
- `google-services.json` git-ignored

---

## 🌍 Multilingual Support

The app supports **Português, English, and Español**. Language is selected automatically from the device system language, or manually via the ⋮ menu → "Language / Idioma" dialog. The selection persists across sessions via `SharedPreferences` and is applied via `attachBaseContext` in `MainActivity`.

---

## 🤖 AI Integration (Gemini)

The app uses Google Gemini as a third fallback in the vessel lookup chain:

```
Firestore shared DB → VesselAPI → Gemini AI → local estimates
```

When Gemini is available, it identifies the vessel by IMO and returns technical dimensions in JSON format. The integration uses `gemini-2.0-flash` on the free tier (1500 req/day). If the quota is exhausted, the app falls back gracefully to local estimation formulas.

---

## 📐 Firebase Architecture

```
Firestore (root)
├── vessels/{imo}         [SHARED — all users read/write]
│   └── Community-sourced vessel database
│       Users who correct estimated dimensions contribute to all future users
│
└── users/{uid}/          [PRIVATE — per authenticated user]
    ├── profile/
    ├── searchHistory/
    ├── savedVessels/
    └── voyages/          [PRO only]
```

---

## 📄 License

Academic project — ENIDH 2025/2026. Not for commercial use.

---

## 👤 Author

**Lucas Alemão**  
Nº 15052  
Engenharia Informática e de Computadores  
Escola Superior Náutica Infante D. Henrique (ENIDH)  
Cadeira: Computação Móvel  
Ano Lectivo: 2025/2026
