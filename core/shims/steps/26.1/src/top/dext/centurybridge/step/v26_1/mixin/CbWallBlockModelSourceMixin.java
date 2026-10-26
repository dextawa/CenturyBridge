package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallBlockModelSource;

/**
 * The block state a BlockModelRenderState was last resolved from (rt/CbWallBlockModelSource), with the resolver and
 * display context that resolved it. clear() forgets the block state (26.1's renderers clear the model when there is
 * no block). Adds no 1.21.11 member: default priority.
 */
@Environment(EnvType.CLIENT)
@Mixin(BlockModelRenderState.class)
public abstract class CbWallBlockModelSourceMixin implements CbWallBlockModelSource.Source {

    @Unique
    private BlockState cb$source;
    @Unique
    private BlockModelResolver cb$resolver;
    @Unique
    private BlockDisplayContext cb$context;

    @Override
    public BlockState cb$source() {
        return this.cb$source;
    }

    @Override
    public BlockModelResolver cb$resolver() {
        return this.cb$resolver;
    }

    @Override
    public BlockDisplayContext cb$context() {
        return this.cb$context;
    }

    @Override
    public void cb$resolved(BlockState state, BlockModelResolver resolver, BlockDisplayContext context) {
        this.cb$source = state;
        this.cb$resolver = resolver;
        this.cb$context = context;
    }

    @Inject(method = "clear()V", at = @At("TAIL"))
    private void cb$cleared(CallbackInfo ci) {
        this.cb$source = null;
    }
}
