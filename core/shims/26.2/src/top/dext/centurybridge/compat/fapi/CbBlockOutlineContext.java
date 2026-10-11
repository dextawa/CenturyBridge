package top.dext.centurybridge.compat.fapi;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * was WorldRenderContext.BlockOutlineContext. vertexConsumer() is null on 26.2:
 * the outline is a retained render state (BlockOutlineRenderState), there is no
 * line consumer to draw into during the event.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbBlockOutlineContext {

    VertexConsumer vertexConsumer();

    Entity entity();

    double cameraX();

    double cameraY();

    double cameraZ();

    BlockPos blockPos();

    BlockState blockState();

    record Impl(Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos blockPos, BlockState blockState) implements CbBlockOutlineContext {
        @Override
        public VertexConsumer vertexConsumer() {
            return null;
        }
    }
}
