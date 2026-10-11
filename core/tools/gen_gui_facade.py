"""Generate the 26.2 DrawContext facade: the 1.20.x/1.21.x GuiGraphics API
re-expressed over the retained-mode GuiGraphicsExtractor.

26.2 replaced immediate-mode GuiGraphics with GuiGraphicsExtractor -- not a
rename, a model change -- but the drawing primitives survived almost 1:1
(fill/blit/text/item/tooltip/scissor/pose). A mod's DrawContext reference is
class-renamed to this facade by the converter; vanilla hands the facade an
extractor through the overrideHooks forwarders (Screen.render ->
extractRenderState). Each entry below is one old symbol as the corpus uses it
(intermediary token, old descriptor) with its 26.2 expression. Names that
survived to 1.21.11 are emitted under their mojang name because the jar
rename stage renamed the call sites too; dead-before tokens stay tokens.

  python3 gen_gui_facade.py <inter-to-mojang.tsv> <out-dir-of-26.2-tree>
"""
import io, os, re, sys

table, tree = sys.argv[1], sys.argv[2]
cmap, mmap = {}, {}
for line in io.open(table, encoding="utf-8"):
    k, i, m = line.rstrip("\n").split("\t")
    if k == "C":
        cmap[i] = m
    elif i.startswith("net/minecraft/class_332."):
        mmap[i.split(".", 1)[1]] = m.rsplit(".", 1)[1]

def jtype(desc):
    """one JVM descriptor -> Java source type (mojang names)"""
    dims = 0
    while desc.startswith("["):
        dims += 1; desc = desc[1:]
    prim = {"I": "int", "F": "float", "Z": "boolean", "V": "void", "D": "double", "J": "long", "B": "byte", "S": "short", "C": "char"}
    if desc in prim:
        t = prim[desc]
    else:
        inner = desc[1:-1]
        t = cmap.get(inner, inner).replace("/", ".").replace("$", ".")
    return t + "[]" * dims

def split_args(desc):
    args, i, s = [], 1, desc
    while s[i] != ")":
        j = i
        while s[j] == "[":
            j += 1
        if s[j] == "L":
            j = s.index(";", j)
        args.append(s[i:j + 1]); i = j + 1
    return args, s[i + 1:]

E = "ext"  # the extractor field
PIPE = "net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED"

