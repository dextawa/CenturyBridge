package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_2586;
import net.minecraft.class_310;
import net.minecraft.class_827;
import net.minecraft.class_9810;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_6.rt.CbPrioritySplit;

/**
 * The injector into 1.21.6's own SectionCompiler.handleBlockEntity that goes with {@link CbSectionResultsRestoredMixin}
 * (default priority): a block entity whose renderer renders off screen goes into the restored globalBlockEntities, the
 * test 1.21.5's handleBlockEntity made (javap: getRenderer(be) non-null and shouldRenderOffScreen(); 1.21.6 makes the
 * same test in ClientLevel.onBlockEntityAdded, through Minecraft's BlockEntityRenderDispatcher, as here). Nothing of
 * 1.21.6's own handling changes; a mod cancelling handleBlockEntity at a higher priority still sees it first.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(class_9810.class)
public abstract class CbSectionResultsRestoredMixinHooks {

    @Inject(method = "method_60902(Lnet/minecraft/class_9810$class_9811;Lnet/minecraft/class_2586;)V", at = @At("HEAD"))
    private void cb$recordGlobal(class_9810.class_9811 results, class_2586 blockEntity, CallbackInfo ci) {
        class_827<class_2586> renderer = class_310.method_1551().method_31975().method_3550(blockEntity);
        if (renderer != null && renderer.method_3563()) {
            ((CbPrioritySplit.SectionGlobalBlockEntities) (Object) results).cb$v1_21_6$globalBlockEntities().add(blockEntity);
        }
    }
}
