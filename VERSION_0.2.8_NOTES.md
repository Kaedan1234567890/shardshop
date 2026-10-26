# Chill Zone Shard Shop 0.2.8 — ADDITIONS TO EXISTING SHOP

- Existing /shop category slots preserved: Deaths (10), Effect Bundles (12), Server Services (14); Upgrades is the fourth button (16).
- 9 existing effect bundles and Deaths, repair/rename/curse/XP services source is retained.
- Upgrades: pickaxe and axe; each one-time 1-Shard default purchase with red Cancel / green Confirm.
- Bought upgrades are automatically enabled and have separate gray OFF / lime ON dye controls on subsequent clicks. Saved in config/chill-zone-shop-upgrades.json.
- Vein Miner and Tree Chopper block break hooks retained and gated by bought+enabled; connected 3x3 neighbourhood bounded traversal.
- Scheduled leaf removal now skips persistent or still-log-connected leaves instead of forcibly removing all nearby leaves.
- This is SOURCE ONLY; uncompiled and not yet tested in Minecraft. This cannot be a guarantee that every old GitHub source was removed: replace the existing Shop src/main tree and remove obsolete upgrade classes.
- Keep the existing server config and shard balances; replace only old Shop JAR after a successful build.
