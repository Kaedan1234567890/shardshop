import sys,zipfile
path=sys.argv[1]
with zipfile.ZipFile(path) as jar:
    files=set(jar.namelist())
    required={'com/chillzone/shardshop/ui/MainShopMenu.class','com/chillzone/shardshop/ui/UpgradePlaceholderMenu.class','com/chillzone/shardshop/ui/DeathMenu.class','com/chillzone/shardshop/ui/EffectBundleMenu.class','com/chillzone/shardshop/ui/ServiceMenu.class'}
    assert required <= files, f'Missing classes: {required-files}'
    main=jar.read('com/chillzone/shardshop/ui/MainShopMenu.class')
    for token in (b'Upgrades',b'netherite_upgrade_smithing_template',b'UpgradePlaceholderMenu',b'0.3.5'):
        assert token in main, f'Main menu lacks {token!r}'
    assert not any('HarvestUpgradeService' in f or 'UpgradeStore' in f or f.endswith('/UpgradeMenu.class') for f in files), 'Old upgrade code still compiled'
print('PASS: Four-category GUI and placeholder compiled; legacy mining upgrades absent.')
