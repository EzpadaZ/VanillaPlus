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
- Configuration within the plugin, not database dependant.
- Correct dependency checks instead of doing some vomit-inducing checks like last time.

# Features

- DoubleXP Event with AuraSkills Integration (Done)
- Travel Commands (Could be improved with language agnostic stuff)
- Some GameplayEnhancements.
- ~~ReviveMe Integration (Done)~~ Replaced by Graveyard System
- ~~Better Database Integration (SQLite, Mongo and or POST to APIs with events)~~
  - This was scrapped as i dont see it as a vital feature for the plugin to have and the ROI i get from it is _low_
- ~~Death Chest~~ **Graveyard System** [**DONE**]
- ~~Backpack System~~ Mojang Added Bags to the game, having a backpack is too overkill.
- Admin system to check inventories (usefull when running an SMP and having theft, to check players0) and other silly stuff i can think of.

I am still thinking of stuff to add to the plugin to manage my own server, i'll probably do a lang.yml file since right now the language is spanish (due to the server being in spanish).
So that's on the list of stuff to do.