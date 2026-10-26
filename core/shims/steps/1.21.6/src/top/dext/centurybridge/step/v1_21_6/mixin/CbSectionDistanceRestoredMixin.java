package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_846;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_6.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's SectionRenderDispatcher.RenderSection.getDistToPlayerSqr() (method_3668), restored with 1.21.5's body
 * (javap): the squared distance from the camera to the section's centre (bounding box minimum + 8 on each axis).
 * 1.21.5's hasAllNeighbors (method_3673) asked it first and answered true for a section within 24 blocks (distance
 * squared &lt;= 576), checking the neighbours only beyond; 1.21.6's checks the neighbours always (javap).
 * Route: when a converted mod mixed getDistToPlayerSqr (CbMixedMembers), 1.21.6's hasAllNeighbors runs whole -- native
 * 1.21.6 mixins on it included, theirs applied after this one -- and at its return asks the restored member and answers
 * true within 24 blocks: 1.21.5's answer (true within 24 blocks, the neighbours' beyond). Unmixed: exactly 1.21.6.
 * Priority below every mod's.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(value = class_846.class_851.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbSectionDistanceRestoredMixin {

    private static final String CB$KEY = "net/minecraft/class_846$class_851.method_3668()D";

    @CbVanillaReads("CbSectionDistanceRestoredMixin: RenderSection.hasAllNeighbors, at its return, when a converted mod mixed "
        + "it (CbMixedMembers): it is asked and a section within 24 blocks has all neighbours, as in 1.21.5")
    protected double method_3668() {
        class_243 camera = class_310.method_1551().field_1773.method_19418().method_19326();
        class_238 box = ((class_846.class_851) (Object) this).method_40051();
        double x = box.field_1323 + 8.0 - camera.field_1352;
        double y = box.field_1322 + 8.0 - camera.field_1351;
        double z = box.field_1321 + 8.0 - camera.field_1350;
        return x * x + y * y + z * z;
    }

    @Inject(method = "method_3673()Z", at = @At("RETURN"), cancellable = true)
    private void cb$closeSectionsHaveNeighbours(CallbackInfoReturnable<Boolean> cir) {
        if (CbMixedMembers.mixed(CB$KEY)) {
            double distance = this.method_3668();
            if (!cir.getReturnValueZ() && distance <= 576.0) {
                cir.setReturnValue(true);
            }
        }
    }
}
