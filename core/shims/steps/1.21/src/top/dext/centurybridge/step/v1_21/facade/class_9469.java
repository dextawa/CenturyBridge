package top.dext.centurybridge.step.v1_21.facade;

import java.util.Optional;

/**
 * 1.20.6's WindBurstEnchantment (net/minecraft/class_9469): neither traded nor found; after the mace hits,
 * a wind burst at the attacker (knockback 0.25 + 0.25 per level, none for a flying player), with
 * 1.20.6's body. Gated behind update_1_21 in 1.20.6, base game in 1.21 (see class_9467). 1.20.6's
 * explosion interaction BLOW is 1.21's TRIGGER (the same constant renamed: what a wind charge's burst
 * does to blocks), and 1.20.6's AbstractWindCharge.WindChargeDamageCalculator, gone in 1.21, is rebuilt
 * below with its bodies.
 */
public class class_9469 extends class_1887 {

    public class_9469() {
        super(class_1887.method_58674(net.minecraft.class_3489.field_50109, 2, 3, class_1887.method_58441(15, 9),
            class_1887.method_58441(65, 9), 4, net.minecraft.class_7701.field_40183, net.minecraft.class_1304.field_6173));
    }

    /** doPostItemStackHurt(LivingEntity attacker, Entity target, int level) */
    @Override
    public void method_58675(net.minecraft.class_1309 attacker, net.minecraft.class_1297 target, int level) {
        float power = 0.25F + 0.25F * (float) level;
        attacker.method_37908().method_8454(null, null, new BurstCalculator(power), attacker.method_23317(), attacker.method_23318(),
            attacker.method_23321(), 3.5F, false, net.minecraft.class_1937.class_7867.field_51779, net.minecraft.class_2398.field_49139,
            net.minecraft.class_2398.field_49140, net.minecraft.class_3417.field_49044);
    }

    @Override
    public boolean method_25949() {
        return false;
    }

    @Override
    public boolean method_25950() {
        return false;
    }

    /** 1.20.6's WindBurstEnchantment.WindBurstEnchantmentDamageCalculator over AbstractWindCharge.WindChargeDamageCalculator */
    private static final class BurstCalculator extends net.minecraft.class_5362 {
        private final float knockBackPower;

        BurstCalculator(float knockBackPower) {
            this.knockBackPower = knockBackPower;
        }

        /** WindChargeDamageCalculator.shouldDamageEntity: a wind burst hurts nobody */
        @Override
        public boolean method_55504(net.minecraft.class_1927 explosion, net.minecraft.class_1297 entity) {
            return false;
        }

        /** WindChargeDamageCalculator.getBlockExplosionResistance: blocks tagged to stop wind charges stop it */
        @Override
        public Optional<Float> method_29555(net.minecraft.class_1927 explosion, net.minecraft.class_1922 level, net.minecraft.class_2338 pos,
                net.minecraft.class_2680 state, net.minecraft.class_3610 fluid) {
            return state.method_26164(net.minecraft.class_3481.field_49051) ? Optional.of(3600000.0F) : Optional.empty();
        }

        /** getKnockbackMultiplier: none for a flying player */
        @Override
        public float method_57007(net.minecraft.class_1297 entity) {
            boolean flying = entity instanceof net.minecraft.class_1657 player && player.method_31549().field_7479;
            return flying ? 0.0F : knockBackPower;
        }
    }
}
