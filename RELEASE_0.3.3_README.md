# Chill Zone Shard Shop 0.3.3 — GitHub compiled artifact verification

This is an update to the **existing** Shard Shop repo. The sole Shop command is `/shop`.

When it opens, the chest GUI is titled **Chill Zone — Shop 0.3.3** and contains four categories:
- slot 11 — Deaths
- slot 13 — Effect Bundles
- slot 15 — Server Services
- slot 17 — Upgrades (Netherite Upgrade Smithing Template)

Clicking the fourth button opens pickaxe/axe upgrades. Each is a one-time 1-Shard purchase with confirm/cancel and subsequent ON/OFF choices. Existing Shop services and effect bundle handlers remain in the source.

**Important:** The screenshot supplied by the user shows the old title and three-category menu. That does not match the attached source. The prior workflow only checked that a JAR existed; this release adds `scripts/verify_shop_jar.py`, which checks the *compiled* JAR constants and required GUI class. The workflow will refuse to upload an unverified artifact. This check is not an in-game test and does not prove the server is using the newly built artifact.

## Deployment
Replace files in the same existing Shop GitHub repository, not a new repo. Preserve server config. Run Actions; verify step must pass before the JAR is uploaded. Download and install the verified `chill-zone-shop-0.3.3.jar`, with old Shop JARs removed, then restart. If still showing three options, collect `latest.log` showing which mod initialized and the exact filename of the installed Shop JAR; do not create another renamed ZIP.
