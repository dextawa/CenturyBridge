package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** a block render state's special renderer, submitted apart from its model (rt/CbFapiSubmitBlocks) */
@Environment(EnvType.CLIENT)
@Mixin(BlockModelRenderState.class)
public interface CbFapiBlockModelRenderStateAccess {
    @Accessor("specialRenderer")
    SpecialModelRenderer<?> cb$specialRenderer();

    @Accessor("specialRenderer")
    void cb$setSpecialRenderer(SpecialModelRenderer<?> renderer);

    @Accessor("specialRendererTransformation")
    Matrix4fc cb$specialRendererTransformation();

    @Invoker("submitSpecialRenderer")
    static void cb$submitSpecialRenderer(SpecialModelRenderer<?> renderer, PoseStack pose, SubmitNodeCollector collector, int light,
                                         int overlay, int outline) {
        throw new AssertionError();
    }
}
