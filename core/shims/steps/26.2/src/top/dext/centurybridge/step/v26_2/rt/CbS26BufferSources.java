package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import top.dext.centurybridge.step.v26_2.facade.MultiBufferSource;
import top.dext.centurybridge.step.v26_2.facade.VertexMultiConsumer;

/** 26.1.2 members that took a MultiBufferSource (facade/MultiBufferSource), which 26.2 deleted (javap of both). */
@Environment(EnvType.CLIENT)
public final class CbS26BufferSources {
    private CbS26BufferSources() {}

    /** 26.1.2 SpriteId.buffer(sprites, bufferSource, renderType): the sprite's wrap of the buffer for its render type */
    public static VertexConsumer buffer(SpriteId self, SpriteGetter sprites, MultiBufferSource bufferSource,
                                        Function<Identifier, RenderType> renderType) {
        return sprites.get(self).wrap(bufferSource.getBuffer(self.renderType(renderType)));
    }

    /** 26.1.2 SpriteId.buffer(sprites, bufferSource, renderType, item, foil): the sprite's wrap of getFoilBuffer */
    public static VertexConsumer buffer(SpriteId self, SpriteGetter sprites, MultiBufferSource bufferSource,
                                        Function<Identifier, RenderType> renderType, boolean item, boolean foil) {
        return sprites.get(self).wrap(getFoilBuffer(bufferSource, self.renderType(renderType), item, foil));
    }

    /**
     * 26.1.2 ItemFeatureRenderer.getFoilBuffer(bufferSource, renderType, item, foil): with foil, the glint buffer and
     * the type's buffer together (VertexMultiConsumer, glint first); without, the type's buffer.
     */
    public static VertexConsumer getFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, boolean item, boolean foil) {
        if (foil) {
            return VertexMultiConsumer.create(bufferSource.getBuffer(getFoilRenderType(renderType, item)), bufferSource.getBuffer(renderType));
        }
        return bufferSource.getBuffer(renderType);
    }

    /**
     * 26.1.2 ItemFeatureRenderer.getFoilRenderType: the translucent glint when shader transparency is on and the type
     * draws into the item-entity target (26.2's useTransparentGlint, the flag now read from GameRenderState), else the
     * item or the entity glint.
     */
    static RenderType getFoilRenderType(RenderType renderType, boolean item) {
        boolean transparent = Minecraft.getInstance().gameRenderer.gameRenderState().useShaderTransparency()
            && renderType.outputTarget() == OutputTarget.ITEM_ENTITY_TARGET;
        if (transparent) {
            return RenderTypes.glintTranslucent();
        }
        return item ? RenderTypes.glint() : RenderTypes.entityGlint();
    }
}
