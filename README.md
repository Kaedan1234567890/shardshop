# Chill Zone Shard Shop 0.1.0-alpha

Server-side Fabric mod for Minecraft 26.2.

## Features
- `/shardshop` opens a 6x9 Chill Zone chest GUI.
- Uses the **same shard balance as Chill Zone Homes** through its public shard store at runtime. It does not create a second currency.
- Saves the player's **28 most recent deaths** in `config/chill-zone-shard-shop-deaths.json`.
- Newest entry is **Last Death**, then Death 2 through Death 28.
- Oldest death rolls off when a 29th death is recorded.
- Death entries show dimension, exact coordinates, timestamp, cause and teleport cost.
- Death teleport costs **50 Shards** by default.
- Exact death location is tried first. If unsafe, the mod searches only within a maximum **10 block horizontal radius** and limited vertical range.
- If no nearby safe location is found, teleport is cancelled and the player is **not charged**.
- Using a death teleport does **not** delete the death record.
- Includes a configurable preconfigured mob-spawner shop.
- Transactions are logged to `config/chill-zone-shard-shop-transactions.log`.

## Config
On first launch, `config/chill-zone-shard-shop.json` is created. Default values:
- deathTeleportCost: 50
- safeTeleportRadius: 10
- safeTeleportVerticalRadius: 4
- starter spawner prices are included and can be changed without rebuilding the mod.

The initial spawner prices are placeholders for balancing; edit the JSON whenever the final Chill Zone prices are decided.

## Required mod
This mod requires Chill Zone Homes because Homes owns the shard balance.

## Build
Push the source to GitHub and run the included GitHub Actions workflow, or build with Java 25 and Gradle 9.5.1:

`gradle build`