# token, old descriptor, parameter names, body (Java statements; return value if non-void)
TABLE = [
    ("method_51448", "()Lcom/mojang/blaze3d/vertex/PoseStack;", [], "return poseAdapter;"),
    ("method_25294", "(IIIII)V", ["x1", "y1", "x2", "y2", "color"], f"{E}.fill(x1, y1, x2, y2, color);"),
    ("method_51439", "(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;IIIZ)I", ["font", "text", "x", "y", "color", "shadow"], f"{E}.text(font, text, x, y, color, shadow); return x + font.width(text);"),
    ("method_25302", "(Lnet/minecraft/class_2960;IIIIII)V", ["tex", "x", "y", "u", "v", "w", "h"], f"{E}.blit({PIPE}, tex, x, y, (float) u, (float) v, w, h, 256, 256);"),
    ("method_25290", "(Lnet/minecraft/class_2960;IIFFIIII)V", ["tex", "x", "y", "u", "v", "w", "h", "tw", "th"], f"{E}.blit({PIPE}, tex, x, y, u, v, w, h, tw, th);"),
    ("method_51433", "(Lnet/minecraft/class_327;Ljava/lang/String;IIIZ)I", ["font", "text", "x", "y", "color", "shadow"], f"if (text == null) return x; {E}.text(font, text, x, y, color, shadow); return x + font.width(text);"),
    ("method_35720", "(Lnet/minecraft/class_327;Lnet/minecraft/class_5481;III)I", ["font", "text", "x", "y", "color"], f"{E}.text(font, text, x, y, color, true); return x + font.width(text);"),
    ("method_25303", "(Lnet/minecraft/class_327;Ljava/lang/String;III)I", ["font", "text", "x", "y", "color"], f"if (text == null) return x; {E}.text(font, text, x, y, color, true); return x + font.width(text);"),
    ("method_51427", "(Lnet/minecraft/class_1799;II)V", ["stack", "x", "y"], f"{E}.item(stack, x, y);"),
    ("method_27534", "(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V", ["font", "text", "cx", "y", "color"], f"{E}.centeredText(font, text, cx, y, color);"),
    ("method_25296", "(IIIIII)V", ["x1", "y1", "x2", "y2", "c1", "c2"], f"{E}.fillGradient(x1, y1, x2, y2, c1, c2);"),
    ("method_51434", "(Lnet/minecraft/class_327;Ljava/util/List;II)V", ["font", "lines", "x", "y"], f"{E}.setComponentTooltipForNextFrame(font, (java.util.List<net.minecraft.network.chat.Component>) (java.util.List<?>) lines, x, y);"),
    ("method_27535", "(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)I", ["font", "text", "x", "y", "color"], f"{E}.text(font, text, x, y, color, true); return x + font.width(text);"),
    ("method_51422", "(FFFF)V", ["r", "g", "b", "a"], "/* retained-mode GUI has no global color multiplier; tint dropped */"),
    ("method_51430", "(Lnet/minecraft/class_327;Lnet/minecraft/class_5481;IIIZ)I", ["font", "text", "x", "y", "color", "shadow"], f"{E}.text(font, text, x, y, color, shadow); return x + font.width(text);"),
    ("method_25291", "(Lnet/minecraft/class_2960;IIIFFIIII)V", ["tex", "x", "y", "z", "u", "v", "w", "h", "tw", "th"], f"{E}.blit({PIPE}, tex, x, y, u, v, w, h, tw, th);"),
    ("method_25300", "(Lnet/minecraft/class_327;Ljava/lang/String;III)V", ["font", "text", "cx", "y", "color"], f"{E}.centeredText(font, text, cx, y, color);"),
    ("method_33284", "(IIIIIII)V", ["x1", "y1", "x2", "y2", "z", "c1", "c2"], f"{E}.fillGradient(x1, y1, x2, y2, c1, c2);"),
    ("method_51421", "()I", [], f"return {E}.guiWidth();"),
    ("method_51443", "()I", [], f"return {E}.guiHeight();"),
    ("method_51445", "(Lnet/minecraft/class_1799;II)V", ["stack", "x", "y"], f"{E}.fakeItem(stack, x, y);"),
    ("method_25293", "(Lnet/minecraft/class_2960;IIIIFFIIII)V", ["tex", "x", "y", "w", "h", "u", "v", "rw", "rh", "tw", "th"], f"{E}.blit(tex, x, y, w, h, u / (float) tw, (u + rw) / (float) tw, v / (float) th, (v + rh) / (float) th);"),
    ("method_51438", "(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;II)V", ["font", "text", "x", "y"], f"{E}.setTooltipForNextFrame(font, text, x, y);"),
    ("method_51431", "(Lnet/minecraft/class_327;Lnet/minecraft/class_1799;II)V", ["font", "stack", "x", "y"], f"{E}.itemDecorations(font, stack, x, y);"),
    # method_51450 getVertexConsumers(): MultiBufferSource.BufferSource itself is gone in 26.2's
    # retained GUI -- no type to declare, ledgered (23 corpus refs)
    ("method_44380", "()V", [], f"{E}.disableScissor();"),
    ("method_51446", "(Lnet/minecraft/class_327;Lnet/minecraft/class_1799;II)V", ["font", "stack", "x", "y"], f"{E}.setTooltipForNextFrame(font, stack, x, y);"),
    ("method_44379", "(IIII)V", ["x1", "y1", "x2", "y2"], f"{E}.enableScissor(x1, y1, x2, y2);"),
    ("method_51432", "(Lnet/minecraft/class_327;Lnet/minecraft/class_1799;IILjava/lang/String;)V", ["font", "stack", "x", "y", "label"], f"{E}.itemDecorations(font, stack, x, y, label);"),
    ("method_51440", "(Lnet/minecraft/class_327;Lnet/minecraft/class_5348;IIII)V", ["font", "text", "x", "y", "width", "color"], f"{E}.textWithWordWrap(font, text, x, y, width, color);"),
    ("method_51447", "(Lnet/minecraft/class_327;Ljava/util/List;II)V", ["font", "lines", "x", "y"], f"{E}.setTooltipForNextFrame(font, (java.util.List<net.minecraft.util.FormattedCharSequence>) (java.util.List<?>) lines, x, y);"),
    ("method_25298", "(IIIIILnet/minecraft/class_1058;)V", ["x", "y", "z", "w", "h", "sprite"], f"{E}.blitSprite({PIPE}, sprite, x, y, w, h);"),
    ("method_51437", "(Lnet/minecraft/class_327;Ljava/util/List;Ljava/util/Optional;II)V", ["font", "lines", "data", "x", "y"], f"{E}.setTooltipForNextFrame(font, (java.util.List<net.minecraft.network.chat.Component>) (java.util.List<?>) lines, (java.util.Optional<net.minecraft.world.inventory.tooltip.TooltipComponent>) (java.util.Optional<?>) data, x, y);"),
    ("method_51441", "(Lnet/minecraft/class_327;Lnet/minecraft/class_2583;II)V", ["font", "style", "x", "y"], "/* hover effects are collected by the extractor's text collector in 26.2; nothing to draw here */"),
    ("method_49601", "(IIIII)V", ["x", "y", "w", "h", "color"], f"{E}.outline(x, y, w, h, color);"),
    ("method_25301", "(IIII)V", ["x", "y1", "y2", "color"], f"{E}.verticalLine(x, y1, y2, color);"),
    ("method_25292", "(IIII)V", ["x1", "x2", "y", "color"], f"{E}.horizontalLine(x1, x2, y, color);"),
    ("method_49697", "(Lnet/minecraft/class_2960;IIIIIIIIII)V", ["tex", "tw", "th", "u", "v", "x", "y", "z", "w", "h"], f"{E}.blit({PIPE}, tex, x, y, (float) u, (float) v, w, h, tw, th);"),
    ("method_25295", "(IIIIII)V", ["x1", "y1", "x2", "y2", "z", "color"], f"{E}.fill(x1, y1, x2, y2, color);"),
    ("method_51436", "(Lnet/minecraft/class_327;Lnet/minecraft/class_1799;II)V", ["font", "stack", "x", "y"], f"{E}.setTooltipForNextFrame(font, stack, x, y);"),
    ("method_51435", "(Lnet/minecraft/class_327;Ljava/util/List;Ljava/util/Optional;II)V", ["font", "lines", "data", "x", "y"], f"{E}.setTooltipForNextFrame(font, (java.util.List<net.minecraft.network.chat.Component>) (java.util.List<?>) lines, (java.util.Optional<net.minecraft.world.inventory.tooltip.TooltipComponent>) (java.util.Optional<?>) data, x, y);"),
    ("method_51444", "(Lnet/minecraft/class_327;Lnet/minecraft/class_5481;III)V", ["font", "text", "cx", "y", "color"], f"{E}.centeredText(font, text, cx, y, color);"),
    ("method_51428", "(Lnet/minecraft/class_1799;III)V", ["stack", "x", "y", "seed"], f"{E}.item(stack, x, y, seed);"),
    ("method_51426", "(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;III)V", ["entity", "stack", "x", "y", "seed"], f"{E}.item(entity, stack, x, y, seed);"),
    ("method_25297", "(Lnet/minecraft/class_2960;IIIIII)V", ["tex", "x", "y", "u", "v", "w", "h"], f"{E}.blit({PIPE}, tex, x, y, (float) u, (float) v, w, h, 256, 256);"),
    # 1.20.2 nine-slice family (ported from DrawContextNineSliceBridge)
    ("method_48586", "(Lnet/minecraft/class_2960;IIIIIIIII)V", ["tex", "x", "y", "w", "h", "border", "regionW", "regionH", "u", "v"],
     "method_48587(tex, x, y, w, h, border, border, border, border, regionW, regionH, u, v);"),
    ("method_48587", "(Lnet/minecraft/class_2960;IIIIIIIIIIII)V", ["tex", "x", "y", "w", "h", "left", "top", "right", "bottom", "regionW", "regionH", "u", "v"],
     f"""if (w == regionW && h == regionH) {{ {E}.blit({PIPE}, tex, x, y, (float) u, (float) v, w, h, 256, 256); return; }}
        // corners
        {E}.blit({PIPE}, tex, x, y, (float) u, (float) v, left, top, 256, 256);
        {E}.blit({PIPE}, tex, x + w - right, y, (float) (u + regionW - right), (float) v, right, top, 256, 256);
        {E}.blit({PIPE}, tex, x, y + h - bottom, (float) u, (float) (v + regionH - bottom), left, bottom, 256, 256);
        {E}.blit({PIPE}, tex, x + w - right, y + h - bottom, (float) (u + regionW - right), (float) (v + regionH - bottom), right, bottom, 256, 256);
        // edges, tiled
        tile(tex, x + left, y, w - left - right, top, u + left, v, regionW - left - right, top);
        tile(tex, x + left, y + h - bottom, w - left - right, bottom, u + left, v + regionH - bottom, regionW - left - right, bottom);
        tile(tex, x, y + top, left, h - top - bottom, u, v + top, left, regionH - top - bottom);
        tile(tex, x + w - right, y + top, right, h - top - bottom, u + regionW - right, v + top, right, regionH - top - bottom);
        // center
        tile(tex, x + left, y + top, w - left - right, h - top - bottom, u + left, v + top, regionW - left - right, regionH - top - bottom);"""),
]

