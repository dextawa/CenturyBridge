package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4fc;
import top.dext.centurybridge.step.v26_2.facade.MultiBufferSource;

/**
 * 26.1.2's Font drawing into a MultiBufferSource (facade/MultiBufferSource), which 26.2 dropped with the buffer
 * sources (javap of both): drawInBatch prepared the text (26.2 keeps prepareText) and visited it with
 * GlyphVisitor.forMultiBufferSource, which rendered every glyph and effect into the source's buffer for its render
 * type in the display mode, at the pose and light given. drawInBatch8xOutline drew the outline glyphs (26.2's
 * prepare8xTextOutline: the same eight offsets, effects discarded) in NORMAL mode, then the text (no shadow, empty
 * glyphs included, no background) in POLYGON_OFFSET mode.
 */
@Environment(EnvType.CLIENT)
public final class CbS26FontClient {
    private CbS26FontClient() {}

    /** 26.1.2 Font.GlyphVisitor.forMultiBufferSource */
    public static Font.GlyphVisitor forMultiBufferSource(MultiBufferSource bufferSource, Matrix4fc pose, Font.DisplayMode displayMode,
                                                         int lightCoords) {
        return new Font.GlyphVisitor() {
            @Override
            public void acceptGlyph(TextRenderable.Styled glyph) {
                this.render(glyph);
            }

            @Override
            public void acceptEffect(TextRenderable effect) {
                this.render(effect);
            }

            private void render(TextRenderable renderable) {
                VertexConsumer buffer = bufferSource.getBuffer(renderable.renderType(displayMode));
                renderable.render(pose, buffer, lightCoords, false);
            }
        };
    }

    /** 26.1.2 Font.drawInBatch(String, ...) */
    public static void drawInBatch(Font self, String text, float x, float y, int color, boolean dropShadow, Matrix4fc pose,
                                   MultiBufferSource bufferSource, Font.DisplayMode displayMode, int backgroundColor, int lightCoords) {
        self.prepareText(text, x, y, color, dropShadow, backgroundColor)
            .visit(forMultiBufferSource(bufferSource, pose, displayMode, lightCoords));
    }

    /** 26.1.2 Font.drawInBatch(Component, ...) */
    public static void drawInBatch(Font self, Component text, float x, float y, int color, boolean dropShadow, Matrix4fc pose,
                                   MultiBufferSource bufferSource, Font.DisplayMode displayMode, int backgroundColor, int lightCoords) {
        self.prepareText(text.getVisualOrderText(), x, y, color, dropShadow, false, backgroundColor)
            .visit(forMultiBufferSource(bufferSource, pose, displayMode, lightCoords));
    }

    /** 26.1.2 Font.drawInBatch(FormattedCharSequence, ...) */
    public static void drawInBatch(Font self, FormattedCharSequence text, float x, float y, int color, boolean dropShadow, Matrix4fc pose,
                                   MultiBufferSource bufferSource, Font.DisplayMode displayMode, int backgroundColor, int lightCoords) {
        self.prepareText(text, x, y, color, dropShadow, false, backgroundColor)
            .visit(forMultiBufferSource(bufferSource, pose, displayMode, lightCoords));
    }

    /** 26.1.2 Font.drawInBatch8xOutline */
    public static void drawInBatch8xOutline(Font self, FormattedCharSequence text, float x, float y, int color, int outlineColor,
                                            Matrix4fc pose, MultiBufferSource bufferSource, int lightCoords) {
        self.prepare8xTextOutline(text, x, y, outlineColor)
            .visit(forMultiBufferSource(bufferSource, pose, Font.DisplayMode.NORMAL, lightCoords));
        self.prepareText(text, x, y, color, false, true, 0)
            .visit(forMultiBufferSource(bufferSource, pose, Font.DisplayMode.POLYGON_OFFSET, lightCoords));
    }
}
