package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * RenderSection's dispatcher (field_20833, the synthetic outer instance of the inner class: javac cannot name it, an access
 * widener does not help): the restored 1.20.6 RebuildTask.compile / handleBlockEntity reach 1.21's SectionCompiler through
 * it (CbRenderRebuildTaskRestoredMixin, s121 C20).
 */
@Mixin(targets = "net.minecraft.class_846$class_851")
public interface CbRenderSectionAccess {

    @Accessor("field_20833")
    net.minecraft.class_846 cb$dispatcher();
}
