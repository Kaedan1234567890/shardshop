package com.chillzone.shardshop;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public enum BundleType {
    MINERS_FOCUS("Miner's Focus", "Fast underground mining.", 15 * 60, 30,
        List.of(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 60 * 20, 3), new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 60 * 20, 0)), null),
    DEEP_DIVER("Deep Diver", "Underwater building and movement.", 15 * 60, 30,
        List.of(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 15 * 60 * 20, 0), new MobEffectInstance(MobEffects.CONDUIT_POWER, 15 * 60 * 20, 0), new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 60 * 20, 0)), null),
    FORTUNES_FAVOR("Fortune's Favor", "Luck V for exploration and loot.", 10 * 60, 35,
        List.of(new MobEffectInstance(MobEffects.LUCK, 10 * 60 * 20, 4)), null),
    SCHOLARS_BLESSING("Scholar's Blessing", "Earn 1.5x experience.", 20 * 60, 35, List.of(), PerkStore.Perk.SCHOLARS_BLESSING),
    BUILDERS_FOCUS("Builder's Focus", "Fast, mobile building support.", 15 * 60, 35,
        List.of(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 60 * 20, 1), new MobEffectInstance(MobEffects.JUMP, 15 * 60 * 20, 1), new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 60 * 20, 0), new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 60 * 20, 0)), null),
    VOID_WALKER("Void Walker", "Safer End exploration.", 10 * 60, 25,
        List.of(new MobEffectInstance(MobEffects.SLOW_FALLING, 10 * 60 * 20, 0), new MobEffectInstance(MobEffects.NIGHT_VISION, 10 * 60 * 20, 0)), null),
    NETHER_WORKER("Nether Worker", "Temporary Nether work protection.", 15 * 60, 30,
        List.of(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 60 * 20, 0), new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 60 * 20, 0)), null),
    PROSPECTOR("Prospector", "Adds +2 effective Fortune on qualifying ore drops, capped at Fortune V.", 10 * 60, 50, List.of(), PerkStore.Perk.PROSPECTOR);

    public final String display;
    public final String description;
    public final int seconds;
    public final int defaultCost;
    public final List<MobEffectInstance> effects;
    public final PerkStore.Perk customPerk;

    BundleType(String display, String description, int seconds, int defaultCost, List<MobEffectInstance> effects, PerkStore.Perk customPerk) {
        this.display = display;
        this.description = description;
        this.seconds = seconds;
        this.defaultCost = defaultCost;
        this.effects = effects;
        this.customPerk = customPerk;
    }

    public int cost() {
        ShopConfig c = ChillZoneShardShop.config();
        return switch (this) {
            case MINERS_FOCUS -> c.minersFocusCost;
            case DEEP_DIVER -> c.deepDiverCost;
            case FORTUNES_FAVOR -> c.fortunesFavorCost;
            case SCHOLARS_BLESSING -> c.scholarsBlessingCost;
            case BUILDERS_FOCUS -> c.buildersFocusCost;
            case VOID_WALKER -> c.voidWalkerCost;
            case NETHER_WORKER -> c.netherWorkerCost;
            case PROSPECTOR -> c.prospectorCost;
        };
    }
}
