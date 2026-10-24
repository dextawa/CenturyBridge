package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallSpecialRenderers;

/** the display context of the item render state submitting, for 1.21.11 special renderers (rt/CbWallSpecialRenderers) */
@Environment(EnvType.CLIENT)
@Mixin(ItemStackRenderState.class)
public abstract class CbWallItemStackRenderStateMixin {

    @Shadow
    ItemDisplayContext displayContext;

    @Unique
    private ItemDisplayContext cb$outerContext;

    @Inject(method = "submit", at = @At("HEAD"))
    private void cb$enter(PoseStack pose, SubmitNodeCollector collector, int light, int overlay, int outline, CallbackInfo ci) {
        this.cb$outerContext = CbWallSpecialRenderers.enter(this.displayContext);
    }

    @Inject(method = "submit", at = @At("RETURN"))
    private void cb$exit(PoseStack pose, SubmitNodeCollector collector, int light, int overlay, int outline, CallbackInfo ci) {
        CbWallSpecialRenderers.exit(this.cb$outerContext);
        this.cb$outerContext = null;
    }
}
