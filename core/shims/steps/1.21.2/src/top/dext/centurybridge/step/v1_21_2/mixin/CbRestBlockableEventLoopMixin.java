package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1255;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.facade.ProcessorHandle;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's BlockableEventLoop was a ProcessorHandle&lt;R&gt; (the server, the client and the main-thread mail
 * box of world generation are handed around as one): it is again (the layer's ProcessorHandle facade).
 * name() is its own; tell(message) schedules it, as 1.21.1's bridge did; close() is the empty default both
 * interfaces give.
 */
@Mixin(value = class_1255.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestBlockableEventLoopMixin<R extends Runnable> implements ProcessorHandle<R> {

    @Shadow
    public abstract void method_63588(R task);

    @Override
    public void method_16901(R message) {
        this.method_63588(message);
    }

    @Override
    public void close() {
    }
}
