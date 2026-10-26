# Shop 0.2.5 GitHub compilation fix

The reported GitHub errors referenced **UpgradeToggleMenu.java** and **UpgradeService.java**, neither of which exists in this clean source ZIP. They are stale files in your existing GitHub repo. This updated source adds backward compatibility for their calls by providing UpgradeStore.setEnabled(UUID,Upgrade,boolean) and ShopConfig.veinMinerUnlockCost / treeChopperUnlockCost. Both costs default to 1.

For safest migration, replace the GitHub repository source tree with the ZIP `src/` tree (delete stale `src/main/java` before copying), not simply upload new files on top of the old ones. Preserve any separate custom changes you know you need first. This is source only; compile and test on private server.
