package cb.clienttest;

import net.fabricmc.api.ModInitializer;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1810;
import net.minecraft.class_1829;
import net.minecraft.class_1832;
import net.minecraft.class_1856;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

/**
 * Registers a 1.20.1 mod's tools on its own Tier (the 26.2 tool rework removed Tier, TieredItem,
 * PickaxeItem and SwordItem): a pickaxe and a sword, with an item model and a name in the mod's assets.
 * A failure is kept for the tally instead of stopping the game, so the other checks still run.
 */
public class ItemsInit implements ModInitializer {
    static final int USES = 300;
    static final float SPEED = 7.0f;
    static final float DAMAGE_BONUS = 2.0f;
    static final int SWORD_DAMAGE = 3; // SwordItem damage = this + the tier's bonus
    static class_1792 pickaxe, sword;
    static Throwable failure;

    /** iron-level tier with its own numbers, so the checks tell it from vanilla's */
    public static class CbTier implements class_1832 {
        @Override
        public int method_8025() {
            return USES;
        }

        @Override
        public float method_8027() {
            return SPEED;
        }

        @Override
        public float method_8028() {
            return DAMAGE_BONUS;
        }

        @Override
        public int method_8024() {
            return 2;
        }

        @Override
        public int method_8026() {
            return 14;
        }

        @Override
        public class_1856 method_8023() {
            return class_1856.method_8091(class_1802.field_8620);
        }
    }

    /** PickaxeItem's constructor is protected in 1.20.1: mods subclass it */
    public static class CbPickaxe extends class_1810 {
        public CbPickaxe(class_1832 tier, int damage, float speed, class_1792.class_1793 props) {
            super(tier, damage, speed, props);
        }
    }

    @Override
    public void onInitialize() {
        try {
            CbTier tier = new CbTier();
            pickaxe = class_2378.method_10230(class_7923.field_41178, new class_2960("cb_clienttest", "cb_pickaxe"),
                new CbPickaxe(tier, 1, -2.8f, new class_1792.class_1793()));
            sword = class_2378.method_10230(class_7923.field_41178, new class_2960("cb_clienttest", "cb_sword"),
                new class_1829(tier, SWORD_DAMAGE, -2.4f, new class_1792.class_1793()));
        } catch (Throwable t) {
            failure = t;
            t.printStackTrace();
        }
    }
}
