package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbFapiSpecialBlocks;

import java.util.Map;

/** 1.21.11's SpecialBlockRendererRegistry blocks join 26.1's built-in block models (rt/CbFapiSpecialBlocks) */
@Environment(EnvType.CLIENT)
@Mixin(BuiltInBlockModels.class)
public abstract class CbFapiBuiltInBlockModelsMixin {

    @Inject(method = "createBlockModels", at = @At("RETURN"), cancellable = true)
    private static void cb$legacySpecialBlocks(BlockColors colors, CallbackInfoReturnable<Map<BlockState, BlockModel.Unbaked>> cir) {
        Map<BlockState, BlockModel.Unbaked> models = cir.getReturnValue();
        Map<BlockState, BlockModel.Unbaked> legacy = CbFapiSpecialBlocks.withRegistered(models, colors);
        if (legacy != models) {
            cir.setReturnValue(legacy);
        }
    }
}
