# Chill Zone Shop 0.2.0-alpha-shop-expansion1

Replacement/upgrade source for the existing Chill Zone Shard Shop on Minecraft 26.2.

## Main command
- `/shop` opens the new main GUI for every player.
- `/shardshop` remains as a compatibility alias.

## Categories
### Deaths
- Existing rolling 28-death history is preserved.
- Existing `config/chill-zone-shard-shop-deaths.json` is reused; no reset/migration deletes records.
- Death teleport now costs 15 Shards. Old saved defaults of 20/25/50 are migrated to 15.

### Effect Bundles
- Miner's Focus: Haste IV + Night Vision, 15m, 30 Shards.
- Deep Diver: Dolphin's Grace + Conduit Power + Night Vision, 15m, 30 Shards.
- Fortune's Favor: Luck V, 5m, 35 Shards. Vanilla Luck mainly improves fishing loot odds.
- Scholar's Blessing: 1.5x XP, 20m, 35 Shards.
- Warrior's Fury: Strength III + Speed II + Fire Resistance, 3m, 65 Shards.
- Builder's Focus: Haste II + Jump Boost II + Speed I + Night Vision, 15m, 35 Shards.
- Void Walker: Slow Falling + Night Vision, 10m, 25 Shards.
- Nether Worker: Fire Resistance + Night Vision, 15m, 30 Shards.
- Prospector: +2 effective Fortune on block drops, capped at Fortune V, 10m, 50 Shards.

Bundle purchases use a confirmation screen. Rebuying refreshes the defined effect/perk; it does not stack amplifiers.
Custom timed perks are persisted in `config/chill-zone-shard-shop-perks.json`.

### Server Services
- Item Repair: read-only inventory preview; price scales from 5 to 150 Shards by missing durability.
- Remove Curse: read-only inventory preview; removes Binding/Vanishing for 25 Shards per curse.
- Item Rename: read-only item selection, then Java anvil text entry or Bedrock Floodgate/Cumulus form; 5 Shards.
- XP Recovery: recovers up to 50% of XP from the most recent actual death, one claim per death, 25 Shards. It never raises the player above their recorded pre-death total.

## Safety / anti-abuse
- GUI item copies are never transferable.
- Repair/curse/rename revalidate the real inventory slot against an item fingerprint immediately before charging.
- Charge happens only after validation; recoverable failures refund Shards.
- XP Recovery is keyed to the latest death timestamp and persisted as claimed before awarding XP.
- Prospector computes only the positive difference between normal drops and the boosted Fortune loot result, avoiding a second full loot roll.
- Shard currency remains the exact existing Chill Zone Homes balance through `ShardBridge`; no second currency exists.

## 0.2.2 Minecraft 26.2 compile fix
This package has the actual source edits applied in `BundleType.java`:
- `MobEffects.DIG_SPEED` -> `MobEffects.HASTE`
- `MobEffects.JUMP` -> `MobEffects.JUMP_BOOST`
- `MobEffects.MOVEMENT_SPEED` -> `MobEffects.SPEED`
