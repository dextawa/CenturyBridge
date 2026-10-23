package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * ByteBufferBuilder's bookkeeping: where the unbuilt batch starts (CbRenderBufferBuilderMixin reads the quads it
 * sorts there, as 1.20.6 read them in its own buffer), how many finished batches are still out (1.20.6 refused to
 * release a builder with any), and its capacity (a Tesselator's 1.20.6 builder is as large as its buffer).
 */
@Mixin(net.minecraft.class_9799.class)
public interface CbRenderByteBufferBuilderAccess {

    /** pointer */
    @Accessor("field_52082")
    long cb$pointer();

    /** capacity */
    @Accessor("field_52083")
    int cb$capacity();

    /** nextResultOffset */
    @Accessor("field_52085")
    int cb$nextResultOffset();

    /** resultCount */
    @Accessor("field_52086")
    int cb$resultCount();
}
