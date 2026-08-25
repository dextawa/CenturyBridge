"""Generate the DrawContext (class_332) overload bridge for an intermediary-era
tree: the 1.20.1 draw API re-added as overloads over the tree's own form.

Two eras. 1.21.2 .. 1.21.5 take a Function<Identifier,RenderLayer> first
("function"); 1.21.6+ take a RenderPipeline, return void from the text
methods and hand out a Matrix3x2fStack instead of a PoseStack ("pipeline").
Everything here is deterministic -- the model was the wrong tool for the
biggest client-side cluster in the corpus, and its bodies got pruned.

  python3 gen_drawcontext_bridge.py <tree-version> function|pipeline
"""
import io, json, os, sys

ver, era = sys.argv[1], sys.argv[2]
pkg = "v" + ver.replace(".", "_")
tree = f"core/shims/{ver}"
SELF = "((net.minecraft.class_332) (Object) this)"
LAYER = "net.minecraft.class_1921::method_62277" if era == "function" else "net.minecraft.class_10799.field_56883"
FONT_W_STR = "method_1727"      # Font.getWidth(String)
FONT_W_VIS = "method_27525"     # Font.getWidth(StringVisitable)
FONT_W_ORD = "method_30880"     # Font.getWidth(OrderedText)

# (token, old descriptor, param names, body) -- covers entry = class_332.token+desc
COMMON = [
    ("method_25302", "(Lnet/minecraft/class_2960;IIIIII)V", ["tex", "x", "y", "u", "v", "w", "h"],
     f"{SELF}.method_25290({LAYER}, tex, x, y, (float) u, (float) v, w, h, 256, 256);"),
    ("method_25290", "(Lnet/minecraft/class_2960;IIFFIIII)V", ["tex", "x", "y", "u", "v", "w", "h", "tw", "th"],
     f"{SELF}.method_25290({LAYER}, tex, x, y, u, v, w, h, tw, th);"),
    ("method_25291", "(Lnet/minecraft/class_2960;IIIFFIIII)V", ["tex", "x", "y", "z", "u", "v", "w", "h", "tw", "th"],
     f"{SELF}.method_25290({LAYER}, tex, x, y, u, v, w, h, tw, th);"),
    ("method_25293", "(Lnet/minecraft/class_2960;IIIIFFIIII)V", ["tex", "x", "y", "w", "h", "u", "v", "rw", "rh", "tw", "th"],
     f"{SELF}.method_25302({LAYER}, tex, x, y, u, v, w, h, rw, rh, tw, th);"),
    ("method_51422", "(FFFF)V", ["r", "g", "b", "a"],
     "/* the retained GUI pipeline has no global colour multiplier; tint dropped */"),
    ("method_51440", "(Lnet/minecraft/class_327;Lnet/minecraft/class_5348;IIII)V", ["font", "text", "x", "y", "width", "color"],
     f"{SELF}.method_51440(font, text, x, y, width, color, true);"),
    ("method_51437", "(Lnet/minecraft/class_327;Ljava/util/List;Ljava/util/Optional;II)V", ["font", "lines", "data", "x", "y"],
     f"{SELF}.method_51437(font, (java.util.List<net.minecraft.class_2561>) (java.util.List<?>) lines, (java.util.Optional<net.minecraft.class_5632>) (java.util.Optional<?>) data, x, y, (net.minecraft.class_2960) null);"),
]
PIPELINE_ONLY = [
    # one adapter per context: a mod pushes on getMatrices() and pops on a later
    # getMatrices(), so both calls must hand back the same stack
    ("method_51448", "()Lnet/minecraft/class_4587;", [],
     f"if (cb$pose == null) {{ cb$pose = new CbPoseStack({SELF}.method_51448()); }} return cb$pose;"),
    ("method_51433", "(Lnet/minecraft/class_327;Ljava/lang/String;IIIZ)I", ["font", "text", "x", "y", "color", "shadow"],
     f"if (text == null) return x; {SELF}.method_51433(font, text, x, y, color, shadow); return x + font.{FONT_W_STR}(text);"),
    ("method_51439", "(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;IIIZ)I", ["font", "text", "x", "y", "color", "shadow"],
     f"{SELF}.method_51439(font, text, x, y, color, shadow); return x + font.{FONT_W_VIS}(text);"),
    ("method_35720", "(Lnet/minecraft/class_327;Lnet/minecraft/class_5481;III)I", ["font", "text", "x", "y", "color"],
     f"{SELF}.method_35720(font, text, x, y, color); return x + font.{FONT_W_ORD}(text);"),
    ("method_25303", "(Lnet/minecraft/class_327;Ljava/lang/String;III)I", ["font", "text", "x", "y", "color"],
     f"if (text == null) return x; {SELF}.method_25303(font, text, x, y, color); return x + font.{FONT_W_STR}(text);"),
    ("method_27535", "(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)I", ["font", "text", "x", "y", "color"],
     f"{SELF}.method_27535(font, text, x, y, color); return x + font.{FONT_W_VIS}(text);"),
    ("method_51430", "(Lnet/minecraft/class_327;Lnet/minecraft/class_5481;IIIZ)I", ["font", "text", "x", "y", "color", "shadow"],
     f"{SELF}.method_51430(font, text, x, y, color, shadow); return x + font.{FONT_W_ORD}(text);"),
    ("method_33284", "(IIIIIII)V", ["x1", "y1", "x2", "y2", "z", "c1", "c2"], f"{SELF}.method_25296(x1, y1, x2, y2, c1, c2);"),
]
TABLE = COMMON + (PIPELINE_ONLY if era == "pipeline" else [])

