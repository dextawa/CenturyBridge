package cb.smx1c.mixin;

import java.util.Queue;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_4184;
import net.minecraft.class_761;
import net.minecraft.class_846;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.1 mixin on LevelRenderer's chunk-graph walk: HEAD of getRelativeFrom (method_3241) and of
 * initializeQueueForFullUpdate (method_38549), both capturing their arguments, neither reading this. 1.20.2 runs that
 * code in SectionOcclusionGraph (method_52831 / method_52821); the 1.20.1 -> 1.20.2 step's mixinMoves send these
 * injectors there (smx1-renames.json). Counts climbing on a 1.20.2 client in a world = the moved injectors run.
 */
@Mixin(class_761.class)
public abstract class LevelRendererGraphMixin {
    @Inject(method = "method_3241(Lnet/minecraft/class_2338;Lnet/minecraft/class_846$class_851;Lnet/minecraft/class_2350;)Lnet/minecraft/class_846$class_851;",
        at = @At("HEAD"), remap = false)
    private void cbsmx1c$relative(class_2338 pos, class_846.class_851 section, class_2350 dir, CallbackInfoReturnable<class_846.class_851> cir) {
        cb.smx1c.Counters.hit("getRelativeFrom", dir);
    }

    @Inject(method = "method_38549(Lnet/minecraft/class_4184;Ljava/util/Queue;)V", at = @At("HEAD"), remap = false)
    private void cbsmx1c$initQueue(class_4184 camera, Queue<?> queue, CallbackInfo ci) {
        cb.smx1c.Counters.hit("initializeQueueForFullUpdate", "queue size " + queue.size());
    }
}
