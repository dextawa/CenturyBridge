package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * SectionRenderDispatcher's closed flag and upload queue: 1.20.6's uploadSectionLayer(RenderedBuffer, VertexBuffer) ran on that queue unless closed (CbRenderLevel).
 */
@Mixin(net.minecraft.class_846.class)
public interface CbRenderSectionDispatcherAccess {

    @Accessor("field_46908")
    boolean cb$closed();

    @Accessor("field_4443")
    java.util.Queue<java.lang.Runnable> cb$toUpload();
}
