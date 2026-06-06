# VanillaPlus

VanillaPlus is a **Paper 26.1.2** plugin for a private SMP server. It adds lightweight vanilla-plus systems without requiring an external database.

The project is a rewrite of the old `VanillaEnhancer` plugin, with a stronger focus on modular features, file-based persistence, configurable behavior, and easier testing.

## Status

- Target server: **Paper 26.1.2**
- Target Java: **25**
- Plugin API version: **26.1.2**
- Default language: **Spanish**
- Primary audience: a personal SMP server
- Test stack: **JUnit Jupiter + MockBukkit 26.1.2**

## Features

- **Graveyard**: creates death graves, stores inventory/XP, handles ownership and expiration.
- **Backpack**: per-player backpack inventories persisted to JSON.
- **Homes**: named homes, delayed teleporting, and admin home teleport.
- **Teleport**: request/accept/cancel teleport flow, `/tp back`, delayed effects, and safety checks.
- **Double XP**: configurable XP multiplier with optional AuraSkills integration.
- **Gameplay Enhancements**: bookshelf compatibility tweaks and XP bottle storage.
- **Admin Tools**: inventory/backpack inspection, save/reload helpers, and admin home teleport.
- **Arbiter**: server watcher/restart control based on TPS thresholds.
- **Debug**: development/debug commands.

## Compatibility

VanillaPlus is built against:

- `io.papermc.paper:paper-api:26.1.2.build.+`
- `dev.aurelium:auraskills-api-bukkit:2.3.3` as a compile-only optional integration

`plugin.yml` declares:

- `api-version: '26.1.2'`
- `softdepend: [AuraSkills]`

## Data And Config

The plugin writes runtime files under its plugin data folder.

- `config.yml`: feature toggles and behavior settings.
- `lang.yml`: user-facing messages, default Spanish.
- `data/homes/homes.json`: saved homes.
- `data/backpack/save.json`: backpack contents.
- `data/deaths/graves.json`: grave data.

Bundled `config.yml` and `lang.yml` include numeric `version` fields. On startup, VanillaPlus refreshes the runtime copy when the bundled version is newer.

## Build

Requirements:

- JDK 25
- Gradle wrapper from this repo

Useful commands:

```bash
./gradlew compileJava
./gradlew test
./gradlew shadowJar
./gradlew runServer
```

Build outputs are written to `server/plugins`.

The generated plugin version uses:

```text
<baseVersion>+<yyyyMMddHHmm>
```

For example:

```text
1.4.0+202606061430
```

## Tests

The test suite currently covers important regression areas in Teleport and Homes.

- `TeleportUtilsTest`: safe-location checks.
- `TeleportManagerTest`: `/tp back` expiration race.
- `HomeManagerTest`: home name normalization, updates, limits, delete behavior, reload behavior, and missing-world safeguards.

Run:

```bash
./gradlew test
```

## Development Notes

- Features live under `src/main/java/dev/ezpadaz/vanillaPlus/Features`.
- Shared helpers live under `src/main/java/dev/ezpadaz/vanillaPlus/Utils`.
- Commands use Aikar Commands Framework.
- Feature modules mostly use static managers and static `initialize()` methods.
- Persistence is JSON through Gson.
- Keep user-facing messages in `lang.yml` unless there is a strong reason not to.

## License

MIT.
