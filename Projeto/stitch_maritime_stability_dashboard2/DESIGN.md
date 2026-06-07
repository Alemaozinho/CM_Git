---
name: Maritime Industrial Logic
colors:
  surface: '#f7f9ff'
  surface-dim: '#d7dadf'
  surface-bright: '#f7f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f1f4f9'
  surface-container: '#ebeef3'
  surface-container-high: '#e5e8ee'
  surface-container-highest: '#e0e3e8'
  on-surface: '#181c20'
  on-surface-variant: '#414754'
  inverse-surface: '#2d3135'
  inverse-on-surface: '#eef1f6'
  outline: '#717786'
  outline-variant: '#c1c6d7'
  surface-tint: '#005bc0'
  primary: '#0059bb'
  on-primary: '#ffffff'
  primary-container: '#0070ea'
  on-primary-container: '#fefcff'
  inverse-primary: '#adc7ff'
  secondary: '#855400'
  on-secondary: '#ffffff'
  secondary-container: '#ffa504'
  on-secondary-container: '#684100'
  tertiary: '#545d65'
  on-tertiary: '#ffffff'
  tertiary-container: '#6d767e'
  on-tertiary-container: '#fcfcff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d8e2ff'
  primary-fixed-dim: '#adc7ff'
  on-primary-fixed: '#001a41'
  on-primary-fixed-variant: '#004493'
  secondary-fixed: '#ffddb7'
  secondary-fixed-dim: '#ffb95c'
  on-secondary-fixed: '#2a1700'
  on-secondary-fixed-variant: '#653e00'
  tertiary-fixed: '#dbe4ed'
  tertiary-fixed-dim: '#bfc8d0'
  on-tertiary-fixed: '#141d23'
  on-tertiary-fixed-variant: '#3f484f'
  background: '#f7f9ff'
  on-background: '#181c20'
  surface-variant: '#e0e3e8'
typography:
  display-lg:
    fontFamily: Public Sans
    fontSize: 57px
    fontWeight: '700'
    lineHeight: 64px
    letterSpacing: -0.25px
  headline-lg:
    fontFamily: Public Sans
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-md:
    fontFamily: Public Sans
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
  headline-sm:
    fontFamily: Public Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  title-lg:
    fontFamily: Public Sans
    fontSize: 22px
    fontWeight: '500'
    lineHeight: 28px
  title-md:
    fontFamily: Public Sans
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
    letterSpacing: 0.15px
  body-lg:
    fontFamily: Public Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  body-md:
    fontFamily: Public Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.25px
  label-lg:
    fontFamily: Public Sans
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-md:
    fontFamily: Public Sans
    fontSize: 12px
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
  unit: 4px
  gutter: 16px
  margin-mobile: 16px
  margin-desktop: 24px
  container-max-width: 1440px
---

## Brand & Style

This design system is engineered for the high-stakes environment of maritime operations, where clarity, reliability, and speed of recognition are paramount. The aesthetic follows a **Corporate Modern** approach with a strict adherence to **Material Design 3** logic, adapted for industrial utility.

The UI evokes a sense of "digital instrumentation"—precise, no-nonsense, and highly legible under various lighting conditions. It prioritizes information density and functional hierarchy over decorative elements, ensuring that professional mariners and logistics coordinators can interpret complex data sets at a glance. The emotional response is one of controlled efficiency and institutional trust.

## Colors

The palette is rooted in industrial safety and maritime standards. 

- **Primary (Corporate Blue):** Used for primary actions, active states, and navigational anchors. It represents the stability of the maritime sector.
- **Secondary (Safety Orange):** Reserved for high-visibility accents, warnings that do not reach "error" status, and critical call-to-actions that require immediate visual attention.
- **Neutral / Surface:** A crisp white background is paired with a light grey surface tiering to define functional zones without introducing visual clutter.
- **High-Contrast Text:** All body and label text utilizes a deep charcoal (#212529) to ensure maximum legibility and compliance with accessibility standards against light backgrounds.

## Typography

**Public Sans** is selected for its institutional clarity and neutral characteristics. It is a typeface designed for government and industrial interfaces, offering excellent readability in data-heavy tables and technical dashboards.

- **Headlines:** Use Semi-Bold (600) weights to create clear structural anchors.
- **Technical Data:** For coordinates, timestamps, or vessel IDs, use `label-md` with strict tracking to maintain a "monospaced" feel within the sans-serif family.
- **Contrast:** Maintain a minimum 4.5:1 contrast ratio for all body text. Use `body-lg` for primary content and `body-md` for secondary metadata.

## Layout & Spacing

The layout philosophy follows a **Dense Fixed Grid** system to maximize information per square inch while maintaining Material Design's hierarchical logic.

- **Grid:** A 12-column grid for desktop with 16px gutters.
- **Minimalism:** Spacing is tightened (using a 4px base unit) compared to standard consumer apps to reflect the efficiency of professional software.
- **Rhythm:** Vertical rhythm is strictly enforced in increments of 8px (2 units). 
- **Reflow:** On mobile devices, columns collapse to a single-stack layout with 16px side margins. Tabular data should transition to "card-list" views or utilize horizontal scrolling with locked primary columns.

## Elevation & Depth

In line with the Industrial aesthetic, elevation is primarily communicated through **Tonal Layers** and **Low-Contrast Outlines** rather than heavy shadows.

- **Surface Tiers:** The background is `#FFFFFF`. Containers (cards, sidebars) use `#F8F9FA` or a 1px border of `#DEE2E6`.
- **Shadows:** Use only when an element is "floating" (e.g., Modals, Menus). Shadows must be sharp and subtle: `0px 2px 4px rgba(0, 0, 0, 0.05)`.
- **Interaction:** Hover states are indicated by a slight tonal shift (darkening the surface by 4%) rather than an increase in shadow depth.

## Shapes

The shape language is **Soft (0.25rem)**. This provides a subtle modern touch while retaining a rigid, structured feel appropriate for engineering and maritime tools.

- **Components:** Buttons, Input Fields, and Chips use a 4px radius.
- **Containers:** Large cards and Modals may use up to 8px (`rounded-lg`) to differentiate them from smaller UI elements.
- **Icons:** Use "Sharp" or "Rounded" Material Symbol variants, but do not mix styles. Icons should be encased in a square 24px bounding box.

## Components

### Buttons
- **Primary:** Solid `#007BFF` with white text. 4px border radius.
- **Secondary:** Outlined with `#007BFF` or solid `#FFA500` for urgent operations (e.g., "Initiate Transfer").
- **States:** Disabled buttons use `#E9ECEF` with `#ADB5BD` text.

### Input Fields
- **Style:** Outlined (Material Design 3 style). 1px border of `#CED4DA`. 
- **Active State:** 2px primary blue border with a high-contrast label.
- **Dense:** Use a 40px height for standard inputs to accommodate professional data entry.

### Cards & Lists
- **Cards:** No shadow, 1px `#DEE2E6` border, `#F8F9FA` background. 
- **Lists:** High-density rows (48px height) with 1px dividers.

### Feedback
- **Error:** Text and icons in `#DC3545`. Background tints for error banners should be ultra-light (5% opacity of red).
- **Status Chips:** Use a "Traffic Light" system: Green (Normal), Orange (Secondary/Warning), Red (Error).

### Specialized Maritime Components
- **Data Grids:** Fixed headers, zebra-striping using the surface color, and compact text.
- **Navigation Rail:** A slim vertical navigation bar (72px wide) to maximize horizontal space for charts and tables.