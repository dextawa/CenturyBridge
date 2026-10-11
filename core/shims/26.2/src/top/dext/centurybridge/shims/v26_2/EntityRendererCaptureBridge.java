package top.dext.centurybridge.shims.v26_2;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v26_2.CbRenderStateEntity;

/**
 * The dispatcher builds every entity's render state through createRenderState(entity,
 * partialTick) (create + extract + finalize): the state leaves it knowing its entity.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererCaptureBridge {

    @Inject(method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;",
            at = @At("RETURN"))
    private void centurybridge$capture(Entity entity, float partialTick, CallbackInfoReturnable<EntityRenderState> cir) {
        EntityRenderState state = cir.getReturnValue();
        if (state != null) {
            ((CbRenderStateEntity) state).centurybridge$capture(entity, partialTick);
        }
    }
}
