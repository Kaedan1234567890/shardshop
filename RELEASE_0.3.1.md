# Chill Zone Shard Shop 0.3.1 — Four-category main GUI

- The **plain `/shop` command** opens `MainShopMenu` with exactly four buttons: Deaths at slot 11, Effect Bundles 13, Server Services 15, and Upgrades 17 (far right of middle row).
- The new Upgrades button (Netherite Upgrade Smithing Template) opens `UpgradeMenu`: diamond pickaxe = Vein Miner; diamond axe = Tree Chopper, 1 Shard each, confirmation, then permanent ON/OFF toggles.
- The GUI **title now says `Chill Zone — Shop 0.3.1`**. This is a hard visual check of the installed main-menu build. If it says `Chill Zone — Shop` without the version, this JAR has not been loaded or another plugin/mod owns `/shop`.
- No `/shop upgrades` or `/shop version` subcommands are needed; `/shop` is the intended entry point.
- Other menu classes (Deaths, Effect Bundles, Server Services) are retained in this source tree.
- Expected GitHub JAR `build/libs/chill-zone-shop-0.3.1.jar`; replace the OLD JAR, do not leave both.
- Compilation and in-server functionality require the GitHub Actions build and real server testing. The source ZIP alone is **NOT INSTALLABLE**.