def emit_method(tok, desc, params, body):
    args, ret = split_args(desc)
    # our own classes keep token member names through the rename stage (the
    # remapper skips top/dext owners), so the facade declares tokens, always
    name = tok
    sig = ", ".join(f"{jtype(a)} {n}" for a, n in zip(args, params))
    return f"""    /** 1.20.x DrawContext.{tok} */
    @SuppressWarnings({{"unchecked", "unused"}})
    public {jtype(ret)} {name}({sig}) {{
        {body}
    }}
"""

seen = set()
methods = []
for tok, desc, params, body in TABLE:
    key = (tok, desc)
    if key in seen:
        continue
    seen.add(key)
    methods.append(emit_method(tok, desc, params, body))

src = f"""package top.dext.centurybridge.facade.v26_2;

/**
 * DrawContext (class_332 / GuiGraphics) as the corpus knows it, over the 26.2
 * retained-mode GuiGraphicsExtractor. Generated by core/tools/gen_gui_facade.py
 * -- edit the table there, not this file.
 *
 * Mods receive one of these wherever vanilla now hands a GuiGraphicsExtractor
 * (see overrideHooks in shims-26.2.json); the extractor is the single source
 * of truth, this object only translates calls.
 */
public class GuiGraphics {{

    private final net.minecraft.client.gui.GuiGraphicsExtractor {E};
    private final CbPoseStack poseAdapter;

    public GuiGraphics(net.minecraft.client.gui.GuiGraphicsExtractor extractor) {{
        this.{E} = extractor;
        this.poseAdapter = new CbPoseStack(extractor.pose());
    }}

    /** The wrapped extractor, for super-call bridges on Screen / AbstractWidget. */
    public net.minecraft.client.gui.GuiGraphicsExtractor cbExtractor() {{
        return {E};
    }}

    private void tile(net.minecraft.resources.Identifier tex, int x, int y, int w, int h, int u, int v, int tw, int th) {{
        if (w <= 0 || h <= 0 || tw <= 0 || th <= 0) {{
            return;
        }}
        for (int dx = 0; dx < w; dx += tw) {{
            for (int dy = 0; dy < h; dy += th) {{
                int cw = Math.min(tw, w - dx);
                int ch = Math.min(th, h - dy);
                {E}.blit({PIPE}, tex, x + dx, y + dy, (float) u, (float) v, cw, ch, 256, 256);
            }}
        }}
    }}

{"".join(methods)}}}
"""
os.makedirs(os.path.join(tree, "src/top/dext/centurybridge/facade/v26_2"), exist_ok=True)
io.open(os.path.join(tree, "src/top/dext/centurybridge/facade/v26_2/GuiGraphics.java"), "w", encoding="utf-8", newline="\n").write(src)

