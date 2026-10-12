package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** SectionRenderDispatcher's fixed buffer pack, put back to the one a 1.20.2-shaped constructor call passed */
@Mixin(net.minecraft.class_846.class)
public interface CbSectionRenderDispatcherAccess {

    @Mutable
    @Accessor("field_20828")
    void cb$setFixedBuffers(net.minecraft.class_750 buffers);
}
