#!/usr/bin/env python3
"""Generate the 1.20.2 layer's legacy GUI texture table (CbGuiLegacyTextureTable.java).

1.20.2 cut the composite GUI textures 1.20.1 drew from -- widgets.png, icons.png, toasts.png, the
container textures' progress/arrow/flame parts... -- into single sprites under textures/gui/sprites.
A 1.20.1 mod blits regions of those composites by texture path and UV (through AbstractWidget.WIDGETS_LOCATION,
Gui.GUI_ICONS_LOCATION, Toast.TEXTURE and the like, or a literal path), and gets the missing-texture
checkerboard -- or nothing, where a surviving texture lost the region -- on 1.20.2. The bridge rebuilds
each composite at runtime from the 1.20.2 resources (so resource packs apply) at the old coordinates.

This generator derives the layout from pixels, not by hand: for every 1.20.1 GUI texture that is gone in
1.20.2, or survives with regions 1.20.2 left transparent, it finds every 1.20.2 GUI png that occurs at an
exact position inside the 1.20.1 texture (RGBA-equal, fully transparent pixels compare equal), plus a short
list of composed placements (the 20x20 icon buttons 1.20.2 draws as button sprite + icon sprite). It then
replays the table exactly as the runtime will and reports, per texture, how many of the 1.20.1 texture's
visible pixels the synthesis reproduces -- and which regions it cannot, because 1.20.2 ships no art for them.

Ops (one string each; paths are minecraft: textures; sizes are the VANILLA 1.20.2 size of the source png,
so the runtime can resample a resource pack's higher-resolution art):
  T <path> <w> <h> <gone|fill> [base]   a texture; 'gone': absent in 1.20.2, built on [base] (a 1.20.2
                                        successor of the same size) or on transparency; 'fill': the 1.20.2
                                        file itself, with only its fully transparent pixels filled
  C <src> <srcW> <srcH> <dx> <dy>       copy the whole source png to (dx, dy); writes only where the
                                        texture is still fully transparent
  S <src> <srcW> <srcH> <sx> <sy> <w> <h> <dx> <dy>
                                        copy a w x h region of the source from (sx, sy); same write rule.
                                        Only taken from a RELATED sprite (the composite's own sprite
                                        folder: container/bundle.png <- sprites/container/bundle/*), for
                                        the leftovers no whole sprite covers (the bundle tooltip's 1-px
                                        border lines are the bundle panel sprite's fill and shade rows)
  N <src> <srcW> <srcH> <border> <dx> <dy> <w> <h>
                                        the source drawn nine-sliced to w x h at (dx, dy), as 1.20.2's
                                        GuiGraphics draws a nine_slice sprite; same write rule as C
  O <src> <srcW> <srcH> <dx> <dy>       the source alpha-blended over the texture at (dx, dy)

  python3 core/tools/gen_legacy_gui_textures.py [dataDir]    (dataDir default: $CB_DATA or data)
"""
import io
import os
import sys
import zipfile

import numpy as np
from PIL import Image

try:
    import cv2
except ImportError:  # pragma: no cover
    cv2 = None

