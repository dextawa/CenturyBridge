package top.dext.centurybridge.rt.v1_20_2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The 1.20.1 composite GUI textures, rebuilt on 1.20.2 from 1.20.2's own resources.
 *
 * 1.20.2 split widgets.png, icons.png, toasts.png and the rest into single sprites under
 * textures/gui/sprites, and cut the progress arrows, flames and buttons out of the container
 * textures that survived. A 1.20.1 mod draws regions of those composites by path and UV -- through
 * AbstractWidget.WIDGETS_LOCATION, Gui.GUI_ICONS_LOCATION, Toast.TEXTURE, ClientBundleTooltip.TEXTURE_LOCATION
 * and the like, or a literal path -- and on 1.20.2 got the missing-texture checkerboard, or nothing.
 * The table ({@link CbGuiLegacyTextureTable}, generated from the two client jars' pixels by
 * core/tools/gen_legacy_gui_textures.py) says where each 1.20.2 sprite sat in the 1.20.1 texture;
 * {@link CbGuiLegacyTexture} replays it whenever the texture loads, reading every source through the
 * resource manager, so a resource pack's sprites show up in the rebuilt composite (a pack that still
 * ships the 1.20.1 file itself keeps it: the file wins). A surviving texture keeps every pixel 1.20.2
 * draws; only its fully transparent pixels -- where the moved parts used to be -- are filled.
 *
 * Source art is resampled to the composite's resolution when a pack ships it at another scale.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiLegacyTextures {

    /** one 1.20.1 texture: its vanilla size, how it starts, and the ops that fill it */
    static final class Spec {
        final String path;
        final int width, height;
        final boolean gone;
        final String base;
        final List<String[]> ops = new ArrayList<>();

        Spec(String path, int width, int height, boolean gone, String base) {
            this.path = path;
            this.width = width;
            this.height = height;
            this.gone = gone;
            this.base = base;
        }
    }

    private static final Map<String, Spec> SPECS = parse(CbGuiLegacyTextureTable.OPS);

    private static Map<String, Spec> parse(String[] lines) {
        Map<String, Spec> out = new HashMap<>();
        Spec cur = null;
        for (String line : lines) {
            String[] p = line.split(" ");
            if (p[0].equals("T")) {
                cur = new Spec(p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3]), p[4].equals("gone"), p.length > 5 ? p[5] : null);
                out.put("textures/" + p[1], cur);
            } else {
                cur.ops.add(p);
            }
        }
        return out;
    }

    /** the 1.20.1 GUI texture this id names, or null when 1.20.2 still ships all of it */
    static Spec spec(net.minecraft.class_2960 id) {
        return id.method_12836().equals("minecraft") ? SPECS.get(id.method_12832()) : null;
    }

    /**
     * Whether a texture being registered under this id is a plain file texture (ResourceTexture) for
     * a 1.20.1 GUI texture: the bridge registers the rebuilt one in its place. Any other texture a mod
     * registered under the id is left alone.
     */
    public static boolean replaces(net.minecraft.class_2960 id, net.minecraft.class_1044 texture) {
        return texture != null && texture.getClass() == net.minecraft.class_1049.class && spec(id) != null;
    }

    /** the file texture for an id: the rebuilt composite for a 1.20.1 GUI texture, vanilla's otherwise */
    public static net.minecraft.class_1049 fileTexture(net.minecraft.class_2960 id) {
        Spec s = spec(id);
        return s != null ? new CbGuiLegacyTexture(id, s) : new net.minecraft.class_1049(id);
    }

    /** a 1.20.2 resource read as RGBA, or null when no pack has it */
    static net.minecraft.class_1011 read(net.minecraft.class_3300 manager, String path) throws IOException {
        Optional<net.minecraft.class_3298> res = manager.method_14486(new net.minecraft.class_2960("minecraft", "textures/" + path));
        if (res.isEmpty()) {
            return null;
        }
        try (InputStream in = res.get().method_14482()) {
            return net.minecraft.class_1011.method_4309(in);
        }
    }

    /**
     * Replay a spec's ops into img (the 1.20.2 file for a surviving texture, the successor or a blank
     * image for a gone one). C / S / N write only pixels that are still fully transparent; O blends.
     */
    static void compose(net.minecraft.class_3300 manager, Spec spec, net.minecraft.class_1011 img) throws IOException {
        double kx = (double) img.method_4307() / spec.width;
        double ky = (double) img.method_4323() / spec.height;
        Map<String, net.minecraft.class_1011> sources = new HashMap<>();
        try {
            for (String[] op : spec.ops) {
                String src = op[1];
                net.minecraft.class_1011 s = sources.containsKey(src) ? sources.get(src) : read(manager, src);
                sources.put(src, s);
                if (s == null) {
                    continue; // no pack provides this sprite: its region stays as it is
                }
                int sw = Integer.parseInt(op[2]);
                int sh = Integer.parseInt(op[3]);
                switch (op[0]) {
                    case "C" -> blit(img, kx, ky, s, sw, sh, 0, 0, sw, sh, Integer.parseInt(op[4]), Integer.parseInt(op[5]), -1, false);
                    case "S" -> blit(img, kx, ky, s, sw, sh, Integer.parseInt(op[4]), Integer.parseInt(op[5]),
                        Integer.parseInt(op[6]), Integer.parseInt(op[7]), Integer.parseInt(op[8]), Integer.parseInt(op[9]), -1, false);
                    case "N" -> blit(img, kx, ky, s, sw, sh, 0, 0, Integer.parseInt(op[7]), Integer.parseInt(op[8]),
                        Integer.parseInt(op[5]), Integer.parseInt(op[6]), Integer.parseInt(op[4]), false);
                    case "O" -> blit(img, kx, ky, s, sw, sh, 0, 0, sw, sh, Integer.parseInt(op[4]), Integer.parseInt(op[5]), -1, true);
                    default -> throw new IOException("legacy GUI texture table: unknown op " + op[0]);
                }
            }
        } finally {
            for (net.minecraft.class_1011 s : sources.values()) {
                if (s != null) {
                    s.close();
                }
            }
        }
    }

    /**
     * Draw a w x h region of a source (vanilla size sw x sh, from (sx, sy)) at (dx, dy) in vanilla
     * texture coordinates; border >= 0 draws the whole source nine-sliced to w x h instead (corners
     * native, edges and middle tiled, as 1.20.2's GuiGraphics draws a nine_slice sprite).
     */
    private static void blit(net.minecraft.class_1011 img, double kx, double ky, net.minecraft.class_1011 src, int sw, int sh,
                             int sx, int sy, int w, int h, int dx, int dy, int border, boolean blend) {
        double qx = (double) src.method_4307() / sw;
        double qy = (double) src.method_4323() / sh;
        int x0 = (int) Math.round(dx * kx), x1 = (int) Math.round((dx + w) * kx);
        int y0 = (int) Math.round(dy * ky), y1 = (int) Math.round((dy + h) * ky);
        for (int ty = Math.max(0, y0); ty < Math.min(img.method_4323(), y1); ty++) {
            double fy = (ty + 0.5) / ky - dy; // vanilla offset inside the region
            double vy = border >= 0 ? nine(fy, h, sh, border) : sy + fy;
            int py = Math.min(src.method_4323() - 1, (int) Math.floor(vy * qy));
            for (int tx = Math.max(0, x0); tx < Math.min(img.method_4307(), x1); tx++) {
                double fx = (tx + 0.5) / kx - dx;
                double vx = border >= 0 ? nine(fx, w, sw, border) : sx + fx;
                int px = Math.min(src.method_4307() - 1, (int) Math.floor(vx * qx));
                int c = src.method_4315(px, py);
                int d = img.method_4315(tx, ty);
                if (blend) {
                    img.method_4305(tx, ty, over(d, c));
                } else if ((d >>> 24) == 0) {
                    img.method_4305(tx, ty, (c >>> 24) == 0 ? 0 : c);
                }
            }
        }
    }

    /** source coordinate for target offset f (0 <= f < n) of a nine-slice of a size-long source */
    private static double nine(double f, int n, int size, int border) {
        int i = (int) Math.floor(f);
        double frac = f - i;
        int mid = size - 2 * border;
        int s;
        if (i < border) {
            s = i;
        } else if (i >= n - border) {
            s = size - (n - i);
        } else {
            s = border + (i - border) % mid;
        }
        return s + frac;
    }

    /** source-over in packed ABGR (the channel order does not matter to the arithmetic) */
    private static int over(int dst, int src) {
        int sa = src >>> 24;
        if (sa == 255) {
            return src;
        }
        if (sa == 0) {
            return dst;
        }
        int da = dst >>> 24;
        int oa = sa + da * (255 - sa) / 255;
        if (oa == 0) {
            return 0;
        }
        int out = oa << 24;
        for (int shift = 0; shift < 24; shift += 8) {
            int s = (src >>> shift) & 0xFF;
            int d = (dst >>> shift) & 0xFF;
            out |= ((s * sa + d * da * (255 - sa) / 255) / oa) << shift;
        }
        return out;
    }

    private CbGuiLegacyTextures() {
    }
}
