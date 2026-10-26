# Chill Zone Shop 0.2.6 - Build compatibility fix

- Changed **actual project version** to 0.2.6 and corrected JAR basename to `chill-zone-shop` (not Homes).
- Added `UpgradeStore.unlocked(UUID, Upgrade)` and `unlock(UUID, Upgrade)` compatibility for GitHub leftover `UpgradeService.java`.
- Added `veinMinerMaxBlocks` and `treeChopperMaxBlocks` config fields for GitHub leftover `BlockUpgradeService.java`.
- Excluded both stale legacy service classes from compilation; only `HarvestUpgradeService` is active.
- Restored missing `fabric.mod.json` so this ZIP is a self-contained Fabric source project.
- Existing purchases and ON/OFF state remain in `config/chill-zone-shop-upgrades.json`.

**GitHub:** In the existing Shop repo, replace the project files with the entire ZIP contents, including `build.gradle`, `gradle.properties`, `settings.gradle`, `src/main/resources` and `src/main/java`. Delete old `UpgradeService.java` and `BlockUpgradeService.java` from the repo when convenient; the Gradle exclusions protect builds if they remain.

**Validation:** Source inspected; archive checked. Full Gradle/Fabric compilation and actual Minecraft test NOT completed in this environment.
