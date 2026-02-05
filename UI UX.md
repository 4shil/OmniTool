# OmniTool — Full UI / UX Implementation Specification

Status: Production UI System  
Focus: Implementation Only  
Scope: Visual + Interaction + Screen Architecture  

---

## 1. Global Design Principles

OmniTool UI is optimized for:

- instant tool access
- repeat usage
- minimal navigation
- visual calm
- high contrast clarity

Every screen must prioritize:

speed > decoration  
clarity > density  
intent > aesthetics  

Users should never browse deeply.  
They summon tools quickly.

---

## 2. Theme System

### Base Colors

Background: #0E0F13  
Surface: #161820  
Elevated Surface: #1F2230  

Used to create layered dark hierarchy.

No pure black backgrounds.

---

### Accent System

Primary Lime: #C7F36A → safe / confirm  
Sky Blue: #8EDBFF → file tools  
Warm Yellow: #FFD96B → conversion  
Mint: #6EF3B2 → text tools  
Soft Coral: #FF6A6A → destructive actions  

Accent colors communicate tool category.

Never mix accent colors randomly.

---

### Text Colors

Primary: #FFFFFF  
Secondary: #B8BCC8  
Muted: #6D7285  
Disabled: #4A4F5E  

---

## 3. Typography

Font: Inter / Manrope / SF Pro

Sizes:

Title XL: 32  
Title L: 24  
Header: 20  
Body: 16  
Label: 14  
Caption: 12  

Line spacing: 1.3–1.5

All UI text must remain readable in low light.

---

## 4. Shape System

Radius scale:

Small: 8px  
Medium: 14px  
Large: 22px  
Card radius: 28px  
Buttons: pill or 22px  

No sharp corners anywhere.

Rounded geometry is mandatory.

---

## 5. Spacing System

8pt grid system:

8 / 16 / 24 / 32 / 48

Margins must never be inconsistent.

Whitespace is part of the design.

---

## 6. Elevation & Depth

Cards float above background.

Shadow:

0 8 30 rgba(0,0,0,0.35)

Soft top highlight overlay:

1px rgba(255,255,255,0.05)

No heavy shadows.
Depth must feel soft.

---

## 7. Home Screen Layout

Purpose: command center

Structure:

[ Search Bar ]
[ Recent Tools Strip ]
[ Favorites Grid ]
[ Category Cards ]

Search is visually dominant.

---

### Search Bar

- full width floating card
- large touch area
- placeholder text: “Find a tool…”
- instant keyboard focus
- suggestion list appears live

Search behaves like command palette.

---

### Recent Tools

Horizontal scroll strip

Each item:

- square pastel card
- centered icon
- short label

Max 8 visible items.

Auto updates based on usage.

---

### Favorites Grid

User pinned tools

Grid: 2–3 columns

Cards slightly larger than recents.

Long press → reorder

---

### Category Cards

Intent-based groups:

Fix Text  
Convert Files  
Compress Media  
Secure Data  
Quick Utilities  

Expandable accordion style.

Not dense lists.

---

## 8. Tool Workspace Screen

Universal template:

Header:
← back + tool name

Main workspace card:

Input panel  
Processing controls  
Output panel  

Primary action:

Large pastel floating button

No nested settings.
No sub menus.

One tool = one screen.

---

### Input Panel

- large editable field
- paste shortcut
- auto focus cursor
- scrollable content

---

### Output Panel

- live preview if possible
- copy button
- export/save button

---

## 9. Favorites Screen

Grid layout

User-controlled order

Long press → drag reorder

Pin/unpin actions via context menu

Acts as personal dashboard.

---

## 10. Vault Screen

Security UI style:

- darker background
- minimal accents
- biometric unlock
- blurred preview before unlock
- no recents visible

Psychological separation from main UI.

---

## 11. Settings Screen

Simple list layout

Sections:

Appearance  
Permissions  
Privacy  
About  
Upgrade

No deep navigation trees.

---

## 12. Navigation

Bottom navigation bar:

Home  
Favorites  
Vault  
Settings  

Active tab uses accent glow.

Inactive tabs muted gray.

Navigation depth must never exceed 1 layer.

---

## 13. Interaction Feedback

Tap → scale 0.97  
Success → glow pulse  
Error → gentle shake  
Save → toast slide  

All animations < 200ms

No bounce.
No playful easing.

Professional motion only.

---

## 14. Motion Rules

Allowed:

fade  
slide  
expand  
glow pulse  

Not allowed:

bounce  
spring physics  
overshoot  
cartoon motion

Utility apps must feel precise.

---

## 15. Error UX

Errors appear as floating message cards.

Structure:

Title  
Explanation  
Fix suggestion  

Example:

Invalid JSON format  
Missing bracket near line 12

Never use generic errors.

Teach the user.

---

## 16. Accessibility

Minimum tap target: 44px  
Font scaling supported  
Voice labels for icons  
High contrast text  
Color blind safe accents  
Reduced motion toggle  

Accessibility is mandatory.

---

## 17. Performance UX Rules

No loading spinners unless required.

Prefer:

instant preview  
skeleton states  
progress bars  
partial rendering

UI must feel responsive even during processing.

---

## 18. Cognitive Load Control

User must never see all tools at once.

Tools surface through:

search  
recents  
favorites  
categories

Complexity stays hidden.

Interface remains calm.

---

## 19. Icon System

Outline icons only  
Consistent stroke width  
Rounded terminals  
Minimal geometry  
No mixed icon styles

Icons must be recognizable in < 1 second.

---

## 20. Consistency Rules

No random colors  
No dense layouts  
No mixed radii  
No decorative gradients  
No clutter  
No deep menus  
No tiny touch targets  

Every element must feel intentional.

---

## Final Implementation Identity

Dark floating dashboard  
Pastel semantic cards  
Search-first interaction  
Workspace-driven design  
Fast micro-interactions  
Minimal cognitive load  
Premium calm aesthetic  

The interface disappears.
The tool remains.

---

END OF UI / UX IMPLEMENTATION SPEC
