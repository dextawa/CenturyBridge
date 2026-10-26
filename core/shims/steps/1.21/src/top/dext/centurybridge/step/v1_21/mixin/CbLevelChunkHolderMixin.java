package top.dext.centurybridge.step.v1_21.mixin;

import java.util.concurrent.CompletableFuture;

import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_3193;
import net.minecraft.class_8563;
import net.minecraft.class_9259;
import net.minecraft.class_9761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbLevelChunks;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * 1.20.6 ChunkHolder members 1.21 has no counterpart of under any name, given back on ChunkHolder itself so a
 * 1.20.6 mod's calls, overrides and super calls all link (covers in s6fin-level.json). The getters 1.21 kept
 * under new names (getPos, getFullStatus, ...) are renamed instead (methodRenames / overrideRenames).
 */
@Mixin(value = class_3193.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbLevelChunkHolderMixin implements CbLevelChunks.LegacyHolder {

    @Shadow
    private void method_39967(CompletableFuture<?> future) {
    }

    /** 1.20.6 getFutureIfPresentUnchecked(status): the status's future, or the unloaded future when none was made */
    @Override
    public CompletableFuture<class_9259<class_2791>> method_16146(class_2806 status) {
        return CbLevelChunks.futureIfPresentUnchecked((class_9761) (Object) this, status);
    }

    /**
     * 1.20.6 getFutureIfPresent(status): getFutureIfPresentUnchecked(status) -- the holder's own, as 1.20.6 called
     * it -- when the ticket level allows generating to the status, else the unloaded future.
     */
    public CompletableFuture<class_9259<class_2791>> method_21737(class_2806 status) {
        class_2806 allowed = class_8563.method_51827(((class_3193) (Object) this).method_14005());
        return allowed.method_12165(status) ? ((CbLevelChunks.LegacyHolder) this).method_16146(status) : class_9761.field_51867;
    }

    /**
     * 1.20.6 addSaveDependency(label, future): saving the chunk waits for the future. 1.21's
     * addSaveDependency(future) does that (on the save-sync future); the label only named the dependency in
     * 1.20.6's debug buffer, which 1.21 dropped.
     */
    protected void method_39967(String label, CompletableFuture<?> future) {
        this.method_39967(future);
    }
}
