# Application Concept Document (ACD)
## StabilityLoadingPlanner

**Author:** Lucas Alemão · Nº 15052  
**Course:** Engenharia Informática e de Computadores — Computação Móvel  
**Institution:** ENIDH — Escola Superior Náutica Infante D. Henrique  
**Academic Year:** 2025/2026  

---

## 1. Application Name and General Concept

**Name:** StabilityLoadingPlanner

**Concept:** A professional Android application designed for maritime officers, cadets, and naval engineering students to plan cargo loading operations and verify vessel stability. The app replaces manual paper-based calculations with a real-time digital tool that fetches vessel data via API, calculates stability using certified naval engineering formulas, and integrates live marine weather at the departure port.

The application targets the maritime industry, a field where stability calculations are legally required before departure and errors can have catastrophic consequences.

---

## 2. Main Characteristics and Functionality

### Entities
- **User** — authenticated account with FREE or PRO plan
- **Vessel** — ship identified by IMO number, with technical particulars (LOA, beam, DWT, KG, holds)
- **Tank/Hold** — cargo compartment with capacity, weight, and cargo type
- **Cargo** — type of goods loaded (general cargo, grain, steel coils, containers), each with a VCG factor
- **Voyage** — route between two ports with departure/arrival data
- **Marine Condition** — real-time sea state at the voyage coordinates

### Core Functionality
1. **Vessel Search by IMO** — queries VesselAPI for vessel identification and dimensions; auto-estimates missing technical data using naval architecture formulas
2. **Cargo Plan** — user distributes cargo across holds; app calculates total load vs deadweight capacity
3. **Stability Analysis** — computes GM (metacentric height) and KG (centre of gravity) using KB + BM/V formulas; classifies vessel as STABLE (GM ≥ 0.15m) or UNSTABLE
4. **Marine Conditions** — fetches live wave height, period, sea temperature from Open-Meteo Marine API
5. **Port Database** — 3,700+ global ports via NGA World Port Index; searchable by name or browseable by country
6. **Vessel Photos** — auto-fetched from Wikimedia Commons using the IMO number as search key
7. **Report Export** — PDF report with vessel data, cargo plan, and stability analysis saved to Downloads (PRO)

---

## 3. Target Audience

- **Primary:** Maritime cadets and officers studying or practicing cargo planning
- **Secondary:** Naval engineering students needing a stability calculation tool
- **Tertiary:** Ship officers needing a quick digital tool instead of manual calculations

---

## 4. Business Model — Key Payable Feature

**Model:** Freemium — €1/month PRO subscription

**Free tier:** All core features — vessel search, cargo planning, stability calculation, marine conditions, port search, vessel photos

**PRO feature:** PDF Export — the ability to generate and download a legally-formatted stability and loading report as a PDF. This is the key differentiator because:
- Maritime regulations require documented stability calculations before departure
- Officers need to file and archive these reports
- The PDF includes vessel particulars, cargo distribution, GM/KG values, and GO/NO-GO status

**10 million user scenario:** In a global maritime industry with 1.7 million seafarers worldwide, and a broader market of maritime students, port agents, and logistics professionals, a €1/month PDF export feature for those needing documented reports is a realistic freemium conversion target.

---

## 5. Navigation Map

See `navigation_map.html` for the interactive navigation diagram.

**Summary of main flows:**
- Auth flow: App Start → Login → Register (optional) → Vessel Setup
- Core workflow: Vessel Setup → Cargo Plan → Stability → Marine Conditions → Reports
- Voyage flow: Marine Conditions → Voyage Settings → Marine Conditions (updated coordinates)
- Utility: Any screen → About / Help (via three-dot menu)
- Account: Any screen → Profile (via bottom navigation)

---

## 6. Wire-frames

See `wireframes.html` for all 12 screens in wireframe format.

**Screens:**
1. Login Screen
2. Register Screen
3. Vessel Setup (IMO search + result card)
4. Vessel Registration (manual entry form)
5. Cargo Plan (holds with weight input and progress bars)
6. Stability Analysis (GM/KG metrics + STABLE/UNSTABLE card)
7. Marine Conditions (wave/temperature data + warning)
8. Voyage Settings (departure/arrival port selection)
9. Reports (document preview + PDF export)
10. Profile (user info, plan badge, upgrade)
11. About (author identification)
12. Help (step-by-step guide)

---

## 7. Entity-Association Diagram (without attributes)

See `entity_association.html` for the full E-A diagram.

**Relationships (simplified):**
- USERS have many VOYAGES
- USERS have many SEARCH_HISTORY entries
- USERS save many VESSELS (personal fleet)
- VESSELS appear in many SEARCH_HISTORY entries
- VESSELS are used in many VOYAGES

---

## 8. Firebase Architecture Plan

**Firestore structure:**
```
vessels/{imo}           ← SHARED (all users — collaborative vessel database)
users/{uid}/
  profile/              ← user account data and plan
  searchHistory/        ← IMO searches with timestamp
  savedVessels/         ← personal vessel fleet
  voyages/              ← voyage history with stability results (PRO)
```

**Firebase Auth:** Email/Password authentication (Google Sign-In as planned extra feature)

**Key architectural decision:** The `vessels/` collection is shared across all users. When a user corrects estimated vessel dimensions using the edit dialog, those corrections are saved to `vessels/{imo}` and become available to all future users searching the same IMO. This creates a community-sourced vessel database that improves with each user.

---

## 9. Technologies Used

| Technology | Purpose | Status |
|---|---|---|
| Kotlin + Jetpack Compose | Android UI | Implemented |
| Retrofit 2 + OkHttp | HTTP API clients | Implemented |
| Coil | Image loading | Implemented |
| VesselAPI | Vessel data by IMO | Implemented |
| NGA World Port Index | Global port database | Implemented |
| Open-Meteo Marine | Live sea conditions | Implemented |
| Wikimedia Commons API | Vessel photos | Implemented |
| Android PdfDocument + MediaStore | PDF generation | Implemented |
| Firebase Auth | User authentication | Planned |
| Firebase Firestore | Cloud state storage | Planned |

---

*ACD v1.0 — May 2026*
