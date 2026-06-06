# VanillaPlus

VanillaPlus is a modular vanilla-plus SMP plugin for **Paper 26.1.2**. It bundles the server utilities used by the SMP into one plugin: homes, teleport requests, backpacks, graves, XP tools, admin inspection commands, watcher controls, and small gameplay fixes.

The project is a rewrite of the old `VanillaEnhancer` plugin. The current direction is to keep the feature set file-backed, configurable, Spanish-first for player messages, and covered by focused regression tests where bugs have already appeared.

## Current Status

- Project version base: **1.4.0**
- Generated plugin version: **`1.4.0+<yyyyMMddHHmm>`**
- Target server: **Paper 26.1.2**
- Plugin API: **26.1.2**
- Target Java: **25**
- Default language: **Spanish**
- Build tool: **Gradle wrapper**
- Test stack: **JUnit Jupiter + MockBukkit 26.1.2**
- Optional integration: **AuraSkills 2.3.3**

`plugin.yml` declares `api-version: '26.1.2'` and `softdepend: [AuraSkills]`.

## Features

### Teleport

Player teleport request flow with delayed teleports, effects, safety checks, cancellation, and `/tp back`.

- Commands: `/teleport`, `/tp`, `/viaje`
- Request someone to come to you: `/tp here <player>`, `/tp traer <player>`, `/tp aqui <player>`
- Request to go to someone: `/tp to <player>`, `/tp ir <player>`, `/tp hacia <player>`
- Return to the last saved pre-teleport location: `/tp back`, `/tp regresar`, `/tp atras`
- Accept a request: `/tp accept [id]`, `/tp aceptar [id]`, `/tp a [id]`
- Cancel a request: `/tp cancel [id]`, `/tp cancelar [id]`, `/tp c [id]`
- Includes request authorization checks, offline/null handling, unsafe destination prevention, fixed cancel-message routing, and a `/tp back` expiration race fix.

### Homes

Named player homes with delayed travel, file persistence, and admin teleport support.

- Commands: `/home`, `/casa`
- Add or update a home: `/home add <name>`, `/home create <name>`, `/home make <name>`, `/home crear <name>`
- Travel to a home: `/home travel <name>`, `/home t <name>`, `/home viajar <name>`
- Delete a home: `/home delete <name>`, `/home erase <name>`, `/home borrar <name>`
- Admin teleport format: `/admin homes tp <home>/<player>`
- Includes trimmed name handling, slash rejection for admin path safety, case-insensitive updates, immediate saves, reload cleanup, and missing-world safeguards.

### Graveyard

Death grave system for preserving player inventories and XP after death.

- Commands: `/graveyard`, `/tomb`
- Latest grave lookup: `/graveyard get latest`
- Highest-XP grave lookup: `/graveyard get most-xp`
- Stores grave data in JSON, supports configurable grave limits, scheduled deletion, instant respawn, and dimension toggles for Nether/End.

### Backpack

Per-player backpack storage persisted to JSON.

- Commands: `/backpack`, `/mochila`, `/b`
- Admin inspection is available through the admin command tree.

### Double XP

Configurable XP multiplier and XP visibility controls, with optional AuraSkills integration.

- Commands: `/dxp`, `/exp`
- Enable event: `/dxp enable`, `/dxp e`, `/dxp on`
- Disable event: `/dxp disable`, `/dxp d`, `/dxp off`
- Show current XP: `/dxp get`, `/dxp ver`, `/dxp g`
- Toggle XP notifications: `/dxp optin`, `/dxp verxp`

### Gameplay Enhancements

Small quality-of-life changes loaded through the enhancements module.

- XP bottle storage commands: `/bottle`, `/xpbottle`, `/xpb`
- Store XP: `/xpb store`, `/xpb guardar`, `/xpb save`
- Bookshelf compatibility tweak for allowing non-standard books when configured.

### Admin Tools

Server/admin utility command tree.

- Root command: `/admin`
- Save all plugin-managed data: `/admin save all`
- Reload all plugin-managed data/config: `/admin reload all`
- See a player's inventory: `/admin inventory see <player>`, `/admin inventory peek <player>`
- See a player's backpack: `/admin backpack <player>`
- Teleport to a player's home: `/admin homes tp <home>/<player>`

### Arbiter

Server watcher and restart safeguard controls based on TPS thresholds.

- Commands: `/arbiter`, `/arb`
- Status: `/arbiter info`, `/arbiter i`
- Disable watcher: `/arbiter disable`, `/arbiter d`
- Reload watcher: `/arbiter reload`, `/arbiter r`
- Enable watcher: `/arbiter enable`, `/arbiter e`
- Console debug toggle: `/arbiter debug <true|false>`, `/arbiter db <true|false>`

### Profile, Miscellaneous, And Debug

Additional command modules loaded by the feature loader.

- Profile commands: `/perfil`, `/profile`, including `/profile see <player>` and `/perfil ver <player>`
- Debug commands: `/vpdebug`, `/vpd`
- Debug effect/sound helpers exist for development servers.

## Configuration And Data

Runtime files are written under the plugin data folder.

- `config.yml`: feature toggles and behavior settings.
- `lang.yml`: Spanish player/admin messages.
- `data/homes/homes.json`: saved homes.
- `data/backpack/save.json`: backpack inventories.
- `data/deaths/graves.json`: grave records.

Bundled `config.yml` and `lang.yml` include numeric `version` fields. On startup, VanillaPlus refreshes the runtime copy when the bundled version is newer.

Major config sections:

- `features.admin`
- `features.backpack`
- `features.homes`
- `features.graveyard`
- `features.double-xp`
- `features.enhancements`
- `features.teleport`
- `features.watcher`
- `integration.aura-skills`

## Build And Run

Requirements:

- JDK 25
- Gradle wrapper from this repository

Useful commands:

```bash
./gradlew compileJava
./gradlew test
./gradlew shadowJar
./gradlew runServer
```

Build outputs are written to `server/plugins`. `runServer` is configured to start a Paper **26.1.2** development server.

Main build metadata lives in `build.gradle`:

- `baseVersion = '1.4.0'`
- `paperVersion = "26.1.2"`
- `paperApiVersion = "26.1.2.build.+"`
- `targetJavaVersion = 25`

## Tests

The current test suite uses JUnit Jupiter and MockBukkit 26.1.2.

Run all tests:

```bash
./gradlew test
```

Current test coverage:

- `TeleportUtilsTest`: safe-location checks for null locations/worlds, valid footing/headroom, missing footing, blocked head space, and hazardous blocks.
- `TeleportManagerTest`: regression coverage for the `/tp back` expiration race, ensuring an older expiration cannot remove a newer back location.
- `HomeManagerTest`: home name trimming, slash rejection, case-insensitive updates, max-home enforcement, delete behavior, reload cleanup, and missing-world refusal for player/admin teleports.

## Project Layout

- `src/main/java/dev/ezpadaz/vanillaPlus/VanillaPlus.java`: plugin entry point.
- `src/main/java/dev/ezpadaz/vanillaPlus/Features`: feature modules.
- `src/main/java/dev/ezpadaz/vanillaPlus/Utils`: shared helpers.
- `src/main/resources/plugin.yml`: Paper plugin metadata.
- `src/main/resources/config.yml`: default config.
- `src/main/resources/lang.yml`: default Spanish messages.
- `src/test/java`: MockBukkit/JUnit regression tests.

Feature modules are loaded by `FeatureLoader.loadAll()` and most modules use static `initialize()` and `shutDown()` methods. Commands use Aikar Commands Framework, shaded and relocated into the plugin package by the Shadow build.

## License

MIT.
