package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * 1.20.4 AbstractArrow.deflect(), which 1.20.5 replaced by Projectile.deflect(ProjectileDeflection, ..):
 * back with its 1.20.4 body (turn the arrow's motion by a random yaw, halve it, turn the arrow with it),
 * for 1.20.4 calls and a mod override's super.deflect(). 1.20.4 called it when the arrow hit an entity
 * that deflects arrows; mixin/CbRestProjectileMixin calls it there again for a mod class overriding it, and for
 * any arrow when a converted mod mixes it (route, CbMixedMembers).
 * Extends Entity for the inherited protected random.
 */
@Mixin(net.minecraft.class_1665.class)
public abstract class CbRestAbstractArrowMixin extends net.minecraft.class_1297 implements CbRoutes.ArrowDeflect {

    protected CbRestAbstractArrowMixin(net.minecraft.class_1299<?> type, net.minecraft.class_1937 level) {
        super(type, level);
    }

    /** was deflect(): 1.20.4's body */
    @Override
    @CbVanillaReads("CbRestProjectileMixin (cbRest$legacyDeflect): hitTargetOrDeflectSelf deflects an arrow through it, as 1.20.4's onHitEntity did for an entity that deflects arrows, when a converted mod mixes it")
    public void method_55052() {
        float turn = this.field_5974.method_43057() * 360.0F;
        this.method_18799(this.method_18798().method_1024(turn * 0.017453292F).method_1021(0.5));
        this.method_36456(this.method_36454() + turn);
        this.field_5982 += turn;
    }
}
