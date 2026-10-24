package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelCalls;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields;

/**
 * 1.21.1 HumanoidModel members 1.21.2 dropped (see CbRenderModelFields):
 * the public pose fields rightArmPose / leftArmPose (field_3395 / field_3399, initially EMPTY as 1.21.1's constructor
 * set them), swimAmount (field_3396), crouching (field_3400); copyPropertiesTo(HumanoidModel); rotlerpRad;
 * setupAttackAnimation(LivingEntity, ageInTicks). While a 1.21.1 call runs 1.21.2 code on this model
 * (CbRenderModelCalls.legacy) getArmPose answers the put-back arm poses, as 1.21.1's setupAnim read the fields.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_572.class)
public abstract class CbRenderHumanoidMixin implements CbRenderModelFields.Humanoid, CbRenderModelFields.HumanoidAttack {

    public net.minecraft.class_572.class_573 field_3395 = net.minecraft.class_572.class_573.field_3409;

    public net.minecraft.class_572.class_573 field_3399 = net.minecraft.class_572.class_573.field_3409;

    public float field_3396;

    public boolean field_3400;

    @Shadow
    protected abstract net.minecraft.class_572.class_573 method_62081(net.minecraft.class_10034 state, net.minecraft.class_1306 arm);

    @Shadow
    protected abstract void method_29353(net.minecraft.class_10034 state, float ageInTicks);

    @Shadow
    public abstract void method_64254(net.minecraft.class_572<?> other);

    @Inject(method = "method_62081", at = @At("HEAD"), cancellable = true)
    private void cb$legacyArmPose(net.minecraft.class_10034 state, net.minecraft.class_1306 arm,
                                  CallbackInfoReturnable<net.minecraft.class_572.class_573> cir) {
        if (CbRenderModelCalls.legacy(this)) {
            cir.setReturnValue(arm == net.minecraft.class_1306.field_6183 ? this.field_3395 : this.field_3399);
        }
    }

    @Override
    public net.minecraft.class_572.class_573 cb$rightArmPose() {
        return this.field_3395;
    }

    @Override
    public net.minecraft.class_572.class_573 cb$leftArmPose() {
        return this.field_3399;
    }

    @Override
    public void cb$armPoses(net.minecraft.class_572.class_573 right, net.minecraft.class_572.class_573 left) {
        this.field_3395 = right;
        this.field_3399 = left;
    }

    @Override
    public float cb$swimAmount() {
        return this.field_3396;
    }

    @Override
    public void cb$swimAmount(float swimAmount) {
        this.field_3396 = swimAmount;
    }

    @Override
    public boolean cb$crouching() {
        return this.field_3400;
    }

    @Override
    public void cb$crouching(boolean crouching) {
        this.field_3400 = crouching;
    }

    @Override
    public net.minecraft.class_572.class_573 cb$w$armPose(net.minecraft.class_10034 state, net.minecraft.class_1306 arm) {
        return this.method_62081(state, arm);
    }

    @Override
    public void cb$w$attack(net.minecraft.class_10034 state, float ageInTicks) {
        this.method_29353(state, ageInTicks);
    }

    /**
     * 1.21.1 copyPropertiesTo(other): EntityModel's (attackTime / riding / young), the arm poses and crouching, and
     * the pose of head, hat, body, arms and legs. 1.21.2's copyPropertiesTo copies the same parts but the hat, which
     * 1.21.2 made a child of the head: copied here as 1.21.1 did (its own pose, now relative to the head's, as the
     * hat's layer definition places it).
     */
    public void method_2818(net.minecraft.class_572<?> other) {
        CbRenderModelFields.Pose from = (CbRenderModelFields.Pose) this;
        ((CbRenderModelFields.Pose) other).cb$pose(from.cb$attackTime(), from.cb$riding(), from.cb$young());
        CbRenderModelFields.Humanoid to = (CbRenderModelFields.Humanoid) other;
        to.cb$armPoses(this.field_3395, this.field_3399);
        to.cb$crouching(this.field_3400);
        this.method_64254(other);
        other.field_3394.method_17138(((net.minecraft.class_572<?>) (Object) this).field_3394);
    }

    /** 1.21.1 rotlerpRad(progress, from, to): from + progress * the shortest signed angle from -> to */
    protected float method_2804(float progress, float from, float to) {
        float d = (to - from) % 6.2831855F;
        if (d < -3.1415927F) {
            d += 6.2831855F;
        }
        if (d >= 3.1415927F) {
            d -= 6.2831855F;
        }
        return from + progress * d;
    }

    /**
     * 1.21.1 setupAttackAnimation(entity, ageInTicks): 1.21.2's setupAttackAnimation(state, ageInTicks) on a state of
     * the entity carrying this model's attackTime (CbRenderModelCalls.pose), as vanilla's own.
     */
    protected void method_29353(net.minecraft.class_1309 entity, float ageInTicks) {
        CbRenderModelCalls.pose(this, entity, net.minecraft.class_10034.class,
            s -> this.method_29353((net.minecraft.class_10034) s, ageInTicks));
    }
}
