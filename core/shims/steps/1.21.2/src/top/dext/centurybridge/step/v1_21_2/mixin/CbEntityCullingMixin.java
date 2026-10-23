package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityMisc;

/**
 * EntityRenderer.shouldRender: 1.21.1 asked the entity -- its noCulling switch, then its getBoundingBoxForCulling --
 * where 1.21.2 asks the renderer (affectedByCulling, getBoundingBoxForCulling(entity)). An entity with 1.21.1's
 * noCulling set is not culled, and a mod class's own 1.21.1 culling box is the one tested (CbEntityMisc).
 */
@Environment(EnvType.CLIENT)
@Mixin(class_897.class)
public abstract class CbEntityCullingMixin {

    @Redirect(method = "method_3933", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_897;method_62406(Lnet/minecraft/class_1297;)Z"))
    private boolean cb$noCulling(class_897<?, ?> renderer, class_1297 entity) {
        if (((CbEntityMisc.Fields) entity).cb$noCulling()) {
            return false;
        }
        return ((CbEntityCullingInvoker) renderer).cb$affectedByCulling(entity);
    }

    @Redirect(method = "method_3933", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_897;method_62358(Lnet/minecraft/class_1297;)Lnet/minecraft/class_238;"))
    private class_238 cb$cullingBox(class_897<?, ?> renderer, class_1297 entity) {
        class_238 own = CbEntityMisc.cullingBox(entity);
        return own != null ? own : ((CbEntityCullingInvoker) renderer).cb$getBoundingBoxForCulling(entity);
    }
}
