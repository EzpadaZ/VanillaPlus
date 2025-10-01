# VanillaPlus

A **Minecraft Paper plugin** for **1.21.9+** that extends the vanilla experience with **lightweight, modular features**.  
Inspired by my old `VanillaEnhancer` plugin — which had to walk so `VanillaPlus` could run 🏃.

---

## ✨ Why VanillaPlus?

I restarted development from scratch after archiving `VanillaEnhancer` because the old codebase was:
- ❌ Unmaintainable and tightly coupled.
- ❌ Hard to extend (e.g., adding another database was a nightmare).
- ❌ Dependent on MongoDB — no Mongo meant no plugin.

With VanillaPlus, my goals are:
- ✅ Modular features (easy to enable/disable).
- ✅ Configuration-driven (no DB lock-in).
- ✅ Proper dependency checks and cleaner architecture.

---

## 📦 Features

VanillaPlus adds server-friendly, SMP-oriented improvements while staying close to vanilla gameplay:

- ⚰️ **Graveyard System** – Player deaths spawn a grave (head + inventory).
- 🎒 **Backpack System** – Store items in personal backpacks.
- 🏠 **Homes & Teleports** – Save locations, teleport to friends, or bring them to you.
- ✨ **Double XP / 2XP Boost** – Command to increase XP gain (with AureliumSkills integration).
- 🛠️ **Gameplay Enhancements** – Small quality-of-life tweaks and plugin compatibility fixes.
- 👁️ **Admin Tools** – Check player inventories (useful for SMP moderation).
- ⏱️ **Arbiter System** – Monitors server performance and safely restarts when needed (works with auto-boot setup).
- 🌐 **Language Support** – Includes a `lang.yml` file for translations (default: Spanish).

---

## 🗺️ Roadmap

- More modular event systems.
- Expanded `lang.yml` for multiple language support.
- Additional gameplay tweaks depending on server needs.

This plugin is primarily developed for **my own SMP server**, but it’s flexible enough for others who want a **vanilla-plus** experience.

---

## 🧩 Compatibility

- Built against **Paper 1.21.9** (latest release).
- Integrates with **AureliumSkills**.
- Designed to coexist with other lightweight vanilla-friendly plugins.

---

## 📖 Installation

1. Download the latest release `.jar`.
2. Drop it into your server’s `/plugins` folder.
3. Restart the server.
4. Configure options inside the generated `config.yml` and `lang.yml`.

---

## 🙋 Notes

- This project is personal and features will be added as I find new ideas useful for my own server.
- Aim: keep it **as vanilla as possible**, only enhancing what feels natural to Minecraft.

---

## 📜 License

MIT — free to use, modify, and adapt.

---