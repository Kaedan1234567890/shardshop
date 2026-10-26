# Chill Zone Shard Shop 0.2.9 — verifiable changes

This modifies the existing Shard Shop, not a separate mod.

Changed Java source:
- `ChillZoneShardShop.java`: adds `/shop version` and logs running 0.2.9 to console; retains `/shop`, `/shardshop`, and all existing categories/services.
- `UpgradeMenu.java`: preserves the 1-Shard purchase -> automatically ON -> later ON/OFF menu, adds confirmation after purchase, refunds in the event a purchase cannot complete, and explains the permanent toggle in the selection lore.
- `gradle.properties`: mod_version is 0.2.9 (actual JAR version).

Existing functionality present in the archive:
- Death teleport and records, 9 effect bundles, Server Services, Vein Miner / Tree Chopper upgrades; `HarvestUpgradeService` registers real block-break handlers. This does not prove those handlers work in-game.

**GitHub existing-repository warning:** GitHub Web upload does NOT delete files. For a clean source replacement, remove obsolete `src/main/java/com/chillzone/shardshop/UpgradeService.java`, `BlockUpgradeService.java`, `ui/UpgradeToggleMenu.java` and `ui/UpgradeConfirmMenu.java` from your repository. build.gradle also excludes them for compatibility.

**Verification:** File integrity and source checks only. No Minecraft 26.3 Gradle compile or server-side gameplay test here. Test /shop version after installing to ensure the right JAR is actually running. Keep only one Shop mod JAR in mods.
