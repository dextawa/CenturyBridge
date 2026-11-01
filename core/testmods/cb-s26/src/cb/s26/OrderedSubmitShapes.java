package cb.s26;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

/**
 * Conversion fixture for s26j-ordered-submit.json (never loaded at run time; judged on the converted bytecode):
 * a 26.1.2 collector under vanilla's SubmitNodeStorage, overriding the two shapes 26.2 reshaped. Its 26.2
 * submitShapeOutline / submitGizmoPrimitives / submitQuadParticleGroup must stay vanilla's (no abstract fill:
 * SubmitNodeStorage implements them on 26.2), while the reshaped overrides get the bridged new shapes.
 */
final class OrderedSubmitShapes {
    private OrderedSubmitShapes() {}

    static final class ModStorage extends SubmitNodeStorage {
        int nameTags;
        int breaking;

        @Override
        public void submitNameTag(PoseStack pose, Vec3 offset, int y, Component name, boolean seeThrough, int light, double distance,
                                  CameraRenderState camera) {
            nameTags++;
            super.submitNameTag(pose, offset, y, name, seeThrough, light, distance, camera);
        }

        @Override
        public void submitBreakingBlockModel(PoseStack pose, BlockStateModel model, long seed, int progress) {
            breaking++;
            super.submitBreakingBlockModel(pose, model, seed, progress);
        }
    }
}
