package top.dext.centurybridge.facade.v26_2;

import java.util.List;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import top.dext.centurybridge.rt.v26_2.CbLegacyModelRender;
import top.dext.centurybridge.rt.v26_2.CbModels;

/**
 * Facade for 1.20.1 net/minecraft/class_5597 (SinglePartEntityModel), the base of most mod
 * entity models: the model is one part tree, getPart() (method_32008) hands it out and
 * render drew it whole. 26.x has no such class; an EntityModel takes its root at super()
 * and is drawn by it.
 *
 * The converter reshapes the mod's super() to the (ModelPart) forms below with the root
 * the mod's own constructor was handed (superCtorReshapes "param"), so 26.x poses, resets
 * and draws the mod's real tree. render is the 1.20.1 one -- getPart() drawn whole --
 * reached through ModelRenderBridge like any 1.20.1 model's render (a mod overriding it
 * keeps its own). Animation is the mod's setAngles, called with the real entity through
 * the setupAnim hook (CbModels), not here.
 *
 * Member names are intermediary tokens (classRename rewrites the owner, not the names).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public abstract class class_5597<T extends EntityRenderState> extends EntityModel<T> implements CbLegacyModelRender {

    /** a constructor that was handed no root: getPart() is still drawn by render */
    private static ModelPart emptyRoot() {
        return new ModelPart(List.of(), Map.of());
    }

    public class_5597() {
        super(emptyRoot());
    }

    public class_5597(java.util.function.Function<net.minecraft.resources.Identifier,
            net.minecraft.client.renderer.rendertype.RenderType> renderType) {
        super(emptyRoot(), renderType);
    }

    /** what the converter makes of the mod's super(): its own root */
    public class_5597(ModelPart root) {
        super(root);
    }

    public class_5597(ModelPart root, java.util.function.Function<net.minecraft.resources.Identifier,
            net.minecraft.client.renderer.rendertype.RenderType> renderType) {
        super(root, renderType);
    }

    /** getPart() -- the mod supplies its model's root */
    public abstract ModelPart method_32008();

    /** SinglePartEntityModel.render: getPart().render(...), the colour packed as 26.x takes it */
    @Override
    public void method_2828(PoseStack pose, VertexConsumer vc, int light, int overlay,
            float r, float g, float b, float a) {
        method_32008().render(pose, vc, light, overlay, CbModels.argb(r, g, b, a));
    }
}
