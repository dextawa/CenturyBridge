package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import top.dext.centurybridge.step.v26_2.facade.MultiBufferSource;

/** 26.1.2 members that took a MultiBufferSource (facade/MultiBufferSource), which 26.2 deleted (javap of both). */
@Environment(EnvType.CLIENT)
public final class CbS26BufferSources {
    private CbS26BufferSources() {}

    /** 26.1.2 SpriteId.buffer(sprites, bufferSource, renderType): the sprite's wrap of the buffer for its render type */
    public static VertexConsumer buffer(SpriteId self, SpriteGetter sprites, MultiBufferSource bufferSource,
                                        Function<Identifier, RenderType> renderType) {
        return sprites.get(self).wrap(bufferSource.getBuffer(self.renderType(renderType)));
    }
}
