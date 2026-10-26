# Shop 0.2.7 — stale upgrade menu compilation fix

The GitHub clone still contains `src/main/java/com/chillzone/shardshop/ui/UpgradeToggleMenu.java`, which imports `UpgradeService`. That class was removed in 0.2.6, making the *older* UI fail Java compilation. The current UI is `UpgradeMenu.java`, not `UpgradeToggleMenu.java`.

**Fix:** `build.gradle` explicitly excludes the obsolete upgrade screens and the original legacy upgrade service classes from the Gradle Java source set, even if old files remain in GitHub. The authoritative classes included here are `UpgradeMenu`, `UpgradeStore` and `HarvestUpgradeService`. The actual Gradle version is raised to 0.2.7, expected JAR `chill-zone-shop-0.2.7.jar`.

Upload this ZIP **to the existing Shop repository** and ensure `build.gradle` and `gradle.properties` are overwritten; otherwise old classes will still compile. Ideally, separately delete old files `UpgradeToggleMenu.java`, `UpgradeService.java` and `BlockUpgradeService.java` from GitHub, but the build exclusions mean deletion is not required for these specific classes.

This source ZIP has been inspected for integrity and references but **is not Minecraft Gradle-compiled or tested in-game**. If any other older Shop Java classes still reference removed APIs, send the complete compile output so all remaining legacy references can be addressed.
