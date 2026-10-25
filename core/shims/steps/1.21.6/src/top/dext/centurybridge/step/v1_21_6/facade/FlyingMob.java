package top.dext.centurybridge.step.v1_21_6.facade;

/**
 * 1.21.5's FlyingMob (class_1307), which 1.21.6 removed: its two vanilla subclasses (Ghast, Phantom) extend Mob there
 * and move through LivingEntity.travelFlying. Members as 1.21.5's (javap): no fall damage, never on a ladder, and
 * travel moving through water (0.02, then x0.8), lava (0.02, then x0.5) or air. 1.21.6's travelFlying(v, 0.02) is the
 * same in water, lava and in the air, but on the ground it no longer takes the block's friction (1.21.5: friction =
 * the block's friction x 0.91, moveRelative(0.1 x 0.16277137 / friction^3), then x friction), so travel is 1.21.5's
 * body on 1.21.6's members.
 */
public abstract class FlyingMob extends net.minecraft.class_1308 {

    public FlyingMob(net.minecraft.class_1299<? extends FlyingMob> type, net.minecraft.class_1937 level) {
        super(type, level);
    }

    /**
     * Mob's addAdditionalSaveData / readAdditionalSaveData, unchanged: declared here only because the layer's access
     * widener opens Entity's (CbValueIoGen calls them), and javac refuses a class inheriting Mob's protected overrides
     * of members that are public on Entity. At run time this is Mob's method.
     */
    @Override
    public void method_5652(net.minecraft.class_11372 output) {
        super.method_5652(output);
    }

    @Override
    public void method_5749(net.minecraft.class_11368 input) {
        super.method_5749(input);
    }

    /** checkFallDamage: none (protected in 1.21.5; a facade member is public, access wideners never reach the layer) */
    @Override
    public void method_5623(double y, boolean onGround, net.minecraft.class_2680 state, net.minecraft.class_2338 pos) {
    }

    /** travel(Vec3) */
    @Override
    public void method_6091(net.minecraft.class_243 input) {
        if (this.method_5799()) {
            this.method_5724(0.02F, input);
            this.method_5784(net.minecraft.class_1313.field_6308, this.method_18798());
            this.method_18799(this.method_18798().method_1021(0.800000011920929D));
        } else if (this.method_5771()) {
            this.method_5724(0.02F, input);
            this.method_5784(net.minecraft.class_1313.field_6308, this.method_18798());
            this.method_18799(this.method_18798().method_1021(0.5D));
        } else {
            float friction = 0.91F;
            if (this.method_24828()) {
                friction = this.method_37908().method_8320(this.method_23314()).method_26204().method_9499() * 0.91F;
            }
            float speed = 0.16277137F / (friction * friction * friction);
            friction = 0.91F;
            if (this.method_24828()) {
                friction = this.method_37908().method_8320(this.method_23314()).method_26204().method_9499() * 0.91F;
            }
            this.method_5724(this.method_24828() ? 0.1F * speed : 0.02F, input);
            this.method_5784(net.minecraft.class_1313.field_6308, this.method_18798());
            this.method_18799(this.method_18798().method_1021(friction));
        }
    }

    /** onClimbable: never */
    @Override
    public boolean method_6101() {
        return false;
    }
}
