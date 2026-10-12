package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * GuiGraphics.blitNineSliced (three shapes) and blitRepeating, removed in 1.20.2 when nine-slicing
 * moved into sprite metadata (drawGuiTexture). These are the 1.20.1 bodies, unchanged: a region of a
 * texture sheet cut into corners, edges and middle, the edges and middle tiled with 1.20.1's Divider
 * (which 1.20.2 still has), each piece drawn with the 1.20.2 blit that 1.20.1 used. The pieces are drawn
 * through this object, as in 1.20.1, so a GuiGraphics subclass that overrides one of them is honoured.
 */
@Mixin(net.minecraft.class_332.class)
public abstract class CbGuiGraphicsBridge implements top.dext.centurybridge.rt.v1_20_2.CbLegacyNineSlice {

    /** 1.20.1 blitNineSliced(texture, x, y, width, height, sliceSize, uWidth, vHeight, u, v) */
    public void method_48586(net.minecraft.class_2960 texture, int x, int y, int width, int height, int slice,
                             int uWidth, int vHeight, int u, int v) {
        this.method_48587(texture, x, y, width, height, slice, slice, slice, slice, uWidth, vHeight, u, v);
    }

    /** 1.20.1 blitNineSliced(texture, x, y, width, height, sliceWidth, sliceHeight, uWidth, vHeight, u, v) */
    public void method_49697(net.minecraft.class_2960 texture, int x, int y, int width, int height, int sliceWidth,
                             int sliceHeight, int uWidth, int vHeight, int u, int v) {
        this.method_48587(texture, x, y, width, height, sliceWidth, sliceHeight, sliceWidth, sliceHeight, uWidth, vHeight, u, v);
    }

    /** 1.20.1 blitNineSliced(texture, x, y, width, height, left, top, right, bottom, uWidth, vHeight, u, v) */
    @Override
    public void method_48587(net.minecraft.class_2960 texture, int x, int y, int width, int height, int left, int top,
                             int right, int bottom, int uWidth, int vHeight, int u, int v) {
        net.minecraft.class_332 self = (net.minecraft.class_332) (Object) this;
        left = Math.min(left, width / 2);
        right = Math.min(right, width / 2);
        top = Math.min(top, height / 2);
        bottom = Math.min(bottom, height / 2);
        if (width == uWidth && height == vHeight) {
            self.method_25302(texture, x, y, u, v, width, height);
            return;
        }
        if (height == vHeight) {
            self.method_25302(texture, x, y, u, v, left, height);
            this.method_48585(texture, x + left, y, width - right - left, height, u + left, v, uWidth - right - left, vHeight);
            self.method_25302(texture, x + width - right, y, u + uWidth - right, v, right, height);
            return;
        }
        if (width == uWidth) {
            self.method_25302(texture, x, y, u, v, width, top);
            this.method_48585(texture, x, y + top, width, height - bottom - top, u, v + top, uWidth, vHeight - bottom - top);
            self.method_25302(texture, x, y + height - bottom, u, v + vHeight - bottom, width, bottom);
            return;
        }
        self.method_25302(texture, x, y, u, v, left, top);
        this.method_48585(texture, x + left, y, width - right - left, top, u + left, v, uWidth - right - left, top);
        self.method_25302(texture, x + width - right, y, u + uWidth - right, v, right, top);
        self.method_25302(texture, x, y + height - bottom, u, v + vHeight - bottom, left, bottom);
        this.method_48585(texture, x + left, y + height - bottom, width - right - left, bottom, u + left, v + vHeight - bottom,
            uWidth - right - left, bottom);
        self.method_25302(texture, x + width - right, y + height - bottom, u + uWidth - right, v + vHeight - bottom, right, bottom);
        this.method_48585(texture, x, y + top, left, height - bottom - top, u, v + top, left, vHeight - bottom - top);
        this.method_48585(texture, x + left, y + top, width - right - left, height - bottom - top, u + left, v + top,
            uWidth - right - left, vHeight - bottom - top);
        this.method_48585(texture, x + width - right, y + top, right, height - bottom - top, u + uWidth - right, v + top,
            right, vHeight - bottom - top);
    }

    /** 1.20.1 blitRepeating(texture, x, y, width, height, u, v, uWidth, vHeight) */
    public void method_48585(net.minecraft.class_2960 texture, int x, int y, int width, int height, int u, int v,
                             int uWidth, int vHeight) {
        net.minecraft.class_332 self = (net.minecraft.class_332) (Object) this;
        int cx = x;
        it.unimi.dsi.fastutil.ints.IntIterator cols = slices(width, uWidth);
        while (cols.hasNext()) {
            int w = cols.nextInt();
            int du = (uWidth - w) / 2;
            int cy = y;
            it.unimi.dsi.fastutil.ints.IntIterator rows = slices(height, vHeight);
            while (rows.hasNext()) {
                int h = rows.nextInt();
                int dv = (vHeight - h) / 2;
                self.method_25302(texture, cx, cy, u + du, v + dv, w, h);
                cy += h;
            }
            cx += w;
        }
    }

    /** 1.20.1 GuiGraphics.slices: total split into ceil(total / piece) near-equal parts */
    private static it.unimi.dsi.fastutil.ints.IntIterator slices(int total, int piece) {
        return new net.minecraft.class_7836(total, net.minecraft.class_3532.method_38788(total, piece));
    }
}
