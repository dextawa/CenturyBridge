package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.CompositeBlockModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 26.1's way of joining a special block model to the block's own model (rt/CbFapiSpecialBlocks) */
@Environment(EnvType.CLIENT)
@Mixin(BuiltInBlockModels.class)
public interface CbFapiBuiltInBlockModelsAccess {
    @Invoker("combineSpecialAndBlockModels")
    static CompositeBlockModel.Unbaked cb$combineSpecialAndBlockModels(BlockModel.Unbaked special, BlockColors colors, BlockState state) {
        throw new AssertionError();
    }
}
