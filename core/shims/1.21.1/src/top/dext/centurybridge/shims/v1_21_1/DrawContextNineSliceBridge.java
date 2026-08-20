package top.dext.centurybridge.shims.v1_21_1;

import net.minecraft.class_332;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;

/**
 * DrawContext nine-slice methods removed at 1.20.2.
 *
 * method_48586(id, x,y,w,h, borderX,borderY, texW,texH): draws a nine-slice
 * from a 256×256 texture.  Rebuilt on method_25302 which draws one tile at a
 * time (id, x,y, u,v, regionW,regionH).
 *
 * method_48587 was the 12-arg variant (explicit uvX,uvY source coords); same
 * nine-slice algorithm, just with an explicit texture-source offset.
 *
 * 29 + (method_48587) call sites.
 */
@Mixin(class_332.class)
public abstract class DrawContextNineSliceBridge {

    // --- method_48586: id, x,y,w,h, borderX,borderY, texW(always 256),texH(always 256) ---
    public void method_48586(class_2960 texture,
                              int x, int y, int width, int height,
                              int borderX, int borderY,
                              int textureWidth, int textureHeight) {
        method_48587(texture, x, y, width, height,
                     borderX, borderY,
                     textureWidth, textureHeight,
                     0, 0,  // u,v source origin
                     textureWidth, textureHeight);
    }

    // --- method_48587: explicit source u/v ---
    public void method_48587(class_2960 texture,
                              int x, int y, int width, int height,
                              int borderX, int borderY,
                              int textureWidth, int textureHeight,
                              int u, int v,
                              int regionW, int regionH) {
        class_332 ctx = (class_332) (Object) this;

        int innerW = width  - borderX * 2;
        int innerH = height - borderY * 2;
        int srcInnerW = regionW - borderX * 2;
        int srcInnerH = regionH - borderY * 2;

        // top-left corner
        ctx.method_25302(texture, x, y,
                         u, v, borderX, borderY);
        // top-right corner
        ctx.method_25302(texture, x + width - borderX, y,
                         u + regionW - borderX, v, borderX, borderY);
        // bottom-left corner
        ctx.method_25302(texture, x, y + height - borderY,
                         u, v + regionH - borderY, borderX, borderY);
        // bottom-right corner
        ctx.method_25302(texture, x + width - borderX, y + height - borderY,
                         u + regionW - borderX, v + regionH - borderY, borderX, borderY);

        // top edge (tiled horizontally)
        drawRepeated(ctx, texture, x + borderX, y, innerW, borderY,
                     u + borderX, v, srcInnerW, borderY);
        // bottom edge
        drawRepeated(ctx, texture, x + borderX, y + height - borderY, innerW, borderY,
                     u + borderX, v + regionH - borderY, srcInnerW, borderY);
        // left edge (tiled vertically)
        drawRepeated(ctx, texture, x, y + borderY, borderX, innerH,
                     u, v + borderY, borderX, srcInnerH);
        // right edge
        drawRepeated(ctx, texture, x + width - borderX, y + borderY, borderX, innerH,
                     u + regionW - borderX, v + borderY, borderX, srcInnerH);
        // center fill
        drawRepeated(ctx, texture, x + borderX, y + borderY, innerW, innerH,
                     u + borderX, v + borderY, srcInnerW, srcInnerH);
    }

    private static void drawRepeated(class_332 ctx, class_2960 texture,
                                      int destX, int destY, int destW, int destH,
                                      int srcU, int srcV, int srcW, int srcH) {
        if (destW <= 0 || destH <= 0 || srcW <= 0 || srcH <= 0) {
            return;
        }
        for (int ty = 0; ty < destH; ty += srcH) {
            int h = Math.min(srcH, destH - ty);
            for (int tx = 0; tx < destW; tx += srcW) {
                int w = Math.min(srcW, destW - tx);
                ctx.method_25302(texture, destX + tx, destY + ty, srcU, srcV, w, h);
            }
        }
    }
}
