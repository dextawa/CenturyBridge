package top.dext.centurybridge.step.v1_21_2.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1058;
import net.minecraft.class_10142;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_293;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_8658;
import net.minecraft.class_8690;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's GuiGraphics draw API that 1.21.2 removed or reshaped (covers): the texture and sprite blits without
 * a RenderType function, setColor, drawManaged and bufferSource.
 *
 * 1.21.1 drew these blits immediately -- RenderSystem.setShaderTexture / setShader(position_tex[_color]), a
 * Tesselator quad, BufferUploader.drawWithShader -- in the GL state the caller left (blend, depth), tinted by
 * the shader colour. 1.21.2 still has that immediate path (CoreShaders.POSITION_TEX / POSITION_TEX_COLOR, the
 * shader colour is the ColorModulator uniform), so each blit here is 1.21.1's body on it. 1.21.1 also flushed
 * its buffer after every unmanaged fill/text/item draw, so whatever was drawn before a blit was on screen
 * first; 1.21.2 batches those until the frame's flush, so each blit flushes the batch before drawing itself
 * and the order on screen stays 1.21.1's.
 *
 * setColor set the shader colour (after flushing what was drawn under the previous one); in 1.21.2 the draws
 * made since are still batched, so it flushes them first -- under the colour that was current when they
 * were made -- and then sets the new one. drawManaged(r) ran r between two flushes, as here.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_332.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestGuiGraphicsMixin {

    @Shadow
    @Final
    private class_4587 field_44657;

    @Shadow
    @Final
    private class_4597.class_4598 field_44658;

    @Shadow
    @Final
    private class_8658 field_45337;

    @Shadow
    public abstract void method_51452();

    /** 1.21.1 drawManaged(Runnable) */
    public void method_51741(Runnable draw) {
        this.method_51452();
        draw.run();
        this.method_51452();
    }

    /** 1.21.1 bufferSource() */
    public class_4597.class_4598 method_51450() {
        return this.field_44658;
    }

    /** 1.21.1 setColor(r, g, b, a) */
    public void method_51422(float r, float g, float b, float a) {
        this.method_51452();
        RenderSystem.setShaderColor(r, g, b, a);
    }

    /** 1.21.1 blit(x, y, z, width, height, sprite) */
    public void method_25298(int x, int y, int z, int width, int height, class_1058 sprite) {
        this.cb$blitSprite(sprite, x, y, z, width, height);
    }

    /** 1.21.1 blit(x, y, z, width, height, sprite, r, g, b, a) */
    public void method_48465(int x, int y, int z, int width, int height, class_1058 sprite, float r, float g, float b, float a) {
        this.cb$innerBlit(sprite.method_45852(), x, x + width, y, y + height, z, sprite.method_4594(), sprite.method_4577(),
            sprite.method_4593(), sprite.method_4575(), r, g, b, a);
    }

    /** 1.21.1 blitSprite(sprite, x, y, width, height) */
    public void method_52706(class_2960 id, int x, int y, int width, int height) {
        this.method_52707(id, x, y, 0, width, height);
    }

    /** 1.21.1 blitSprite(sprite, x, y, z, width, height): stretched, tiled or nine-sliced as its metadata says */
    public void method_52707(class_2960 id, int x, int y, int z, int width, int height) {
        class_1058 sprite = this.field_45337.method_18667(id);
        class_8690 scaling = this.field_45337.method_52714(sprite);
        if (scaling instanceof class_8690.class_8693) {
            this.cb$blitSprite(sprite, x, y, z, width, height);
        } else if (scaling instanceof class_8690.class_8694 tile) {
            this.cb$blitTiledSprite(sprite, x, y, z, width, height, 0, 0, tile.comp_1644(), tile.comp_1645(), tile.comp_1644(), tile.comp_1645());
        } else if (scaling instanceof class_8690.class_8691 nineSlice) {
            this.cb$blitNineSlicedSprite(sprite, nineSlice, x, y, z, width, height);
        }
    }

    /** 1.21.1 blitSprite(sprite, textureWidth, textureHeight, u, v, x, y, width, height) */
    public void method_52708(class_2960 id, int tw, int th, int u, int v, int x, int y, int width, int height) {
        this.method_52709(id, tw, th, u, v, x, y, 0, width, height);
    }

    /** 1.21.1 blitSprite(sprite, textureWidth, textureHeight, u, v, x, y, z, width, height) */
    public void method_52709(class_2960 id, int tw, int th, int u, int v, int x, int y, int z, int width, int height) {
        class_1058 sprite = this.field_45337.method_18667(id);
        class_8690 scaling = this.field_45337.method_52714(sprite);
        if (scaling instanceof class_8690.class_8693) {
            this.cb$blitSpriteRegion(sprite, tw, th, u, v, x, y, z, width, height);
        } else {
            this.cb$blitSprite(sprite, x, y, z, width, height);
        }
    }

    /** 1.21.1 blit(texture, x, y, u, v, width, height): a 256x256 texture */
    public void method_25302(class_2960 tex, int x, int y, int u, int v, int width, int height) {
        this.method_25291(tex, x, y, 0, (float) u, (float) v, width, height, 256, 256);
    }

    /** 1.21.1 blit(texture, x, y, z, u, v, width, height, textureWidth, textureHeight) */
    public void method_25291(class_2960 tex, int x, int y, int z, float u, float v, int width, int height, int tw, int th) {
        this.cb$blit(tex, x, x + width, y, y + height, z, width, height, u, v, tw, th);
    }

    /** 1.21.1 blit(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight) */
    public void method_25293(class_2960 tex, int x, int y, int width, int height, float u, float v, int rw, int rh, int tw, int th) {
        this.cb$blit(tex, x, x + width, y, y + height, 0, rw, rh, u, v, tw, th);
    }

    /** 1.21.1 blit(texture, x, y, u, v, width, height, textureWidth, textureHeight) */
    public void method_25290(class_2960 tex, int x, int y, float u, float v, int width, int height, int tw, int th) {
        this.method_25293(tex, x, y, width, height, u, v, width, height, tw, th);
    }

    // ------------------------------------------------------------------ 1.21.1's private helpers

    @Unique
    private void cb$blit(class_2960 tex, int x1, int x2, int y1, int y2, int z, int rw, int rh, float u, float v, int tw, int th) {
        this.cb$innerBlit(tex, x1, x2, y1, y2, z, (u + 0.0F) / (float) tw, (u + (float) rw) / (float) tw,
            (v + 0.0F) / (float) th, (v + (float) rh) / (float) th);
    }

    @Unique
    private void cb$blitSprite(class_1058 sprite, int x, int y, int z, int width, int height) {
        if (width != 0 && height != 0) {
            this.cb$innerBlit(sprite.method_45852(), x, x + width, y, y + height, z, sprite.method_4594(), sprite.method_4577(),
                sprite.method_4593(), sprite.method_4575());
        }
    }

    @Unique
    private void cb$blitSpriteRegion(class_1058 sprite, int tw, int th, int u, int v, int x, int y, int z, int width, int height) {
        if (width != 0 && height != 0) {
            this.cb$innerBlit(sprite.method_45852(), x, x + width, y, y + height, z,
                sprite.method_4580((float) u / (float) tw), sprite.method_4580((float) (u + width) / (float) tw),
                sprite.method_4570((float) v / (float) th), sprite.method_4570((float) (v + height) / (float) th));
        }
    }

    @Unique
    private void cb$blitNineSlicedSprite(class_1058 sprite, class_8690.class_8691 nine, int x, int y, int z, int width, int height) {
        class_8690.class_8691.class_8692 border = nine.comp_1639();
        int left = Math.min(border.comp_1640(), width / 2);
        int right = Math.min(border.comp_1642(), width / 2);
        int top = Math.min(border.comp_1641(), height / 2);
        int bottom = Math.min(border.comp_1643(), height / 2);
        int w = nine.comp_1637();
        int h = nine.comp_1638();
        if (width == w && height == h) {
            this.cb$blitSpriteRegion(sprite, w, h, 0, 0, x, y, z, width, height);
        } else if (height == h) {
            this.cb$blitSpriteRegion(sprite, w, h, 0, 0, x, y, z, left, height);
            this.cb$blitTiledSprite(sprite, x + left, y, z, width - right - left, height, left, 0, w - right - left, h, w, h);
            this.cb$blitSpriteRegion(sprite, w, h, w - right, 0, x + width - right, y, z, right, height);
        } else if (width == w) {
            this.cb$blitSpriteRegion(sprite, w, h, 0, 0, x, y, z, width, top);
            this.cb$blitTiledSprite(sprite, x, y + top, z, width, height - bottom - top, 0, top, w, h - bottom - top, w, h);
            this.cb$blitSpriteRegion(sprite, w, h, 0, h - bottom, x, y + height - bottom, z, width, bottom);
        } else {
            this.cb$blitSpriteRegion(sprite, w, h, 0, 0, x, y, z, left, top);
            this.cb$blitTiledSprite(sprite, x + left, y, z, width - right - left, top, left, 0, w - right - left, top, w, h);
            this.cb$blitSpriteRegion(sprite, w, h, w - right, 0, x + width - right, y, z, right, top);
            this.cb$blitSpriteRegion(sprite, w, h, 0, h - bottom, x, y + height - bottom, z, left, bottom);
            this.cb$blitTiledSprite(sprite, x + left, y + height - bottom, z, width - right - left, bottom, left, h - bottom,
                w - right - left, bottom, w, h);
            this.cb$blitSpriteRegion(sprite, w, h, w - right, h - bottom, x + width - right, y + height - bottom, z, right, bottom);
            this.cb$blitTiledSprite(sprite, x, y + top, z, left, height - bottom - top, 0, top, left, h - bottom - top, w, h);
            this.cb$blitTiledSprite(sprite, x + left, y + top, z, width - right - left, height - bottom - top, left, top,
                w - right - left, h - bottom - top, w, h);
            this.cb$blitTiledSprite(sprite, x + width - right, y + top, z, left, height - bottom - top, w - right, top, right,
                h - bottom - top, w, h);
        }
    }

    @Unique
    private void cb$blitTiledSprite(class_1058 sprite, int x, int y, int z, int width, int height, int u, int v, int tileW, int tileH,
                                    int tw, int th) {
        if (width > 0 && height > 0) {
            if (tileW <= 0 || tileH <= 0) {
                throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + tileW + "x" + tileH);
            }
            for (int dx = 0; dx < width; dx += tileW) {
                int cw = Math.min(tileW, width - dx);
                for (int dy = 0; dy < height; dy += tileH) {
                    int ch = Math.min(tileH, height - dy);
                    this.cb$blitSpriteRegion(sprite, tw, th, u, v, x + dx, y + dy, z, cw, ch);
                }
            }
        }
    }

    /** 1.21.1 innerBlit (position_tex), after the pending batch */
    @Unique
    private void cb$innerBlit(class_2960 tex, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2) {
        this.method_51452();
        RenderSystem.setShaderTexture(0, tex);
        RenderSystem.setShader(class_10142.field_53879);
        Matrix4f pose = this.field_44657.method_23760().method_23761();
        class_287 b = class_289.method_1348().method_60827(class_293.class_5596.field_27382, class_290.field_1585);
        b.method_22918(pose, (float) x1, (float) y1, (float) z).method_22913(u1, v1);
        b.method_22918(pose, (float) x1, (float) y2, (float) z).method_22913(u1, v2);
        b.method_22918(pose, (float) x2, (float) y2, (float) z).method_22913(u2, v2);
        b.method_22918(pose, (float) x2, (float) y1, (float) z).method_22913(u2, v1);
        class_286.method_43433(b.method_60800());
    }

    /** 1.21.1 innerBlit (position_tex_color, blended), after the pending batch */
    @Unique
    private void cb$innerBlit(class_2960 tex, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2,
                              float r, float g, float bl, float a) {
        this.method_51452();
        RenderSystem.setShaderTexture(0, tex);
        RenderSystem.setShader(class_10142.field_53880);
        RenderSystem.enableBlend();
        Matrix4f pose = this.field_44657.method_23760().method_23761();
        class_287 b = class_289.method_1348().method_60827(class_293.class_5596.field_27382, class_290.field_1575);
        b.method_22918(pose, (float) x1, (float) y1, (float) z).method_22913(u1, v1).method_22915(r, g, bl, a);
        b.method_22918(pose, (float) x1, (float) y2, (float) z).method_22913(u1, v2).method_22915(r, g, bl, a);
        b.method_22918(pose, (float) x2, (float) y2, (float) z).method_22913(u2, v2).method_22915(r, g, bl, a);
        b.method_22918(pose, (float) x2, (float) y1, (float) z).method_22913(u2, v1).method_22915(r, g, bl, a);
        class_286.method_43433(b.method_60800());
        RenderSystem.disableBlend();
    }
}