pose = """package top.dext.centurybridge.facade.v26_2;

/**
 * The PoseStack a 1.20.x mod gets from DrawContext.getMatrices(): every
 * transform is applied to the real stack (so last() stays meaningful for code
 * that reads it back) and mirrored onto the extractor's 2D Matrix3x2fStack,
 * which is what actually positions retained-mode GUI elements in 26.2.
 */
public final class CbPoseStack extends com.mojang.blaze3d.vertex.PoseStack {

    private final org.joml.Matrix3x2fStack mirror;

    CbPoseStack(org.joml.Matrix3x2fStack mirror) {
        this.mirror = mirror;
    }

    @Override
    public void pushPose() {
        super.pushPose();
        mirror.pushMatrix();
    }

    @Override
    public void popPose() {
        super.popPose();
        mirror.popMatrix();
    }

    @Override
    public void translate(float x, float y, float z) {
        super.translate(x, y, z);
        mirror.translate(x, y);
    }

    @Override
    public void translate(double x, double y, double z) {
        translate((float) x, (float) y, (float) z);
    }

    @Override
    public void scale(float x, float y, float z) {
        super.scale(x, y, z);
        mirror.scale(x, y);
    }

    @Override
    public void mulPose(org.joml.Quaternionfc q) {
        super.mulPose(q);
        org.joml.Vector3f e = new org.joml.Quaternionf(q).getEulerAnglesZYX(new org.joml.Vector3f());
        mirror.rotate(e.z);
    }
}
"""
io.open(os.path.join(tree, "src/top/dext/centurybridge/facade/v26_2/CbPoseStack.java"), "w", encoding="utf-8", newline="\n").write(pose)
print(f"facade: {len(methods)} methods -> {tree}/src/top/dext/centurybridge/facade/v26_2/GuiGraphics.java (+CbPoseStack)")
