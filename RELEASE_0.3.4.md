# Chill Zone Shard Shop 0.3.4 — rebuilt Upgrades

Only the upgrade classes were deleted and rebuilt: UpgradeStore, UpgradeMenu and HarvestUpgradeService. Existing Deaths, Bundle Effects, Server Services Java files are unchanged. /shop is the only command. The normal GUI has Deaths slot 11, Effects slot 13, Services slot 15 and Upgrades slot 17 (zero-indexed Java inventory positions).

This is a source-only release. Build and runtime behavior must be verified. Replace the old project `src/main` with this one; do not layer old classes. Save a copy of the server beforehand.
