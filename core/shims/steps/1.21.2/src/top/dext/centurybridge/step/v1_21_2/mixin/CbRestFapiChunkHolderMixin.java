package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.concurrent.Executor;
import net.minecraft.class_2806;
import net.minecraft.class_2818;
import net.minecraft.class_3193;
import net.minecraft.class_3194;
import net.minecraft.class_3218;
import net.minecraft.class_3898;
import net.minecraft.class_5539;
import net.minecraft.class_8563;
import net.minecraft.class_9761;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestFapiEvents;

/**
 * ServerChunkEvents.CHUNK_LEVEL_TYPE_CHANGE of 1.21.1's Fabric API (0.116), tracked per chunk holder as 0.116's
 * ChunkHolderMixin tracked it (1.21.2's ChunkHolder.updateFutures and demoteFullChunk are 1.21.1's): after each
 * of updateFutures' three save-dependency calls (full, block ticking, entity ticking promotions) one step up
 * when the tracked type is the one below; at the start of demoteFullChunk each step down to the new status.
 */
@Mixin(class_3193.class)
public abstract class CbRestFapiChunkHolderMixin implements CbRestFapiEvents.LevelTypeTracker {

    @Shadow
    @Final
    private class_5539 field_26929;

    @Shadow
    private int field_16432;

    @Unique
    private class_3194 cb$currentEventLevelType = class_3194.field_19334;

    @Override
    public class_3194 cb$getCurrentEventLevelType() {
        return this.cb$currentEventLevelType;
    }

    @Override
    public void cb$setCurrentEventLevelType(class_3194 type) {
        this.cb$currentEventLevelType = type;
    }

    @Unique
    private class_2818 cb$fullChunk() {
        return (class_2818) ((class_9761) (Object) this).method_60457(class_2806.field_12803);
    }

    @Unique
    private void cb$step(class_3194 from, class_3194 to) {
        CbRestFapiEvents.CHUNK_LEVEL_TYPE_CHANGE.invoker().onChunkLevelTypeChange((class_3218) this.field_26929, cb$fullChunk(), from, to);
        this.cb$currentEventLevelType = to;
    }

    @Inject(method = "method_14007", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3193;method_39967(Ljava/util/concurrent/CompletableFuture;)V",
            shift = At.Shift.AFTER, ordinal = 0))
    private void cb$inaccessibleToFull(class_3898 chunkMap, Executor executor, CallbackInfo ci) {
        if (((class_9761) (Object) this).method_60457(class_2806.field_12803) instanceof class_2818
                && this.cb$currentEventLevelType == class_3194.field_19334) {
            cb$step(class_3194.field_19334, class_3194.field_44855);
        }
    }

    @Inject(method = "method_14007", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3193;method_39967(Ljava/util/concurrent/CompletableFuture;)V",
            shift = At.Shift.AFTER, ordinal = 1))
    private void cb$fullToBlockTicking(class_3898 chunkMap, Executor executor, CallbackInfo ci) {
        if (this.cb$currentEventLevelType == class_3194.field_44855) {
            cb$step(class_3194.field_44855, class_3194.field_44856);
        }
    }

    @Inject(method = "method_14007", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3193;method_39967(Ljava/util/concurrent/CompletableFuture;)V",
            shift = At.Shift.AFTER, ordinal = 2))
    private void cb$blockTickingToEntityTicking(class_3898 chunkMap, Executor executor, CallbackInfo ci) {
        if (this.cb$currentEventLevelType == class_3194.field_44856) {
            cb$step(class_3194.field_44856, class_3194.field_13877);
        }
    }

    @Inject(method = "method_31408", at = @At("HEAD"))
    private void cb$decreaseLevel(class_3898 chunkMap, class_3194 target, CallbackInfo ci) {
        class_3194 status = class_8563.method_51830(this.field_16432);
        class_3194[] types = class_3194.values();
        for (int i = status.ordinal(); i > target.ordinal(); i--) {
            class_3194 from = types[i];
            class_3194 to = types[i - 1];
            if (this.cb$currentEventLevelType.method_14014(from)) {
                CbRestFapiEvents.CHUNK_LEVEL_TYPE_CHANGE.invoker().onChunkLevelTypeChange((class_3218) this.field_26929, cb$fullChunk(), from, to);
                this.cb$currentEventLevelType = to;
            }
        }
    }
}
