package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderDispatch;

/**
 * BlockEntityRenderDispatcher's 1.21.3 members (see {@link CbRenderDispatch}): a dispatcher built with 1.21.3's renderer
 * Suppliers asks them when it reloads, as 1.21.3 did; renderItem(blockEntity, ..) is back.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_824.class)
public abstract class CbRenderDispatchMixin implements CbRenderDispatch.CbLegacyBlockEntityDispatcher {

    @Shadow
    @Final
    @Mutable
    private net.minecraft.class_776 field_27747;

    @Shadow
    @Final
    @Mutable
    private net.minecraft.class_10442 field_55283;

    @Shadow
    @Final
    @Mutable
    private net.minecraft.class_918 field_38880;

    @Shadow
    @Final
    @Mutable
    private net.minecraft.class_898 field_38881;

    @Shadow
    public abstract <E extends net.minecraft.class_2586> net.minecraft.class_827<E> method_3550(E blockEntity);

    private Supplier<net.minecraft.class_776> cb$blockRenderer;
    private Supplier<net.minecraft.class_918> cb$itemRenderer;
    private Supplier<net.minecraft.class_898> cb$entityRenderer;

    @Override
    public void cb$setSuppliers(Supplier<net.minecraft.class_776> blockRenderer, Supplier<net.minecraft.class_918> itemRenderer,
            Supplier<net.minecraft.class_898> entityRenderer) {
        this.cb$blockRenderer = blockRenderer;
        this.cb$itemRenderer = itemRenderer;
        this.cb$entityRenderer = entityRenderer;
    }

    /** 1.21.3's reload asked the Suppliers for the renderers its renderers' context carries */
    @Inject(method = "method_14491", at = @At("HEAD"))
    private void cb$askSuppliers(net.minecraft.class_3300 resourceManager, CallbackInfo ci) {
        if (this.cb$blockRenderer != null) {
            this.field_27747 = this.cb$blockRenderer.get();
            this.field_55283 = CbRenderDispatch.cbGameResolver();
            this.field_38880 = this.cb$itemRenderer.get();
            this.field_38881 = this.cb$entityRenderer.get();
        }
    }

    /** was renderItem(blockEntity, pose, buffers, light, overlay) (method_23077): true when it has no renderer */
    public <E extends net.minecraft.class_2586> boolean method_23077(E blockEntity, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers,
            int light, int overlay) {
        net.minecraft.class_827<E> renderer = this.method_3550(blockEntity);
        if (renderer == null) {
            return true;
        }
        try {
            renderer.method_3569(blockEntity, 0.0f, pose, buffers, light, overlay);
        } catch (Throwable t) {
            net.minecraft.class_128 report = net.minecraft.class_128.method_560(t, "Rendering Block Entity");
            blockEntity.method_11003(report.method_562("Block Entity Details"));
            throw new net.minecraft.class_148(report);
        }
        return false;
    }
}
