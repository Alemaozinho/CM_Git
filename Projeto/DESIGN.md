---
name: Maritime Precision
colors:
  surface: '#0a1421'
  surface-dim: '#0a1421'
  surface-bright: '#313a49'
  surface-container-lowest: '#060e1c'
  surface-container-low: '#131c2a'
  surface-container: '#17202e'
  surface-container-high: '#212a39'
  surface-container-highest: '#2c3544'
  on-surface: '#dae3f6'
  on-surface-variant: '#c5c5d3'
  inverse-surface: '#dae3f6'
  inverse-on-surface: '#283140'
  outline: '#8f909d'
  outline-variant: '#444651'
  surface-tint: '#b6c4ff'
  primary: '#b6c4ff'
  on-primary: '#05297a'
  primary-container: '#1e3a8a'
  on-primary-container: '#90a8ff'
  inverse-primary: '#4059aa'
  secondary: '#b7c8e1'
  on-secondary: '#213145'
  secondary-container: '#3a4a5f'
  on-secondary-container: '#a9bad3'
  tertiary: '#89ceff'
  on-tertiary: '#00344d'
  tertiary-container: '#004565'
  on-tertiary-container: '#36b6fb'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#dce1ff'
  primary-fixed-dim: '#b6c4ff'
  on-primary-fixed: '#00164e'
  on-primary-fixed-variant: '#264191'
  secondary-fixed: '#d3e4fe'
  secondary-fixed-dim: '#b7c8e1'
  on-secondary-fixed: '#0b1c30'
  on-secondary-fixed-variant: '#38485d'
  tertiary-fixed: '#c9e6ff'
  tertiary-fixed-dim: '#89ceff'
  on-tertiary-fixed: '#001e2f'
  on-tertiary-fixed-variant: '#004c6e'
  background: '#0a1421'
  on-background: '#dae3f6'
  surface-variant: '#2c3544'
typography:
  display-lg:
    fontFamily: Hanken Grotesk
    fontSize: 57px
    fontWeight: '700'
    lineHeight: 64px
    letterSpacing: -0.25px
  headline-lg:
    fontFamily: Hanken Grotesk
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-md:
    fontFamily: Hanken Grotesk
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
  title-lg:
    fontFamily: Hanken Grotesk
    fontSize: 22px
    fontWeight: '500'
    lineHeight: 28px
  title-md:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
    letterSpacing: 0.15px
  body-lg:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  body-md:
    fontFamily: Hanken Grotesk
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.25px
  label-lg:
    fontFamily: Hanken Grotesk
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.1px
  label-md:
    fontFamily: Hanken Grotesk
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  unit: 8px
  container-padding: 24px
  gutter: 16px
  density-compact: 4px
  density-comfortable: 12px
---

## Brand & Style

The design system is engineered for the high-stakes environment of maritime cargo operations and vessel stability management. It prioritizes **functional minimalism** and **instrument-grade precision**, evoking the reliability of a ship's bridge consoles. 

The aesthetic is a refined evolution of **Material Design 3 (M3)**, optimized for low-light environments (Dark Mode) to reduce eye strain during night watches. It rejects decorative flourishes in favor of data density and clarity. The UI should feel like a sophisticated piece of hardware—authoritative, resilient, and focused entirely on the user's cognitive load during complex loading operations.

**Key Principles:**
- **Mission Criticality:** Every element must serve a purpose in the vessel's safety and stability.
- **Instrument Aesthetic:** High-contrast text against deep, recessive backgrounds.
- **Professional Rigor:** Structured, logical layouts that mirror industrial engineering standards.

## Colors

The color palette is anchored in a deep **Charcoal/Slate Gray** to provide a stable, low-glare foundation. The **Ocean Blue** primary accent is used sparingly to denote action and focus, ensuring it stands out against the dark environment without causing visual fatigue.

- **Backgrounds:** Utilize `#121b29` for the lowest level. Use incremental lightness (Surface-dim to Surface-bright) to define hierarchy rather than shadows.
- **Accents:** Use Tertiary Blue for secondary interactive elements and info-states.
- **Semantic Colors:** Critical for maritime safety. **Error Red** and **Warning Amber** must be high-chroma to ensure immediate visibility against the dark background when stability limits are approached.
- **Text:** Standardize on `#ffffff` for primary information and `#94a3b8` (Slate 400) for secondary metadata.

