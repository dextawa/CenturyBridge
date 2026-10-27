package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import top.dext.centurybridge.step.v26_1.rt.CbWallSpecialBlocks;
import top.dext.centurybridge.step.v26_1.rt.CbWallSpecialRenderers;

/**
 * 1.21.11's SpecialBlockModelRenderer (net/minecraft/class_10418), gone in 26.1 (a block's special model lives in its
 * block model, BuiltInBlockModels): reached by classRenames, 1.21.11's body (javap): a block -> special renderer map,
 * renderByBlock submitting the block's renderer with no argument, no foil. The renderer is asked with the display
 * context the caller gives, through CbWallSpecialRenderers.callSubmit (a 1.21.11 renderer takes it; 26.1's never read
 * one). Member names are 1.21.11's intermediary names (a classRenames facade keeps them past the wall).
 */
@Environment(EnvType.CLIENT)
public class SpecialBlockModelRenderer {
    /** EMPTY */
    public static final SpecialBlockModelRenderer field_55269 = new SpecialBlockModelRenderer(Map.of());

    /** renderers (private in 1.21.11; public here: a mod's access widener cannot reach a CenturyBridge class) */
    public final Map<Block, SpecialModelRenderer<?>> field_55270;

    public SpecialBlockModelRenderer(Map<Block, SpecialModelRenderer<?>> renderers) {
        this.field_55270 = renderers;
    }

    /** vanilla(context): the renderers of 1.21.11's block table (rt/CbWallSpecialBlocks.createBlockRenderers) */
    public static SpecialBlockModelRenderer method_65536(SpecialModelRenderer.BakingContext context) {
        return new SpecialBlockModelRenderer(CbWallSpecialBlocks.createBlockRenderers(context));
    }

    /** renderByBlock(block, displayContext, poseStack, collector, light, overlay, outline) */
    public void method_65535(Block block, ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector collector, int light,
                             int overlay, int outline) {
        SpecialModelRenderer<?> renderer = this.field_55270.get(block);
        if (renderer != null) {
            CbWallSpecialRenderers.callSubmit(renderer, null, displayContext, poseStack, collector, light, overlay, false, outline);
        }
    }
}
