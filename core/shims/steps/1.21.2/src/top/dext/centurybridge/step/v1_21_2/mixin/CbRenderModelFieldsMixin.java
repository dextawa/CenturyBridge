package top.dext.centurybridge.step.v1_21_2.mixin;

import java.lang.ref.WeakReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelCalls;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields;

/**
 * 1.21.1 EntityModel's public attackTime / riding / young (field_3447 / field_3449 / field_3448), gone in 1.21.2 (the
 * render state carries them): put back as the plain fields a 1.21.1 model and its renderer read and write. 1.21.1's
 * initial values: young = true (EntityModel's constructor), the others zero. See CbRenderModelFields.
 * Also 1.21.1's copyPropertiesTo, and what CbRenderModelCalls needs: 1.21.2's setupAnim publicly, and the partial tick
 * of the latest prepareMobModel.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_583.class)
public abstract class CbRenderModelFieldsMixin implements CbRenderModelFields.Pose, CbRenderModelCalls.Model {

    public float field_3447;

    public boolean field_3449;

    public boolean field_3448 = true;

    @Unique
    private WeakReference<net.minecraft.class_1297> cb$preparedEntity;

    @Unique
    private float cb$preparedPartialTick;

    @Shadow
    public abstract void method_2819(net.minecraft.class_10017 state);

    @Override
    public void cb$pose(float attackTime, boolean riding, boolean young) {
        this.field_3447 = attackTime;
        this.field_3449 = riding;
        this.field_3448 = young;
    }

    @Override
    public float cb$attackTime() {
        return this.field_3447;
    }

    @Override
    public boolean cb$riding() {
        return this.field_3449;
    }

    @Override
    public boolean cb$young() {
        return this.field_3448;
    }

    /** 1.21.1 EntityModel.copyPropertiesTo(other): other.attackTime / riding / young = this model's */
    public void method_17081(net.minecraft.class_583<?> other) {
        ((CbRenderModelFields.Pose) other).cb$pose(this.field_3447, this.field_3449, this.field_3448);
    }

    @Override
    public void cb$w$setupAnim(net.minecraft.class_10017 state) {
        this.method_2819(state);
    }

    @Override
    public void cb$prepared(net.minecraft.class_1297 entity, float partialTick) {
        this.cb$preparedEntity = new WeakReference<>(entity);
        this.cb$preparedPartialTick = partialTick;
    }

    @Override
    public float cb$preparedPartialTick(net.minecraft.class_1297 entity) {
        WeakReference<net.minecraft.class_1297> r = this.cb$preparedEntity;
        return r != null && r.get() == entity ? this.cb$preparedPartialTick : Float.NaN;
    }
}
