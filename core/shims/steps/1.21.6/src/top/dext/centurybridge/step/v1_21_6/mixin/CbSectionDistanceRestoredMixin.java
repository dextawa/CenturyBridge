package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_846;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's SectionRenderDispatcher.RenderSection.getDistToPlayerSqr() (method_3668), restored with 1.21.5's body
 * (javap): the squared distance from the camera to the section's centre (bounding box minimum + 8 on each axis).
 * 1.21.5's only vanilla caller was hasAllNeighbors (method_3673: sections within 24 blocks counted as having all
 * neighbours); 1.21.6's hasAllNeighbors checks the neighbours always and computes no distance (javap), so vanilla has no
 * path that stands for this member. Mods' calls and invokers reach it. Priority below every mod's.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(value = class_846.class_851.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbSectionDistanceRestoredMixin {

    protected double method_3668() {
        class_243 camera = class_310.method_1551().field_1773.method_19418().method_19326();
        class_238 box = ((class_846.class_851) (Object) this).method_40051();
        double x = box.field_1323 + 8.0 - camera.field_1352;
        double y = box.field_1322 + 8.0 - camera.field_1351;
        double z = box.field_1321 + 8.0 - camera.field_1350;
        return x * x + y * y + z * z;
    }
}
