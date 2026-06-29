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

StabilityLoadingPlanner is an Android application designed to assist maritime officers and students in planning cargo loading operations and calculating vessel stability. The app integrates real-time vessel data, global port databases, and live marine weather conditions into a single professional tool.

The app follows a **freemium business model**: core features are available to all users, while PDF report export is locked behind a PRO subscription (€1/month).

---

## ✨ Features

### Core (Free)
| Feature | Description |
|---|---|
| 🚢 Vessel Search | Search any vessel worldwide by IMO number via VesselAPI |
| 📐 Dimension Estimation | Automatic LOA/Beam/DWT estimation from vessel type when API data is unavailable |
| 📦 Cargo Planning | Dynamic hold allocation with cargo type selection and weight input |
| ⚖️ Stability Calculation | Real-time GM/KG calculation using KB + BM/V naval engineering formulas |
| 🌊 Marine Conditions | Live sea state from Open-Meteo Marine API (wave height, period, temperature) |
| 🗺️ Port Search | 3,700+ ports worldwide via NGA World Port Index — browse by country or name |
| 📸 Vessel Photos | Auto-fetched from Wikimedia Commons by IMO number |
| 🔐 Authentication | Local user accounts with FREE/PRO tiers |
| ℹ️ Help & About | Step-by-step usage guide and author information |

### PRO (€1/month)
| Feature | Description |
|---|---|
| 📄 PDF Export | Full stability and cargo loading report saved to Downloads |

### Planned (Firebase)
- Cloud sync of vessel data across devices
- Collaborative vessel database (community-sourced technical data)
- Search history per user with "Use Again" button
- Voyage history with stability results (PRO)
- Google Sign-In

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (ViewModel + mutableStateOf)
- **Navigation:** Jetpack Navigation Compose
- **HTTP:** Retrofit 2 + OkHttp 3
- **Image Loading:** Coil
- **PDF Generation:** Android PdfDocument + MediaStore API

---

## 🌐 APIs

| API | Purpose | Auth |
|---|---|---|
| [VesselAPI](https://api.vesselapi.com) | Vessel data by IMO | Bearer token (free tier) |
| [NGA World Port Index](https://services9.arcgis.com/j1CY4yzWfwptbTWN/arcgis/rest/services/WorldPortIndex_WFL1/FeatureServer/0/) | 3,700+ global ports | None (public) |
| [Open-Meteo Marine](https://marine-api.open-meteo.com) | Live sea conditions | None (public) |
| [Wikimedia Commons](https://commons.wikimedia.org) | Vessel photos by IMO | None (User-Agent required) |
| [Wikipedia](https://en.wikipedia.org) | Vessel article photos | None (User-Agent required) |

---

## 🧮 Stability Calculation

The app computes metacentric height (GM) using simplified naval architecture formulas:

```
Draft  = Displacement / (LOA × Beam × 0.75)
KB     = Draft / 2                                    (rectangular section approximation)
BM     = (LOA × Beam³ / 12) / (Displacement / 1.025) (BM = I / V)
KMT    = KB + BM
KG     = Σ(weight_i × VCG_i) / Σ(weight_i)          (weighted centre of gravity)
GM     = KMT − KG
```

A vessel is considered **stable** when GM ≥ 0.15 m.

---

## 🗂️ Project Structure

```
app/src/main/java/com/example/stabilityloadingplanner/
├── api/
│   ├── OpenMeteoApi.kt          ← Retrofit clients (weather, ports, wiki, vessel)
│   ├── PortApiService.kt        ← NGA World Port Index endpoints
│   ├── VesselApiService.kt      ← VesselAPI endpoint
│   └── VesselPhotoApiService.kt ← Wikipedia + Wikimedia Commons endpoints
├── data/
│   └── models/
│       ├── MarineModels.kt      ← Open-Meteo response models
│       ├── PortModels.kt        ← Port query response models
│       ├── Tank.kt              ← Tank + CargoType data classes
│       ├── Vessel.kt            ← Vessel data class
│       ├── VesselApiModels.kt   ← VesselAPI response models
│       ├── VesselDatabase.kt    ← In-memory local vessel store
│       ├── VesselPhotoModels.kt ← Wikipedia/Commons response models
│       ├── Voyage.kt            ← Voyage data class
│       ├── User.kt              ← User data class
│       └── LocalUserDatabase.kt ← In-memory user store
└── ui/
    └── theme/
        ├── Color.kt             ← Design tokens (industrial maritime palette)
        ├── Theme.kt             ← Material 3 theme
        ├── Type.kt              ← Typography
        ├── Components.kt        ← Shared composables (BottomNav, AppMenuActions)
        ├── AppNavigation.kt     ← NavHost with all routes
        ├── VesselViewModel.kt   ← Central ViewModel
        ├── AuthViewModel.kt     ← Authentication ViewModel
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
```

---

## ⚙️ Setup

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 26+
- JDK 17

### API Key Configuration

This app requires a **VesselAPI key**. Never commit the key to version control.

1. Go to [vesselapi.com](https://vesselapi.com) and create a free account
2. Copy your API key
3. Open (or create) `local.properties` in the project root:

```properties
sdk.dir=/path/to/your/Android/sdk
VESSEL_API_KEY=your_api_key_here
```

> ⚠️ `local.properties` is listed in `.gitignore` and will never be committed.

### Build

```bash
# Clone the repository
git clone https://github.com/Alemaozinho/CM_Git.git
cd CM_Git/Projeto

# Open in Android Studio and let Gradle sync
# Then: Build → Run 'app'
```

### Test Accounts

The app ships with two built-in test accounts:

| Email | Password | Plan |
|---|---|---|
| `free@test.com` | `free123` | FREE |
| `pro@test.com` | `pro123` | PRO |

---

## 🔒 Security

- API keys stored in `local.properties` (git-ignored)
- Keys accessed at runtime via `BuildConfig` (generated by Gradle)
- `buildConfig = true` enabled in `build.gradle.kts`
- `google-services.json` listed in `.gitignore` (never committed)

---

## 🚧 Roadmap

- [ ] **Firebase Auth** — replace local user database with Firebase Authentication
- [ ] **Firestore** — cloud sync of vessels, voyages, and search history
- [ ] **Community vessel database** — users contribute real technical data by IMO
- [ ] **Multilingual support** — PT + EN + ES (strings.xml)
- [ ] **Google Sign-In** — Firebase Auth extra provider (+1 grade point)
- [ ] **Search history** — "Use Again" button for previously searched vessels
- [ ] **Voyage history** (PRO) — archive of past voyages with stability reports

---

## 📐 Architecture (planned with Firebase)

```
┌──────────────────────────────────────────┐
│               Firestore                  │
│  vessels/{imo}         (shared — all)    │
│  users/{uid}/                            │
│    searchHistory/      (private)         │
│    savedVessels/       (private)         │
│    voyages/            (PRO only)        │
└──────────────────────────────────────────┘
          ↑
   VesselViewModel
          ↑
   Jetpack Compose UI
```

**Data flow:** IMO search → check Firestore `vessels/{imo}` → if found, use cached real data → if not, call VesselAPI + estimate dimensions → if user corrects via ✎, save back to Firestore for all future users.

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
