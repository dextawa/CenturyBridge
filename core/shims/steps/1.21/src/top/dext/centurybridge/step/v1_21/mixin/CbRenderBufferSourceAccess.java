package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * MultiBufferSource.BufferSource's memory and bookkeeping, which a 1.20.6 subclass read through the fields 1.20.6
 * had there (its shared builder, its fixed builders, the last state, the started builders: CbRenderBuffers'
 * BufferSource views).
 */
@Mixin(net.minecraft.class_4597.class_4598.class)
public interface CbRenderBufferSourceAccess {

    /** sharedBuffer */
    @Accessor("field_52156")
    net.minecraft.class_9799 cb$sharedBuffer();

    /** fixedBuffers */
    @Accessor("field_20953")
    java.util.SequencedMap<net.minecraft.class_1921, net.minecraft.class_9799> cb$fixedBuffers();

    /** startedBuilders */
    @Accessor("field_52157")
    java.util.Map<net.minecraft.class_1921, net.minecraft.class_287> cb$startedBuilders();

    /** lastSharedType */
    @Accessor("field_52158")
    net.minecraft.class_1921 cb$lastSharedType();

    /** lastSharedType */
    @Accessor("field_52158")
    void cb$setLastSharedType(net.minecraft.class_1921 type);
}
