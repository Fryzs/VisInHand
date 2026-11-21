<div align="center">

# 🪄 Vis in Hand

**Play Thaumaturge, Industrial Craft 2 and CC: Tweaked comfortably in VR with Vivecraft.**

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-62B47A)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.x-E67E22)
![Requires](https://img.shields.io/badge/requires-Vivecraft%201.3-8E44AD)
![Version](https://img.shields.io/badge/version-0.3.0-2E86C1)
![License](https://img.shields.io/badge/license-MIT-27AE60)

**English** · [Українська](README.uk.md)

</div>

---

Many tech and magic mods assume a mouse and keyboard: they aim where your **head** looks, open radial
menus that follow the **mouse**, and expect you to **type**. In VR none of that works well.
Vis in Hand fixes it for the mods below. Each part switches on **only when that mod is installed**;
Vivecraft is the only hard requirement. Players who are not in VR notice nothing.

| Mod | What changes in VR |
| --- | --- |
| 🔮 **Thaumaturge** | Wands, the thaumometer and other tools aim with your **controller**; a pointable **focus panel** replaces the mouse-only radial menu |
| ⚙️ **Industrial Craft 2: Nostalgia** | The **mining laser** fires from your hand; pushing the **thumbstick** counts as "forward" for jetpacks and quantum leggings |
| 💻 **CC: Tweaked** | The **VR keyboard** opens by itself with computer, turtle and pocket computer screens |

## ✨ Features

### 🔮 Thaumaturge

**Controller aiming** — everything that used to follow your head now follows the hand holding the tool:

| What | Hooked in Thaumaturge |
| --- | --- |
| Wand spells: where they start and where they fly | `SpellTarget.origin` (1.1.0), `SpellTarget.wandOrigin` (newer) |
| Effects at the wand tip | `SpellTarget.visualWandOrigin` |
| Thaumometer scanning and the aspect overlay | `ScanRaycastHelper.performRaycast`, `ScanningManager.blockStack` |
| Draining nodes, refining crystals | `ItemWand.targetedNode`, `ItemWand.refineTarget` |
| Architect focus, Elemental Shovel | `traceAnchor` |
| Configuring essentia tube sides | `BlockEssentiaTransport.traceLook` |

**Focus panel** — in VR, *Change focus* opens a flat panel you point at with the controller laser:

- every focus from your inventory and focus pouches (Curios slots too) on a ring, the current one framed in gold;
- **Remove focus** in the middle (was Shift+F);
- **Area size** / **Area axis** for the Architect focus (was G / Ctrl+G — no keyboard in VR);
- a short controller buzz when you pick something.

### ⚙️ Industrial Craft 2: Nostalgia

- The **mining laser** takes its position and direction from the controller holding it, in every mode,
  including clicking a block.
- **Thumbstick = forward.** IC2 sends a "forward" key to the server for the jetpack and quantum leggings. VR
  players walk with the stick, so while you are in VR, pushing it forward more than 30% counts as holding the key.
- IC2's own keys (Alt, Boost, Mode Switch, Side Inventory, HUD, Gravi) can be bound to controller buttons — see below.

### 💻 CC: Tweaked

Computers read keys straight from the screen rather than from a text box, so Vivecraft never shows its keyboard
for them. Vis in Hand opens the VR keyboard together with any computer, turtle or pocket computer screen and
hides it again when the screen closes. Shutdown, reboot and terminate are the buttons on the side of the
computer screen — no need to hold Ctrl+S / Ctrl+R / Ctrl+T.

## 🎮 Binding controller buttons

Vivecraft puts every mod key into its **Mod** action set:

- **SteamVR:** Settings → Controllers → Manage Controller Bindings → Minecraft (Vivecraft) → Custom → the
  *Mod* tab → bind *Change focus*, *Open Thaumonomicon*, IC2's *Mode Switch Key* and so on;
- **or in game:** Vivecraft settings → Radial Menu → add those actions to Vivecraft's own radial menu.

## 📦 Installation

| | |
| --- | --- |
| Minecraft | 1.21.1 |
| Loader | NeoForge 21.1.x |
| Required | [Vivecraft](https://modrinth.com/mod/vivecraft) 1.3.x |
| Optional | Thaumaturge ≥ 1.1.0, Industrial Craft 2: Nostalgia 21.1, CC: Tweaked ≥ 1.110 |
| Side | **client and server** (the server needs it for controller aiming) |

Drop `visinhand-0.3.0.jar` into the `mods` folder next to Vivecraft.

**Tested** (2026-11-21) with Thaumaturge 1.1.0, IC2 Nostalgia 21.1.0, CC: Tweaked 1.120.2, Vivecraft 1.3.15 and
NeoForge 21.1.267: the game starts, every hook is applied, and it also runs without Thaumaturge. Playing it in
a headset has not been tested yet.

## 🛠️ Building

```
./gradlew build
```

Compiling needs the Thaumaturge and IC2 Nostalgia jars in `libs/` (local only: never committed, never shipped).
CC: Tweaked is not needed to compile.

`./gradlew runSelftest` opens the focus panel (when Thaumaturge is present), saves
`run/screenshots/visinhand_selftest.png` and closes the game by itself (after 2 minutes at most). Patched classes
are dumped to `run/.mixin.out`, so `javap -c` shows exactly where each hook landed (look for `visinhand$`).

## 🧩 How it works

No code from other mods is copied: `@Pseudo` mixins attach to their classes by name, and controller poses come
from Vivecraft's public API (`org.vivecraft.api`). Client-side parts are registered only when their mod is
installed (`VisInHandClient`).

## 🗺️ Roadmap

- Wand and aura HUD on the wrist instead of the screen
- Haptics while casting and draining vis
- Gestures (flick the wand to cast)

## 📄 License

The Vis in Hand code is [MIT](LICENSE). It covers only this mod's own code: Thaumaturge, Industrial Craft 2,
CC: Tweaked and Vivecraft are separate projects under their own licenses, and no part of them is included here.
