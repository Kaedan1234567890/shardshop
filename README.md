# Chill Zone Shard Shop — 0.1.0-alpha-fix2

Server-side Fabric mod for Minecraft 26.2.

## First-release design

`/shardshop` opens a regular 3-row chest GUI with a decorative glass border.
The only category in this release is **Deaths**, represented by a Skeleton Skull
in the exact centre of the menu.

Clicking **Deaths** opens a 6-row double chest containing the player's rolling
history of up to 28 deaths. Each saved death is represented by a Skeleton Skull.
Empty death positions remain empty and a skull appears automatically when a new
death is recorded.

Newest-to-oldest labels are:
- Last Death
- Death 2
- Death 3
- ...
- Death 28

When death 29 is recorded, the previous Death 28 ages out.

## Death teleport

- Cost: 50 shards per use by default.
- Uses the existing Chill Zone Homes shard balance; no second currency is made.
- Exact death position is tried first.
- If unsafe, the mod searches nearby only, up to 10 blocks horizontally by default.
- If no safe destination is found nearby, the teleport is cancelled and no shards
  are charged.
- If charging succeeds but the teleport itself errors, the shards are refunded.
- Using a death teleport does not delete the saved death record.

## Persistence

Death history is stored in:
`config/chill-zone-shard-shop-deaths.json`

Config is stored in:
`config/chill-zone-shard-shop.json`

Transactions are logged in:
`config/chill-zone-shard-shop-transactions.log`

## fix1

Removed the unused spawner shop and its SpawnerFactory code. This also removes
 the Minecraft 26.2 `BLOCK_ENTITY_DATA` compile error from the original alpha.
Changed the main shop to a regular chest with a centred Skeleton Skull for Deaths,
and changed every saved death icon to a Skeleton Skull.

## Fix2 clean-source note
This release intentionally contains **no spawner shop code**. The only shop category is **Deaths**.

If upgrading a GitHub repository that previously contained the alpha version, delete these obsolete files from the repository before building if they are still present:
- `src/main/java/com/chillzone/shardshop/SpawnerFactory.java`
- `src/main/java/com/chillzone/shardshop/ui/SpawnerMenu.java`

Git does not delete old files merely because a newer ZIP does not contain them. A clean replacement of the repository contents avoids compiling obsolete spawner classes.


## Fix 3 — dimension death teleport
- Keeps the existing `/shardshop` GUI, death slots, skull layout, Last Death ordering, 50-shard price, and saved history unchanged.
- Death skull lore now explicitly displays the saved death dimension (Overworld, The Nether, or The End).
- Safe-location checks now load the destination chunk before validating it, allowing saved deaths in another dimension to be checked and teleported to correctly.
- Nether-roof deaths are accepted when the recorded spot (or a nearby spot within the configured safety radius) has solid ground and enough empty space.
- End deaths use the same local safety rule and will not select unsupported air over the void.
- If no safe location exists near the saved death, teleport is cancelled and no shards are charged.
