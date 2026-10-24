package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbFapiBlockColors;

/**
 * A 1.21.11 BlockColor answered every tint index; converted to a 26.1 tint list it is as long as the block's models
 * need (rt/CbFapiBlockColors), and an index past that is still answered by the BlockColor.
 */
@Environment(EnvType.CLIENT)
@Mixin(BlockColors.class)
public abstract class CbFapiBlockColorsMixin {

    @Inject(method = "getTintSource", at = @At("RETURN"), cancellable = true)
    private void cb$legacyTintIndex(BlockState state, int tintIndex, CallbackInfoReturnable<BlockTintSource> cir) {
        if (cir.getReturnValue() == null) {
            BlockTintSource beyond = CbFapiBlockColors.beyond(((BlockColors) (Object) this).getTintSources(state), tintIndex);
            if (beyond != null) {
                cir.setReturnValue(beyond);
            }
        }
    }
}
