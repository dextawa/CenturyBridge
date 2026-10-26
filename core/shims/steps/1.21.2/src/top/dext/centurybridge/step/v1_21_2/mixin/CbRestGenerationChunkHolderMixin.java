package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.class_9761;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** GenerationChunkHolder.getGenerationRefCount() of 1.21.1, back: the generation reference counter's value */
@Mixin(value = class_9761.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestGenerationChunkHolderMixin {

    @Shadow
    @Final
    private AtomicInteger field_51874;

    public int method_60470() {
        return this.field_51874.get();
    }
}
