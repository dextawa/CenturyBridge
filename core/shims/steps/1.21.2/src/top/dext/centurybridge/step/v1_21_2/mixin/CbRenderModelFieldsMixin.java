package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields;

/**
 * 1.21.1 EntityModel's public attackTime / riding / young (field_3447 / field_3449 / field_3448), gone in 1.21.2 (the
 * render state carries them): put back as the plain fields a 1.21.1 model and its renderer read and write. 1.21.1's
 * initial values: young = true (EntityModel's constructor), the others zero. See CbRenderModelFields.
 */
@Mixin(net.minecraft.class_583.class)
public abstract class CbRenderModelFieldsMixin implements CbRenderModelFields.Pose {

    public float field_3447;

    public boolean field_3449;

    public boolean field_3448 = true;

    @Override
    public void cb$pose(float attackTime, boolean riding, boolean young) {
        this.field_3447 = attackTime;
        this.field_3449 = riding;
        this.field_3448 = young;
    }
}
