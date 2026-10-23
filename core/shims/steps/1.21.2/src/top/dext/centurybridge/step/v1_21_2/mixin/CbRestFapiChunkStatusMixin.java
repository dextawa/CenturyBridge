package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2791;
import net.minecraft.class_2818;
import net.minecraft.class_2821;
import net.minecraft.class_9310;
import net.minecraft.class_9312;
import net.minecraft.class_9761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestFapiEvents;

/**
 * ChunkStatusTasks.full (its lambda making the full chunk), as 1.21.1's Fabric API (0.116) hooked it after
 * CHUNK_LOAD: CHUNK_GENERATE when the chunk was made from a freshly generated proto chunk (not an
 * ImposterProtoChunk, which wraps a chunk read from disk), then CHUNK_LEVEL_TYPE_CHANGE for each step from the
 * holder's tracked level type to its full status. Applied after 1.21.2's Fabric API mixin (priority), whose
 * CHUNK_LOAD therefore fires first, as in 0.116.
 */
@Mixin(value = class_9310.class, priority = 1100)
public abstract class CbRestFapiChunkStatusMixin {

    @Inject(method = "method_60553", at = @At("TAIL"))
    private static void cb$generated(class_2791 proto, class_9312 context, class_9761 holder, CallbackInfoReturnable<class_2791> cir) {
        class_2818 chunk = (class_2818) cir.getReturnValue();
        if (!(proto instanceof class_2821)) {
            CbRestFapiEvents.CHUNK_GENERATE.invoker().onChunkGenerate(context.comp_2434(), chunk);
        }
        CbRestFapiEvents.raiseLevelType((CbRestFapiEvents.LevelTypeTracker) holder, context.comp_2434(), chunk, holder.method_60474());
    }
}
