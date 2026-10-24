package top.dext.centurybridge.step.v1_21_2.mixin;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * 1.21.1 BoatModel's parts (see CbRenderBoats): createPartsBuilder(root) -- bottom, back, front, right, left and the
 * two paddles, the chest boat's (ChestBoatModel) also chest_bottom, chest_lid and chest_lock, which 1.21.2's one
 * BoatModel class has when baked from the chest boat layer -- parts(), built once from it as 1.21.1's constructor did
 * (a subclass's override is called), and waterPatch().
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_554.class)
public abstract class CbRenderBoatModelMixin {

    @Unique
    private ImmutableList<net.minecraft.class_630> cb$parts;

    @Unique
    private net.minecraft.class_630 cb$waterPatch;

    /** 1.21.1 createPartsBuilder(root) */
    protected ImmutableList.Builder<net.minecraft.class_630> method_45702(net.minecraft.class_630 root) {
        return top.dext.centurybridge.step.v1_21_2.rt.CbRenderBoats.boatParts(root);
    }

    /** 1.21.1 parts() */
    public ImmutableList<net.minecraft.class_630> method_22953() {
        if (this.cb$parts == null) {
            this.cb$parts = this.method_45702(((net.minecraft.class_3879) (Object) this).method_63512()).build();
        }
        return this.cb$parts;
    }

    /** 1.21.1 waterPatch() */
    public net.minecraft.class_630 method_22954() {
        if (this.cb$waterPatch == null) {
            this.cb$waterPatch = top.dext.centurybridge.step.v1_21_2.rt.CbRenderBoats.waterPatch();
        }
        return this.cb$waterPatch;
    }
}
