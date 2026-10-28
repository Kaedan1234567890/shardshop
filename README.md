# Chill Zone Shard Shop 0.3.5 — clean four-button baseline

This is a **source project**, not an installable JAR. Build with GitHub Actions.

- `/shop` is the only command registered by this mod.
- Slot 11: Deaths; slot 13: Effect Bundles; slot 15: Server Services — original implementation retained.
- Slot 17: Netherite Upgrade Smithing Template called **Upgrades**.
- Click Upgrades to open a deliberately empty 27-slot chest. There is **no** purchase/toggle/harvesting system in this build.
- No old Vein Miner or Tree Chopper classes are included; next phase will integrate existing mods.

**Important GitHub deployment:** Completely empty the OLD repository contents (including old Java files; do not delete the repo itself), then upload **all** files including `.github/workflows/build.yml`. Otherwise old Java sources can be compiled or old artifacts deployed.

This archive passed structural source checks but has **not** been compiled or validated in game here.
