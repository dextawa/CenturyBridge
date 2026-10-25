package top.dext.centurybridge.step.v1_21_6.mixin;

/**
 * Every LevelReader is a 1.21.5 RenderAttachedBlockView, as Fabric API 1.21.5's fabric-rendering-data-attachment-v1
 * made it (its WorldViewMixin targets class_4538); the interface only adds a default method.
 */
@org.spongepowered.asm.mixin.Mixin(net.minecraft.class_4538.class)
public interface CbRenderAttachedViewMixin extends top.dext.centurybridge.step.v1_21_6.facade.fapi.RenderAttachedBlockView {
}
