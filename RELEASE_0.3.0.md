# Chill Zone Shard Shop 0.3.0 — Four-button /shop menu

**This release adds the fourth Upgrades button to the actual MainShopMenu class.**

- Slot 11: Deaths (original position)
- Slot 13: Effect Bundles (original position)
- Slot 15: Server Services (original position)
- Slot 17: Upgrades (new Netherite Upgrade Smithing Template, far right)
- `/shop upgrades` opens the upgrade menu directly to distinguish an old deployed JAR from a GUI bug.
- `/shop version` reports `0.3.0`.
- The `UpgradeMenu` source has pickaxe and axe choices, one-Shard purchase confirmation, automatic enable, and ON/OFF settings.

## Critical GitHub installation instructions

**Replace the full `src/main` directory with this release's one, rather than merely adding changed files.** If GitHub's web UI won't delete the older Java files, exclude them via the included `build.gradle`, but copying the project into a clean checkout is safer.

In `mods/`, keep exactly one `chillzoneshardshop` mod JAR. Don't remove configuration or balances. If `/shop version` does not show 0.3.0, the server is using the previous mod; do not keep testing the old GUI. Verify the installed JAR's fabric.mod.json ID is `chillzoneshardshop` and version is `0.3.0`.

For a straightforward visual check, use `/shop` and confirm four icons appear. If it still shows three, try `/shop upgrades` to distinguish an old JAR/command collision.

Uncompiled source — not verified against a running Minecraft server.