## Typography

**Hanken Grotesk** is selected for its sharp, contemporary geometry and exceptional legibility in technical contexts. Its balanced apertures and clean terminals ensure that numeric data—crucial for stability calculations—is unambiguous even at small sizes.

- **Numeric Data:** For tabular data and stability metrics, use `body-md` or `label-lg`. Ensure tabular lining figures are used for vertical alignment of numbers.
- **Hierarchy:** Use `title-md` for section headers within cards to maintain a compact vertical footprint.
- **Labels:** Use `label-md` in all-caps with the specified letter spacing for technical units (e.g., MT, MTRS, LCG) to distinguish them from variable values.

## Layout & Spacing

This design system employs an **8px grid system** optimized for a high-density, professional dashboard. The layout philosophy is a **fixed-fluid hybrid**: sidebars and utility panels maintain fixed widths (e.g., 280px), while the central calculation and visualization area (vessel profile) scales fluently.

- **Data Density:** High-density layouts are preferred. Use `density-compact` (4px) for related input groups (e.g., Draft Fore/Aft/Mid) and `density-comfortable` (12px) for spacing between unrelated functional blocks.
- **Grid:** Use a 12-column grid for desktop. For the main stability planner view, use a split-screen layout with a 4-column cargo list and an 8-column vessel visualization.
- **Breakpoints:** 
  - Mobile (< 600px): Single column, full-width cards.
  - Tablet (600px - 1024px): 8-column grid, persistent bottom sheet for summary metrics.
  - Desktop (> 1024px): 12-column grid, persistent left navigation.

## Elevation & Depth

In alignment with Modern M3 in Dark Mode, depth is communicated through **tonal layering** rather than traditional drop shadows. Higher elevation levels are represented by lighter surface container colors.

- **Level 0 (Background):** `#121b29` – The base canvas.
- **Level 1 (Cards/Panels):** `#1e293b` – Primary containers for loading lists and tank tables.
- **Level 2 (Dialogs/Popovers):** `#334155` – Floating elements that require immediate attention.
- **Outlines:** Use a subtle 1px border (`#ffffff1a`) for all cards to maintain structural definition without relying on glow or shadow effects, ensuring clarity on various display qualities.

## Shapes

To maintain a professional and industrial character, the design system utilizes **Soft (Level 1)** roundedness. This provides enough softening to feel modern and accessible while maintaining the rigid, structured feel of technical software.

- **Standard Elements:** 4px (`0.25rem`) corner radius for input fields, buttons, and small components.
- **Large Containers:** 8px (`0.5rem`) for cards and main content areas.
- **Interactive States:** Use sharp corners for data grid cells to maximize the screen real estate for numeric entry.

## Components

### Buttons
- **Primary:** Filled with `#1e3a8a`, white text. 4px radius. 
- **Secondary:** Outlined with `#94a3b8`, no fill.
- **Critical Action:** Filled with `#ef4444` (e.g., "Emergency Dump" or "Reset Loading Plan").

### Input Fields
- Use **Filled** M3 style with a dark background (`#0f172a`) and a bottom stroke. 
- Labels must remain visible (not floating) when data is entered to ensure no ambiguity in calculation units.
- Active state indicated by a Primary Blue 2px bottom stroke.

### Data Tables
- Use `body-md` for row content.
- Alternate row striping using `#1e293b` and `#121b29`.
- Headers are sticky with a 1px bottom border and `label-md` typography.

### Cards
- No shadows. Use `#1e293b` fill with a `#334155` 1px border.
- Header sections within cards should have a subtle background tint to separate title from content.

### Status Indicators
- **Gauges:** Circular or linear indicators for KG/GM limits. Use a gradient from Success Green to Error Red.
- **Chips:** Small, low-profile badges for cargo categories (e.g., "Hazardous," "Reefer"). 4px radius, monochromatic styling.

### Vessel Visualization
- Represent the vessel profile with a clean, 2px outline. Cargo blocks should be filled with semi-transparent colors based on cargo type, with a 1px border for clear separation between bays/hatches.