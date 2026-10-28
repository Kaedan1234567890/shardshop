# Shop 0.3.6 — four visible icons

This is the entire replacement source repository, not a compiled JAR.
The `/shop` GUI now displays **Deaths / Effect Bundles / Server Services / Upgrades** at slots **10 / 12 / 14 / 16** in the 27-slot chest.
Upgrades is a netherite upgrade smithing template and opens a blank 27-slot placeholder. No older mining upgrade implementation is included.

**Proving which JAR is running:** This build's main GUI title is `Chill Zone — Shop FOUR 0.3.6`. Startup console prints `Shop 0.3.6 LOADED - /shop FOUR icons in slots 10,12,14,16`.
If the server still shows `Chill Zone — Shop` and only three icons, the **new Shop JAR is not the active code** (likely an old jar, a duplicate jar, or build artifact). Do not change the menu layout again; find which jar registered `/shop`.

Upload the extracted files, including `.github`, to the repository, build through GitHub Actions, download `chill-zone-shop-0.3.6.jar`, remove previous versions of Chill Zone Shop from the server `mods` folder, and install only the new JAR. Keep existing config and data files.

**Status:** Source audit and package verification only; this environment cannot compile Minecraft 26.3 against Fabric dependencies, and the user must confirm the JAR is actually loaded.