PRIM = {"I": "int", "F": "float", "Z": "boolean", "V": "void", "D": "double", "J": "long"}
def jtype(d):
    if d in PRIM:
        return PRIM[d]
    return d[1:-1].replace("/", ".").replace("$", ".")
def split_args(desc):
    args, i = [], 1
    while desc[i] != ")":
        j = i
        while desc[j] == "[":
            j += 1
        if desc[j] == "L":
            j = desc.index(";", j)
        args.append(desc[i:j + 1]); i = j + 1
    return args, desc[i + 1:]

methods, covers = [], []
for tok, desc, names, body in TABLE:
    args, ret = split_args(desc)
    sig = ", ".join(f"{jtype(a)} {n}" for a, n in zip(args, names))
    methods.append(f"""    /** 1.20.1 DrawContext.{tok} */
    @SuppressWarnings({{"unchecked", "unused"}})
    public {jtype(ret)} {tok}({sig}) {{
        {body}
    }}
""")
    covers.append(f"net/minecraft/class_332.{tok}{desc}")

src = f"""package top.dext.centurybridge.shims.{pkg};

import org.spongepowered.asm.mixin.Mixin;

/**
 * The 1.20.1 DrawContext draw API as overloads on class_332 ({era} era).
 * Generated by core/tools/gen_drawcontext_bridge.py -- edit the table there.
 */
@Mixin(net.minecraft.class_332.class)
public abstract class DrawContextBridge {{
{("    @org.spongepowered.asm.mixin.Unique private top.dext.centurybridge.rt." + pkg + ".CbPoseStack cb$pose;" + chr(10)) if era == "pipeline" else ""}
{"".join(methods)}}}
"""
d = f"{tree}/src/top/dext/centurybridge/shims/{pkg}"
io.open(os.path.join(d, "DrawContextBridge.java"), "w", encoding="utf-8", newline="\n").write(src)

if era == "pipeline":
    pose = f"""package top.dext.centurybridge.shims.{pkg};

/**
 * The PoseStack a 1.20.1 mod gets from DrawContext.getMatrices() past 1.21.6:
 * every transform is applied to the real stack (last() stays meaningful) and
 * mirrored onto the context's 2D Matrix3x2fStack, which is what positions the
 * retained-mode GUI. Lives in the mixin package only so the bridge can name it
 * without an import; it is a plain class, not a mixin.
 */
public final class CbPoseStack extends net.minecraft.class_4587 {{
    private final org.joml.Matrix3x2fStack mirror;

    public CbPoseStack(org.joml.Matrix3x2fStack mirror) {{
        this.mirror = mirror;
    }}

    @Override
    public void method_22903() {{
        super.method_22903();
        mirror.pushMatrix();
    }}

    @Override
    public void method_22909() {{
        super.method_22909();
        mirror.popMatrix();
    }}

    @Override
    public void method_46416(float x, float y, float z) {{
        super.method_46416(x, y, z);
        mirror.translate(x, y);
    }}

    @Override
    public void method_22904(double x, double y, double z) {{
        method_46416((float) x, (float) y, (float) z);
    }}

    @Override
    public void method_22905(float x, float y, float z) {{
        super.method_22905(x, y, z);
        mirror.scale(x, y);
    }}

    @Override
    public void method_22907(org.joml.Quaternionfc q) {{
        super.method_22907(q);
        mirror.rotate(new org.joml.Quaternionf(q).getEulerAnglesZYX(new org.joml.Vector3f()).z);
    }}
}}
"""
    # a plain class inside the mixin package would be transformed as a mixin;
    # keep it under rt/ instead and reference it fully qualified
    rt = f"{tree}/src/top/dext/centurybridge/rt/{pkg}"
    pose = pose.replace(f"package top.dext.centurybridge.shims.{pkg};", f"package top.dext.centurybridge.rt.{pkg};")
    io.open(os.path.join(rt, "CbPoseStack.java"), "w", encoding="utf-8", newline="\n").write(pose)
    s = io.open(os.path.join(d, "DrawContextBridge.java"), encoding="utf-8").read()
    s = s.replace("new CbPoseStack(", f"new top.dext.centurybridge.rt.{pkg}.CbPoseStack(")
    io.open(os.path.join(d, "DrawContextBridge.java"), "w", encoding="utf-8", newline="\n").write(s)

cfgp = f"{tree}/resources/cb-{ver}.mixins.json"
cfg = json.load(io.open(cfgp, encoding="utf-8"))
if "DrawContextBridge" not in cfg.get("client", []) and "DrawContextBridge" not in cfg.get("mixins", []):
    cfg.setdefault("client" if "client" in cfg else "mixins", []).append("DrawContextBridge")
io.open(cfgp, "w", encoding="utf-8", newline="\n").write(json.dumps(cfg, indent=1))
wp = f"core/segments/shims-{ver}.json"
w = json.load(io.open(wp, encoding="utf-8"))
cv = set(w.get("covers", []))
for c in covers:
    cv.add(c); w.get("ledger", {}).pop(c, None)
w["covers"] = sorted(cv)
io.open(wp, "w", encoding="utf-8", newline="\n").write(json.dumps(w, indent=1, ensure_ascii=False))
print(f"{ver} ({era}): {len(methods)} overloads on class_332, {len(covers)} covers")