OUT = "core/shims/1.20.2/src/top/dext/centurybridge/rt/v1_20_2/CbGuiLegacyTextureTable.java"
GUI = "assets/minecraft/textures/gui/"
# 1.20.1 textures nothing in 1.20.1 draws any more (Twitch streaming indicator)
SKIP = {"gui/stream_indicator.png"}
# a gone texture is built on a same-size 1.20.2 png only when that png reproduces most of it
SUCCESSOR_MIN_AGREEMENT = 0.5
# composed placements: (texture, x, y, w, h, background sprite, icon sprite) -- 1.20.2 draws these
# buttons as a nine-sliced button sprite with an icon sprite on top (SpriteIconButton.CenteredIcon)
COMPOSED = [
    ("gui/widgets.png", 0, 106, 20, 20, "gui/sprites/widget/button.png", "gui/sprites/icon/language.png"),
    ("gui/widgets.png", 0, 126, 20, 20, "gui/sprites/widget/button_highlighted.png", "gui/sprites/icon/language.png"),
    ("gui/accessibility.png", 0, 0, 20, 20, "gui/sprites/widget/button.png", "gui/sprites/icon/accessibility.png"),
    ("gui/accessibility.png", 0, 20, 20, 20, "gui/sprites/widget/button_highlighted.png", "gui/sprites/icon/accessibility.png"),
]
# the sprite folder that took over each composite's leftovers, for S ops: the bundle tooltip's border
# lines are its panel sprite's fill and shade, the villager screen's bars its experience-bar sprites.
# Matching pixels anywhere else would only find the same colour by accident.
RELATED = {
    "gui/container/bundle.png": ("gui/sprites/container/bundle/",),
    "gui/container/villager2.png": ("gui/sprites/container/villager/",),
}
# the regions 1.20.1's own code cuts from those composites (u, v, w, h[, layers]), applied before any
# whole-sprite match so a part comes from its own sprites: matched whole against a related region
# (exactly, else the closest related region when it agrees on >= PART_NEAR of the part -- the same
# element with a few pixels recoloured -- reported as such), or built from explicit layers, the way
# 1.20.2 draws that element: ("S", src, sx, sy) a region of src, ("O", src) src blended on top
PARTS = {
    # ClientBundleTooltip.Texture: SLOT and BLOCKED_SLOT (1.20.2 draws the panel sprite under the slot
    # sprite, whose two bottom rows are transparent), BORDER_VERTICAL, BORDER_HORIZONTAL_TOP,
    # BORDER_HORIZONTAL_BOTTOM, BORDER_CORNER_TOP, BORDER_CORNER_BOTTOM
    "gui/container/bundle.png": [
        (0, 0, 18, 20, [("S", "gui/sprites/container/bundle/background.png", 4, 4), ("O", "gui/sprites/container/bundle/slot.png")]),
        (0, 40, 18, 20, [("S", "gui/sprites/container/bundle/background.png", 4, 4), ("O", "gui/sprites/container/bundle/blocked_slot.png")]),
        (0, 18, 1, 20), (0, 20, 18, 1), (0, 60, 18, 1), (0, 20, 1, 1), (0, 60, 1, 1)],
    # MerchantScreen.renderProgressBar: experience background, current, and the trade's result
    "gui/container/villager2.png": [(0, 186, 102, 5), (0, 191, 102, 5), (0, 182, 102, 3)],
}
PART_NEAR = 0.95


def load_jar(path):
    out = {}
    with zipfile.ZipFile(path) as z:
        for n in z.namelist():
            if n.startswith(GUI) and n.endswith(".png"):
                a = np.array(Image.open(io.BytesIO(z.read(n))).convert("RGBA"), dtype=np.int32)
                a[a[:, :, 3] == 0] = 0
                out["gui/" + n[len(GUI):]] = a
            elif n.startswith(GUI) and n.endswith(".png.mcmeta"):
                out["gui/" + n[len(GUI):]] = z.read(n).decode("utf-8")
    return out


def find(comp, spr):
    """exact-match positions of spr in comp"""
    H, W, _ = comp.shape
    h, w, _ = spr.shape
    if h > H or w > W:
        return []
    acc = None
    for c in range(4):
        r = cv2.matchTemplate(comp[:, :, c].astype(np.float32), spr[:, :, c].astype(np.float32), cv2.TM_SQDIFF)
        acc = r if acc is None else acc + r
    # float32 sums of squares lose integer precision on large templates: filter loosely, verify exactly
    tol = 1e-4 * float((spr.astype(np.float64) ** 2).sum()) + 1.0
    ys, xs = np.where(acc < tol)
    return [(x, y) for y, x in zip(ys.tolist(), xs.tolist()) if np.array_equal(comp[y:y + h, x:x + w], spr)]


def nine_index(n, size, border):
    """source index per target index along one axis: borders native, the middle tiled"""
    mid = size - 2 * border
    out = []
    for i in range(n):
        if i < border:
            out.append(i)
        elif i >= n - border:
            out.append(size - (n - i))
        else:
            out.append(border + (i - border) % mid)
    return out


