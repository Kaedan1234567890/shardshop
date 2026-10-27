#!/usr/bin/env python3
"""Fail GitHub Actions if the compiled Shop JAR does not contain the requested GUI."""
import json, sys, zipfile
from pathlib import Path
jar = Path(sys.argv[1])
if not jar.is_file(): raise SystemExit(f"ERROR: JAR missing: {jar}")
with zipfile.ZipFile(jar) as z:
    files = set(z.namelist())
    main_name = "com/chillzone/shardshop/ui/MainShopMenu.class"
    entry_name = "com/chillzone/shardshop/ChillZoneShardShop.class"
    upgrade_name = "com/chillzone/shardshop/ui/UpgradeMenu.class"
    for name in (main_name,entry_name,upgrade_name):
        if name not in files: raise SystemExit(f"ERROR: JAR missing class: {name}")
    main=z.read(main_name)
    entry=z.read(entry_name)
    metadata=z.read('fabric.mod.json').decode('utf-8')
    for text in (b'Chill Zone \xe2\x80\x94 Shop 0.3.3',b'netherite_upgrade_smithing_template',b'Upgrades',b'UpgradeMenu',b'Deaths',b'Effect Bundles',b'Server Services'):
        if text not in main: raise SystemExit(f"ERROR: main menu missing compiled constant: {text!r}")
    if b'shop' not in entry or b'MainShopMenu' not in entry: raise SystemExit('ERROR: /shop registration missing')
    # This source registers only one command. Full deployment check still requires server run.
    if '"version": "0.3.3"' not in metadata and '"version":"0.3.3"' not in metadata:
        raise SystemExit('ERROR: fabric.mod.json does not identify 0.3.3')
print('PASS: compiled JAR has /shop entry class, 0.3.3 GUI, all 4 labels, upgrade icon and submenu class.')
