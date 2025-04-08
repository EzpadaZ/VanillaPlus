# VanillaPlus

### This is a Minecraft Paper Plugin for > 1.21.5

This plugin is based on my old `VanillaEnhancer` plugin made for an old server, the old plugin served as a testbed for this one.
You could say `VanillaEnhancer` had to walk so `VanillaPlus` could run.

The reasons for resuming development in a new branch are a few but the most important ones are these:

- The old codebase had an unmantainable architecture
- The old codebase had almost everything tied to everything else, adding a different database for example would be a gigantic effort that i had no time to develop for.
- Configuration parameters were tied to mongo, no mongo == no plugin.

Those were the main reasons i decided to archive the old repo and start this one, wich my only purpose with this is to have:

- Feature Modularization
- Database agnostic (local, local-mongo, local-backend) 
  - local = SQLite Integration
  - local-mongo = The old mongo integration, improved hopefully.
  - local-backend = Send events via POST request to whatever backend service you desire.
- Configuration within the plugin, not database dependant.
- Correct dependency checks instead of doing some vomit-inducing checks like last time.

# Features

#### Old Features that will be finished and or ported from the old system:

- DoubleXP Event with AuraSkills Integration (Done)
- Travel Commands (Could be improved with language agnostic stuff)
- Some GameplayEnhancements.
- ReviveMe Integration (Done)

### New Planned Stuff

- Better Database Integration (SQLite, Mongo and or POST to APIs with events)
- Death Chest
- Backpack System

Hopefully i can finish this project this time.