def over(dst, src):
    """alpha-over, integer math as the runtime does it"""
    sa = src[:, :, 3:4]
    da = dst[:, :, 3:4]
    oa = sa + da * (255 - sa) // 255
    safe = np.where(oa == 0, 1, oa)
    rgb = (src[:, :, :3] * sa + dst[:, :, :3] * da * (255 - sa) // 255) // safe
    res = np.concatenate([rgb, oa], axis=2)
    res[oa[:, :, 0] == 0] = 0
    return res


def replay(spec, ops, new, base):
    """the runtime's composition, on vanilla 1.20.2 resources"""
    _, path, w, h, mode = spec[:5]
    img = base.copy() if base is not None else np.zeros((h, w, 4), dtype=np.int32)
    for op in ops:
        k = op[0]
        src = new[op[1]]
        if k == "C":
            dx, dy = op[4], op[5]
            sh, sw, _ = src.shape
            reg = img[dy:dy + sh, dx:dx + sw]
            hole = reg[:, :, 3] == 0
            reg[hole] = src[:reg.shape[0], :reg.shape[1]][hole]
        elif k == "S":
            sx, sy, w, h, dx, dy = op[4], op[5], op[6], op[7], op[8], op[9]
            reg = img[dy:dy + h, dx:dx + w]
            hole = reg[:, :, 3] == 0
            reg[hole] = src[sy:sy + h, sx:sx + w][hole]
        elif k == "N":
            border, dx, dy, tw, th = op[4], op[5], op[6], op[7], op[8]
            sh, sw, _ = src.shape
            ys = nine_index(th, sh, border)
            xs = nine_index(tw, sw, border)
            part = src[np.ix_(ys, xs)]
            reg = img[dy:dy + th, dx:dx + tw]
            hole = reg[:, :, 3] == 0
            reg[hole] = part[hole]
        elif k == "O":
            dx, dy = op[4], op[5]
            sh, sw, _ = src.shape
            img[dy:dy + sh, dx:dx + sw] = over(img[dy:dy + sh, dx:dx + sw], src)
    return img


def regions(mask):
    from scipy import ndimage
    lab, n = ndimage.label(mask, structure=np.ones((3, 3)))
    out = []
    for i, sl in enumerate(ndimage.find_objects(lab)):
        ys, xs = sl
        out.append(f"{xs.start},{ys.start} {xs.stop - xs.start}x{ys.stop - ys.start} ({int((lab[sl] == i + 1).sum())} px)")
    return out


def related(path, sprite):
    """a sprite of the composite's own folder (RELATED below): the only sources S ops may cut from"""
    return any(sprite.startswith(d) for d in RELATED.get(path, ()))


def apply_parts(path, o, new_png, notes):
    """S / O ops for the 1.20.1 parts of PARTS, and the mask they cover"""
    ops = []
    covered = np.zeros(o.shape[:2], dtype=bool)
    cands = [(p, a) for p, a in sorted(new_png.items()) if related(path, p)]
    for part in PARTS.get(path, ()):
        x0, y0, w, h = part[:4]
        want = o[y0:y0 + h, x0:x0 + w]
        if len(part) > 4:
            for layer in part[4]:
                src = new_png[layer[1]]
                if layer[0] == "S":
                    ops.append(("S", layer[1], src.shape[1], src.shape[0], layer[2], layer[3], w, h, x0, y0))
                else:
                    ops.append(("O", layer[1], src.shape[1], src.shape[0], x0, y0))
            covered[y0:y0 + h, x0:x0 + w] = True
            continue
        best = None
        for p, a in cands:
            H, W, _ = a.shape
            for sy in range(H - h + 1):
                for sx in range(W - w + 1):
                    agree = int((np.abs(a[sy:sy + h, sx:sx + w] - want).sum(axis=2) == 0).sum())
                    if best is None or agree > best[0]:
                        best = (agree, ("S", p, W, H, sx, sy, w, h, x0, y0))
                    if agree == w * h:
                        break
                if best and best[0] == w * h:
                    break
            if best and best[0] == w * h:
                break
        if best and best[0] >= PART_NEAR * w * h:
            ops.append(best[1])
            covered[y0:y0 + h, x0:x0 + w] = True
            if best[0] < w * h:
                notes.append(f"part {x0},{y0} {w}x{h} from {best[1][1]} region {best[1][4]},{best[1][5]}: "
                             f"{best[0]}/{w * h} px equal (recoloured in 1.20.2)")
    return ops, covered


def main():
    data = sys.argv[1] if len(sys.argv) > 1 else os.environ.get("CB_DATA", "data")
    old = load_jar(os.path.join(data, "jars", "client-1.20.1-obf.jar"))
    new = load_jar(os.path.join(data, "jars", "client-1.20.2-obf.jar"))
    old_png = {p: a for p, a in old.items() if p.endswith(".png")}
    new_png = {p: a for p, a in new.items() if p.endswith(".png")}
    lines = []
    report = []
    for path in sorted(old_png):
        if path in SKIP:
            continue
        o = old_png[path]
        H, W, _ = o.shape
        vis = o[:, :, 3] > 0
        if path in new_png:
            base = new_png[path]
            if base.shape != o.shape:
                continue
            lost = vis & (base[:, :, 3] == 0)
            if not lost.any():
                continue
            spec = ("T", path, W, H, "fill")
            basename = None
        else:
            spec = ("T", path, W, H, "gone")
            base = None
            basename = None
            best = None
            for p, a in new_png.items():
                if a.shape == o.shape and not p.startswith("gui/sprites/"):
                    agree = (vis & (np.abs(o - a).sum(axis=2) == 0)).sum()
                    if best is None or agree > best[0]:
                        best = (agree, p)
            if best and best[0] >= SUCCESSOR_MIN_AGREEMENT * vis.sum():
                basename = best[1]
                base = new_png[basename]
        cur = base.copy() if base is not None else np.zeros_like(o)
        # what the base already gets right is not wanted from a sprite
        need = vis & ~((np.abs(cur - o).sum(axis=2) == 0) & (cur[:, :, 3] > 0))
        notes = []
        # the parts 1.20.1's code draws come from their own sprites first
        ops, covered = apply_parts(path, o, new_png, notes)
        for (cp, x, y, w, h, bg, icon) in COMPOSED:
            if cp != path:
                continue
            b = new_png[bg]
            ic = new_png[icon]
            border = int(new[bg + ".mcmeta"].split('"border"')[1].split(":")[1].split(",")[0].split("}")[0].strip()) \
                if (bg + ".mcmeta") in new else 0
            bestc = None
            for ox in range(0, w - ic.shape[1] + 1):
                for oy in range(0, h - ic.shape[0] + 1):
                    trial = [("N", bg, b.shape[1], b.shape[0], border, x, y, w, h),
                             ("O", icon, ic.shape[1], ic.shape[0], x + ox, y + oy)]
                    img = replay(spec, trial, new_png, None if base is None else base)
                    miss = int((np.abs(img[y:y + h, x:x + w] - o[y:y + h, x:x + w]).sum(axis=2) > 0).sum())
                    if bestc is None or miss < bestc[0]:
                        bestc = (miss, trial)
            ops.extend(bestc[1])
            covered[y:y + h, x:x + w] = True
        for sp, spr in sorted(new_png.items(), key=lambda kv: (-kv[1].shape[0] * kv[1].shape[1], kv[0])):
            if sp == path or sp == basename or spr[:, :, 3].max() == 0:
                continue
            pos = find(o, spr)
            if not pos or len(pos) > 16:
                continue
            h, w, _ = spr.shape
            for x, y in pos:
                gain = (need[y:y + h, x:x + w] & ~covered[y:y + h, x:x + w]).sum()
                if gain == 0:
                    continue
                ops.append(("C", sp, w, h, x, y))
                covered[y:y + h, x:x + w] |= need[y:y + h, x:x + w]
        if not ops:
            continue
        img = replay(spec, ops, new_png, base)
        exact = vis & (np.abs(img - o).sum(axis=2) == 0)
        extra = (~vis) & (img[:, :, 3] > 0)
        missing = vis & ~exact
        report.append(f"{path} ({mode_word(spec, basename)}): {int(exact.sum())}/{int(vis.sum())} visible 1.20.1 px reproduced"
                      + (f", {int(extra.sum())} px visible where 1.20.1 was transparent" if extra.any() else "")
                      + ("" if not missing.any() else "; not reproduced: " + "; ".join(regions(missing)))
                      + "".join("; " + n for n in notes))
        lines.append(" ".join(str(v) for v in spec) + ("" if basename is None else " " + basename))
        for op in ops:
            lines.append(" ".join(str(v) for v in op))
    with open(OUT, "w", encoding="utf-8", newline="\n") as f:
        f.write("package top.dext.centurybridge.rt.v1_20_2;\n\n")
        f.write("/**\n * Generated by core/tools/gen_legacy_gui_textures.py from the 1.20.1 and 1.20.2 client jars -- do not edit.\n")
        f.write(" * The op grammar is documented there; {@link CbGuiLegacyTextures} replays it on the live resources.\n *\n")
        for r in report:
            f.write(" * " + r.replace("*/", "* /") + "\n")
        f.write(" */\n@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)\nfinal class CbGuiLegacyTextureTable {\n\n")
        f.write("    static final String[] OPS = {\n")
        for ln in lines:
            f.write(f'        "{ln}",\n')
        f.write("    };\n\n    private CbGuiLegacyTextureTable() {\n    }\n}\n")
    print("\n".join(report))
    print(f"{sum(1 for ln in lines if ln.startswith('T '))} textures, {sum(1 for ln in lines if not ln.startswith('T '))} ops -> {OUT}")


def mode_word(spec, basename):
    if spec[4] == "fill":
        return "survives; transparent pixels filled"
    return "gone; built on " + basename if basename else "gone"


if __name__ == "__main__":
    main()
