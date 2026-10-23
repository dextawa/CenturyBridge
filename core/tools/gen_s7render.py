"""gen_s7render.py: generated wiring of climb step 1.21.1 -> 1.21.2, area s7render (client/renderer, client/model,
blaze3d, the model-bake classes).

  [CB_DATA=<data dir>] python gen_s7render.py

Reads V's (1.21.1) and W's (1.21.2) intermediary stubs and V's code jar, and writes
core/segments/steps/1.21.1__1.21.2.json.d/s7render-gen.json plus the generated layer sources it names
(rt/CbRenderModelsGen.java). Nothing in either is listed by hand: every key is read off the jars.

Family 1 -- entity model animation (EntityModel.setupAnim / prepareMobModel).
  1.21.1: setupAnim(T entity, limbAngle, limbDistance, ageInTicks, headYaw, headPitch), prepareMobModel(T entity,
  limbAngle, limbDistance, partialTick), both called by the renderer that drew the model. 1.21.2: setupAnim(S state).
  A model class of a 1.21.1 mod overrides setupAnim at the erasure of the model class it extends (HumanoidModel's is
  (LivingEntity, 5 floats), QuadrupedModel's (Entity, 5 floats)); javac's bridges in V tie every erasure to
  EntityModel's. For each erasure V declares (the non-bridge member of EntityModel.setupAnim's bridge family in some
  model class), one override hook: a mod model under that class that declares it gets 1.21.2's
  EntityModel.setupAnim(EntityRenderState) synthesized, which calls CbRenderModels.setupAnim through the adapter
  CbRenderModelsGen.Anim<n> (the erasure's own shape, the mod's method implements it). prepareMobModel is called from
  there with the same entity -- it has no 1.21.2 caller of its own, and a model that does not override it inherits
  1.21.1's empty one (the adapter's default).
"""
import json
import os
import struct
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

DATA = os.environ.get("CB_DATA", "data")
HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
STEP = "top/dext/centurybridge/step/v1_21_2"
RT = STEP + "/rt"
FRAGMENT = os.path.join(ROOT, "core/segments/steps/1.21.1__1.21.2.json.d/s7render-gen.json")
RT_SRC = os.path.join(ROOT, "core/shims/steps/1.21.2/src", RT)
MC = "net/minecraft/"
ENTITY_MODEL = MC + "class_583"
ENTITY = MC + "class_1297"
STATE = MC + "class_10017"
SETUP = ("method_2819", "(L" + ENTITY + ";FFFFF)V")
PREPARE = ("method_2816", "(L" + ENTITY + ";FFF)V")
W_SETUP = ("method_2819", "(L" + STATE + ";)V")
ACC_BRIDGE, ACC_SYNTH = 0x40, 0x1000


def load(jar, code=False):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith(MC):
                c = classfile.read(z.read(n), code)
                if c:
                    out[c.name] = c
    return out


def chain(c, J):
    out, todo = [], [c]
    while todo:
        x = todo.pop(0)
        if x is None or x in out or x not in J:
            continue
        out.append(x)
        todo.append(J[x].super)
        todo.extend(J[x].interfaces)
    return out


def _cp_methodrefs(b):
    """constant-pool index -> (owner, name, desc) of every Methodref / InterfaceMethodref"""
    n = struct.unpack(">H", b[8:10])[0]
    i, idx = 10, 1
    utf8, cls, nat, ref = {}, {}, {}, {}
    while idx < n:
        t = b[i]
        if t == 1:
            ln = struct.unpack(">H", b[i + 1:i + 3])[0]
            utf8[idx] = b[i + 3:i + 3 + ln].decode("utf-8", "replace")
            i += 3 + ln
        elif t in (3, 4):
            i += 5
        elif t in (5, 6):
            i += 9
            idx += 1
        elif t == 7:
            cls[idx] = struct.unpack(">H", b[i + 1:i + 3])[0]
            i += 3
        elif t in (8, 16, 19, 20):
            i += 3
        elif t == 12:
            nat[idx] = struct.unpack(">HH", b[i + 1:i + 5])
            i += 5
        elif t in (10, 11):
            ref[idx] = struct.unpack(">HH", b[i + 1:i + 5])
            i += 5
        elif t in (9, 17, 18):
            i += 5
        elif t == 15:
            i += 4
        idx += 1
    return {k: (utf8[cls[c]], utf8[nat[nt][0]], utf8[nat[nt][1]]) for k, (c, nt) in ref.items()}


def _cp_fieldrefs(b):
    """constant-pool index -> (owner, name, desc) of every Fieldref"""
    n = struct.unpack(">H", b[8:10])[0]
    i, idx = 10, 1
    utf8, cls, nat, ref = {}, {}, {}, {}
    while idx < n:
        t = b[i]
        if t == 1:
            ln = struct.unpack(">H", b[i + 1:i + 3])[0]
            utf8[idx] = b[i + 3:i + 3 + ln].decode("utf-8", "replace")
            i += 3 + ln
        elif t in (3, 4):
            i += 5
        elif t in (5, 6):
            i += 9
            idx += 1
        elif t == 7:
            cls[idx] = struct.unpack(">H", b[i + 1:i + 3])[0]
            i += 3
        elif t in (8, 16, 19, 20):
            i += 3
        elif t == 12:
            nat[idx] = struct.unpack(">HH", b[i + 1:i + 5])
            i += 5
        elif t == 9:
            ref[idx] = struct.unpack(">HH", b[i + 1:i + 5])
            i += 5
        elif t in (10, 11, 17, 18):
            i += 5
        elif t == 15:
            i += 4
        idx += 1
    return {k: (utf8[cls[c]], utf8[nat[nt][0]], utf8[nat[nt][1]]) for k, (c, nt) in ref.items()}


def constant_returns(jar, wanted):
    """{(class, name, desc): (owner, field)} for each wanted method whose whole body is `getstatic owner.field; areturn`"""
    out = {}
    by_class = {}
    for c, n, d in wanted:
        by_class.setdefault(c, set()).add((n, d))
    with zipfile.ZipFile(jar) as z:
        for c, ms in by_class.items():
            b = z.read(c + ".class")
            ci = classfile.read(b, True)
            refs = None
            for m in ci.methods:
                if (m.name, m.desc) in ms and m.code and len(m.code) == 4 and m.code[0] == 0xb2 and m.code[3] == 0xb0:
                    refs = refs or _cp_fieldrefs(b)
                    f = refs.get(struct.unpack(">H", m.code[1:3])[0])
                    if f:
                        out[(c, m.name, m.desc)] = (f[0], f[1])
    return out


def _first_invoke(code, refs):
    """the first invokevirtual/invokespecial/invokeinterface of a method body (javac's bridge calls exactly one)"""
    i = 0
    while i < len(code):
        op = code[i]
        if op in (0xb6, 0xb7, 0xb9):
            return refs.get(struct.unpack(">H", code[i + 1:i + 3])[0])
        if op == 0xaa or op == 0xab or op == 0xc4:
            return None  # no switch / wide in a bridge
        i += 1 + classfile._LEN[op]
    return None


def bridge_targets(jar):
    """class -> {(bridge name, desc): (target name, desc)}: javac's bridges, each calling one method of its own class"""
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if not (n.endswith(".class") and n.startswith(MC)):
                continue
            b = z.read(n)
            ci = classfile.read(b, True)
            if not ci:
                continue
            refs = None
            for m in ci.methods:
                if m.access & (ACC_BRIDGE | ACC_SYNTH) and m.code and not m.name.startswith("lambda$"):
                    refs = refs or _cp_methodrefs(b)
                    t = _first_invoke(m.code, refs)
                    if t and t[0] == ci.name:
                        out.setdefault(ci.name, {})[(m.name, m.desc)] = (t[1], t[2])
    return out


def jtype(d):
    """Java source type of a field descriptor"""
    prim = {"F": "float", "I": "int", "Z": "boolean", "D": "double", "J": "long", "V": "void"}
    if d in prim:
        return prim[d]
    assert d.startswith("L") and d.endswith(";"), d
    return d[1:-1].replace("/", ".").replace("$", ".")


# ---------------------------------------------------------------------------------------------------------------------
# Family 2 -- getTextureLocation (EntityRenderer / RenderLayerParent / the renderers' own erasures), and
# Family 3 -- EntityRenderer.createRenderState(), which 1.21.2 made abstract.
#
# 1.21.1: EntityRenderer.getTextureLocation(T entity) abstract, answered by every renderer at its own erasure
# (AbstractZombieRenderer.getTextureLocation(Zombie) with javac's bridge getTextureLocation(Entity)). 1.21.2: gone from
# EntityRenderer; LivingEntityRenderer.getTextureLocation(S) (abstract, its render calls it) and ArrowRenderer's
# protected getTextureLocation(ArrowRenderState) are state-driven; the other renderers keep their texture to themselves.
#
# Put back (one generated mixin per 1.21.1 renderer class that declared an erasure, CbRenderTex<n>Mixin): the 1.21.1
# erasure itself and its javac bridges in that class, answering
#   - for a renderer whose 1.21.2 class is a LivingEntityRenderer: CbRenderTex.living (its 1.21.2 texture of the
#     entity's state, as vanilla's own);
#   - otherwise what the 1.21.1 body returned: a body `getstatic F; areturn` (the class's texture constant, which 1.21.2
#     keeps under the same name -- checked), or the hand-written bodies of HAND below (each one transcribes the 1.21.1
#     bytecode named in its comment onto 1.21.2's API).
# Override hooks: a mod class declaring a 1.21.1 erasure, under a class whose 1.21.2 counterpart has a state-driven
# texture the game calls (non-private), gets that 1.21.2 method synthesized (guarded), which hands the mod's override the
# entity its state was extracted from (CbRenderTexGen.tex<n>); a mod class extending LivingEntityRenderer or ArrowRenderer
# itself overrides at getTextureLocation(Entity), hooked to the abstract 1.21.2 method.
#
# createRenderState(): every 1.21.2 renderer class a 1.21.1 mod can extend whose createRenderState is still abstract
# gets an abstractFill answering a new state of the class its S is bounded by (read off the generic signatures), so a
# mod renderer gets the state its vanilla superclass's extractRenderState fills.
# ---------------------------------------------------------------------------------------------------------------------
LOC = MC + "class_2960"
TEX_V = ("method_3931", "(L" + ENTITY + ";)L" + LOC + ";")
TEX_W = [(MC + "class_922", "method_3885", "(L" + MC + "class_10042;)L" + LOC + ";"),
         (MC + "class_876", "method_4120", "(L" + MC + "class_9999;)L" + LOC + ";")]
RENDERER = MC + "class_897"
CREATE = ("method_55269", "()L" + STATE + ";")
MIXIN_SRC = os.path.join(ROOT, "core/shims/steps/1.21.2/src", STEP, "mixin")
MIXIN_CONFIG = os.path.join(ROOT, "core/shims/steps/1.21.2/resources/cb-step-1.21.2.mixins.json")
TEX_MIXIN = "CbRenderTex%sMixin"

# 1.21.1 bodies no pattern covers, transcribed by hand: class -> (shadows, implements, body, extra members)
HAND = {
    # class_954.method_4130 (TippableArrowRenderer): arrow.getColor() > 0 ? TIPPED_ARROW_LOCATION : NORMAL_ARROW_LOCATION
    MC + "class_954": ([], [], "return entity.method_7460() > 0 ? net.minecraft.class_954.field_4794 : net.minecraft.class_954.field_4795;", []),
    # class_966.method_4160 (WitherSkullRenderer): skull.isDangerous() ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION
    MC + "class_966": (["@Shadow @Final private static net.minecraft.class_2960 field_4817;",
                        "@Shadow @Final private static net.minecraft.class_2960 field_4815;"], [],
                       "return entity.method_7503() ? field_4817 : field_4815;", []),
    # class_928.method_4077 (PaintingRenderer): Minecraft.getInstance().getPaintingTextures().getBackSprite().atlasLocation()
    MC + "class_928": ([], [], "return net.minecraft.class_310.method_1551().method_18321().method_18342().method_45852();", []),
    # class_881.method_3891 (BoatRenderer): boatResources.get(boat.getVariant()).getFirst() -- the texture of the boat's
    # type. 1.21.2 has one BoatRenderer per boat type, each with that type's texture (field_54501): the one the
    # dispatcher picks for this boat holds it.
    MC + "class_881": (["@Shadow @Final private net.minecraft.class_2960 field_54501;"],
                       ["top.dext.centurybridge.step.v1_21_2.rt.CbRenderTex.BoatTexture"],
                       "net.minecraft.class_897<?, ?> r = net.minecraft.class_310.method_1551().method_1561().method_3953(entity);\n"
                       "        return r instanceof top.dext.centurybridge.step.v1_21_2.rt.CbRenderTex.BoatTexture b ? b.cb$boatTexture() : this.field_54501;",
                       ["@Override", "public net.minecraft.class_2960 cb$boatTexture() {", "    return this.field_54501;", "}"]),
}


def _anonymous(c):
    """a javac anonymous class (Outer$1): no mod can extend or name it"""
    return any(part.isdigit() for part in c.split("$")[1:])


def _num(c):
    return c[len(MC):].replace("class_", "").replace("$", "_")


def _sig_params(sig):
    """[(name, erased bound)] of a class signature's type parameters, and the rest of the signature"""
    out = []
    if not sig or not sig.startswith("<"):
        return out, sig or ""
    i = 1
    while sig[i] != ">":
        j = sig.index(":", i)
        name = sig[i:j]
        i = j + 1
        if sig[i] == ":":  # no class bound: the first interface bound
            i += 1
        k, depth = i, 0
        while True:
            ch = sig[k]
            if ch == "<":
                depth += 1
            elif ch == ">":
                depth -= 1
            elif ch == ";" and depth == 0:
                break
            k += 1
        b = sig[i:k + 1]
        out.append((name, b[1:-1].split("<")[0] if b.startswith("L") else None))
        i = k + 1
        while sig[i] == ":":  # further interface bounds
            k, depth = i + 1, 0
            while True:
                ch = sig[k]
                if ch == "<":
                    depth += 1
                elif ch == ">":
                    depth -= 1
                elif ch == ";" and depth == 0:
                    break
                k += 1
            i = k + 1
    return out, sig[i + 1:]


def _super_args(sig):
    """type argument signatures of a class signature's superclass"""
    _, rest = _sig_params(sig)
    if "<" not in rest.split(";")[0]:
        return []
    i = rest.index("<") + 1
    args, depth, start = [], 0, i
    while True:
        ch = rest[i]
        if ch == "<":
            depth += 1
        elif ch == ">":
            if depth == 0:
                args.append(rest[start:i])
                break
            depth -= 1
        elif ch == ";" and depth == 0:
            args.append(rest[start:i + 1])
            start = i + 1
        i += 1
    return args


def state_class(c, W):
    """the erased class 1.21.2's EntityRenderer<T, S> S resolves to in class c (its bound when still a variable)"""
    path = []
    x = c
    while x != RENDERER:
        path.append(x)
        x = W[x].super
        if x not in W:
            return None
    # walk down from EntityRenderer: S is its parameter 1
    var = 1
    cur = RENDERER
    for x in reversed(path):
        args = _super_args(W[x].signature)
        if var >= len(args):
            return None
        a = args[var]
        if a.startswith("T"):
            params, _ = _sig_params(W[x].signature)
            names = [n for n, _ in params]
            var = names.index(a[1:-1])
            cur = x
        else:
            return a[1:-1].split("<")[0]
    params, _ = _sig_params(W[c].signature)
    return params[var][1] if var < len(params) else None


def family_texture(V, W, targets):
    Wt = bridge_targets(os.path.join(DATA, "jars/client-1.21.2-intermediary-code.jar"))

    def fam(c, root, J, T):
        f = {root}
        grew = True
        while grew:
            grew = False
            for cc in chain(c, J):
                for b, t in T.get(cc, {}).items():
                    if b in f and t not in f:
                        f.add(t)
                        grew = True
        return f

    def wdecl(c, name, desc):
        return W.get(c) and any(m.name == name and m.desc == desc for m in W[c].methods)

    plans, notes = {}, []
    wanted = []
    for c, ci in sorted(V.items()):
        if c == RENDERER or RENDERER not in chain(c, V) or _anonymous(c):
            continue
        f = fam(c, TEX_V, V, targets)
        own = [m for m in ci.methods if (m.name, m.desc) in f and not m.access & (ACC_BRIDGE | ACC_SYNTH) and not m.access & 0x400]
        if not own:
            continue
        m = own[0]
        if c not in W:
            notes.append(f"{c}: gone in 1.21.2, getTextureLocation{m.desc} not put back (the class itself is missing)")
            continue
        if wdecl(c, m.name, m.desc):
            notes.append(f"{c}: 1.21.2 declares {m.name}{m.desc} itself")
            continue
        bridges = [(bn, bd) for (bn, bd), t in targets.get(c, {}).items() if t == (m.name, m.desc) and (bn, bd) in f]
        clash = [b for b in bridges if wdecl(c, *b)]
        bridges = [b for b in bridges if b not in clash]
        notes += [f"{c}: 1.21.2 declares bridge shape {b[0]}{b[1]} itself, left alone" for b in clash]
        plans[c] = {"m": m, "bridges": bridges, "living": MC + "class_922" in chain(c, W)}
        if not plans[c]["living"] and c not in HAND:
            wanted.append((c, m.name, m.desc))
    consts = constant_returns(os.path.join(DATA, "jars/client-1.21.1-intermediary-code.jar"), wanted)
    for c, n, d in wanted:
        k = consts.get((c, n, d))
        if not k:
            notes.append(f"{c}: 1.21.1 body of {n}{d} is not a constant and has no hand transcription: not put back")
            del plans[c]
            continue
        owner, fname = k
        fw = [x for x in W.get(owner, ClassInfoStub).fields if x[0] == fname] if owner in W else []
        if not fw:
            notes.append(f"{c}: {owner}.{fname} gone in 1.21.2: not put back")
            del plans[c]
            continue
        acc = fw[0][2]
        if acc & 0x1:
            plans[c]["body"] = ([], f"return {jtype('L' + owner + ';')}.{fname};")
        elif owner == c:
            plans[c]["body"] = ([f"@Shadow @Final private static {jtype(fw[0][1])} {fname};"], f"return {fname};")
        else:
            notes.append(f"{c}: {owner}.{fname} not public in 1.21.2: not put back")
            del plans[c]

    # hooks: for each put-back erasure under a class with a state-driven, callable 1.21.2 texture
    hooks, adapters, state_tex = [], [], set()
    roots = [(MC + "class_922", TEX_W[0]), (MC + "class_876", TEX_W[1])]
    for c, p in sorted(plans.items()):
        wc = chain(c, W)
        for rc, (_, rn, rd) in roots:
            if rc not in wc:
                continue
            fw = fam(c, (rn, rd), W, Wt)
            hit = None
            for x in wc:
                for mm in W[x].methods:
                    if (mm.name, mm.desc) in fw and not mm.access & (ACC_BRIDGE | ACC_SYNTH):
                        hit = (x, mm)
                        break
                if hit:
                    break
            if not hit or hit[1].access & 0x2:
                continue
            x, mm = hit
            if mm.access & 0x400:
                fb = "CbRenderTex.missing(state)"
            elif mm.access & 0x1:
                fb = f"(({jtype('L' + x + ';')}) (Object) self).{mm.name}(state)"
            else:
                fb = "((CbRenderTex.StateTexture) self).cb$stateTexture(state)"
                state_tex.add((x, mm.name, mm.desc))
            adapters.append((c, p["m"].name, p["m"].desc, mm.name, mm.desc, fb))
    for rc, (_, rn, rd) in roots:
        adapters.append((rc, TEX_V[0], TEX_V[1], rn, rd, "CbRenderTex.missing(state)"))
    for i, (c, on, od, nn, nd, fb) in enumerate(adapters):
        iface = f"Tex{i}"
        hooks.append({
            "old": on + od,
            "new": nn + nd,
            "onlyUnder": c,
            "adapter": f"{RT}/CbRenderTexGen${iface}",
            "bridge": f"{RT}/CbRenderTexGen.{iface.lower()}(L{RT}/CbRenderTexGen${iface};{nd[1:nd.index(')')]})L{LOC};",
            "guard": f"{RT}/Guard",
            "basis": f"1.21.1 getTextureLocation{od} under {c}: 1.21.2's renderer asks {nn}{nd} instead; the synthesized "
                     f"one hands the mod's override the entity its state was extracted from (CbRenderTexGen.{iface.lower()})",
        })

    # fills: createRenderState() still abstract on a 1.21.2 class a mod extends
    fills, fill_src = [], []
    cands = []
    for c in sorted(W):
        if c not in V or RENDERER not in chain(c, W):
            continue
        concrete = any(mm.name == CREATE[0] and not mm.access & 0x400 and not mm.desc == "()V"
                       for x in chain(c, W) if RENDERER in chain(x, W) for mm in W[x].methods)
        if concrete:
            continue
        st = state_class(c, W)
        if not st or st not in W or W[st].access & 0x400 or not any(mm.name == "<init>" and mm.desc == "()V" and mm.access & 0x1 for mm in W[st].methods):
            notes.append(f"{c}: createRenderState abstract, state {st} has no public no-argument constructor: no fill")
            continue
        cands.append((len(chain(c, W)), c, st))
    for _, c, st in sorted(cands, key=lambda t: (-t[0], t[1])):
        k = _num(c)
        fills.append({"under": c, "method": CREATE[0] + CREATE[1],
                      "call": f"{RT}/CbRenderTexGen.state{k}(L{c};)L{STATE};"})
        fill_src += ["",
                     f"    /** createRenderState() of a 1.21.1 mod renderer under {c}: the state its S is bounded by */",
                     "    @SuppressWarnings(\"rawtypes\")",
                     f"    public static net.minecraft.class_10017 state{k}({jtype('L' + c + ';')} self) {{",
                     f"        return new {jtype('L' + st + ';')}();",
                     "    }"]

    # sources: rt/CbRenderTexGen.java
    src = ["package top.dext.centurybridge.step.v1_21_2.rt;", "",
           "import net.fabricmc.api.EnvType;", "import net.fabricmc.api.Environment;", "",
           "/** Generated by core/tools/gen_s7render.py: getTextureLocation hooks and createRenderState fills (see CbRenderTex). */",
           "@Environment(EnvType.CLIENT)", "public final class CbRenderTexGen {", "", "    private CbRenderTexGen() {", "    }"]
    for i, (c, on, od, nn, nd, fb) in enumerate(adapters):
        et = jtype(od[1:od.index(")")])
        stt = jtype(nd[1:nd.index(")")])
        src += ["",
                f"    /** a 1.21.1 renderer under {c} overriding getTextureLocation{od} */",
                f"    public interface Tex{i} {{",
                f"        net.minecraft.class_2960 {on}({et} entity);",
                "    }", "",
                f"    public static net.minecraft.class_2960 tex{i}(Tex{i} self, {stt} state) {{",
                f"        if (CbRenderStateEntity.entityOf(state) instanceof {et} e) {{",
                f"            return self.{on}(e);",
                "        }",
                f"        return {fb};",
                "    }"]
    src += fill_src
    src.append("}")
    open(os.path.join(RT_SRC, "CbRenderTexGen.java"), "w", encoding="utf-8", newline="\n").write("\n".join(src) + "\n")

    # sources: one mixin per put-back class
    for fn in os.listdir(MIXIN_SRC):
        if fn.startswith("CbRenderTex") and fn.endswith("Mixin.java") and fn[len("CbRenderTex"):-len("Mixin.java")].replace("_", "").isdigit():
            os.remove(os.path.join(MIXIN_SRC, fn))
    mixins = []
    covers = []
    st_by_class = {}
    for x, n, d in state_tex:
        st_by_class[x] = (n, d)
        if x not in plans:
            plans[x] = {"m": None, "bridges": [], "living": False}
    for c, p in sorted(plans.items()):
        name = TEX_MIXIN % _num(c)
        mixins.append(name)
        m = p["m"]
        shadows, impls, extra = [], [], []
        lines = []
        if m is not None:
            covers.append(f"{c}.{m.name}{m.desc}")
            covers += [f"{c}.{bn}{bd}" for bn, bd in p["bridges"]]
            et = jtype(m.desc[1:m.desc.index(")")])
            if p["living"]:
                body = "return CbRenderTex.living(this, entity);"
            elif c in HAND:
                sh, im, body, ex = HAND[c]
                shadows += sh
                impls += im
                extra += ex
            else:
                sh, body = p["body"]
                shadows += sh
            lines += ["", f"    /** 1.21.1 getTextureLocation{m.desc} of this renderer */",
                      f"    public net.minecraft.class_2960 {m.name}({et} entity) {{", f"        {body}", "    }"]
            for bn, bd in p["bridges"]:
                bt = jtype(bd[1:bd.index(")")])
                lines += ["", f"    /** javac's 1.21.1 bridge {bn}{bd} to it */",
                          f"    public net.minecraft.class_2960 {bn}({bt} entity) {{",
                          f"        return this.{m.name}(({et}) entity);", "    }"]
        if c in st_by_class:
            n, d = st_by_class[c]
            stt = jtype(d[1:d.index(")")])
            impls.append("top.dext.centurybridge.step.v1_21_2.rt.CbRenderTex.StateTexture")
            shadows.append(f"@Shadow protected abstract net.minecraft.class_2960 {n}({stt} state);")
            extra += ["@Override", "public net.minecraft.class_2960 cb$stateTexture(net.minecraft.class_10017 state) {",
                      f"    return this.{n}(({stt}) state);", "}"]
        out = ["package top.dext.centurybridge.step.v1_21_2.mixin;", "",
               "import net.fabricmc.api.EnvType;", "import net.fabricmc.api.Environment;",
               "import org.spongepowered.asm.mixin.Final;", "import org.spongepowered.asm.mixin.Mixin;",
               "import org.spongepowered.asm.mixin.Shadow;", "import top.dext.centurybridge.step.v1_21_2.rt.CbRenderTex;", "",
               "/** Generated by core/tools/gen_s7render.py: 1.21.1 getTextureLocation of this renderer put back (see CbRenderTex). */",
               "@SuppressWarnings(\"unused\")",
               "@Environment(EnvType.CLIENT)", f"@Mixin({jtype('L' + c + ';')}.class)",
               f"public abstract class {name}" + (" implements " + ", ".join(impls) if impls else "") + " {"]
        for sh in shadows:
            out += ["", "    " + sh.replace("@Shadow @Final ", "@Shadow\n    @Final\n    ").replace("@Shadow protected", "@Shadow\n    protected")]
        out += lines
        if extra:
            out.append("")
            out += ["    " + e for e in extra]
        out.append("}")
        open(os.path.join(MIXIN_SRC, name + ".java"), "w", encoding="utf-8", newline="\n").write("\n".join(out) + "\n")
    for n in notes:
        print("  note:", n)
    print(f"getTextureLocation: {len(plans)} renderer classes put back, {len(adapters)} hooks; createRenderState fills {len(fills)}")
    return hooks, fills, mixins, covers


# ---------------------------------------------------------------------------------------------------------------------
# Family 4 -- the renderer / layer methods 1.21.2 moved from the entity to the render state.
#
# Each SPEC is one 1.21.1 method of EntityRenderer, LivingEntityRenderer or RenderLayer that took the entity (and often
# a partial tick, a yaw or a bob) and whose 1.21.2 counterpart takes the renderer's state instead:
#   v / w        the 1.21.1 and 1.21.2 root (name, descriptor) on `owner`;
#   recv         "r": called on the renderer that extracts the state; "l": on a layer, whose renderer is its parent;
#   ei / si      index of the entity parameter in v, of the state parameter in w (None: w takes no state);
#   wmap         for each other w parameter, the v parameter it is (same value, same meaning);
#   pt           index of v's partial tick parameter (None: the frame's);
#   fill         v parameters w has no place for, as 1.21.1's caller computed them -- "yaw" (LevelRenderer: the
#                entity's lerped yRot), "bob" (LivingEntityRenderer.render: this renderer's getBob), "walkPos" /
#                "walkSpeed" (LivingEntityRenderer.render's limb swing, CbRenderModels.living).
# For every 1.21.1 class that declared an erasure of the method (javac's bridges tie them to the root), one generated
# mixin (CbRenderState<n>Mixin) puts the erasure and its bridges back: it extracts a state of the entity with the
# renderer (CbRenderLiving.stateOf -- the renderer's own state when it already holds this extraction) and runs the
# 1.21.2 method as vanilla's own (guard held, so a mod override synthesized below falls through to vanilla instead of
# re-entering the mod's method). The root class's mixin also gives the 1.21.2 root a public trampoline
# (CbRenderStateGen.W<n>.cb$w$<name>) for the rt code. And one override hook per erasure: a mod class declaring it
# gets the 1.21.2 method synthesized (guarded), which hands the mod's override the entity its state was extracted from
# and v's other arguments as above; with no entity (a state no extraction filled) it falls through to the 1.21.2
# implementation above, or -- where that is abstract -- fails, saying so.
# ---------------------------------------------------------------------------------------------------------------------
PS, MBS = MC + "class_4587", MC + "class_4597"
LSTATE = MC + "class_10042"
SPECS = [
    # EntityRenderer.render(T, yaw, partialTick, PoseStack, MultiBufferSource, light) -> render(S, PoseStack, MultiBufferSource, light)
    dict(owner=RENDERER, v=("method_3936", f"(L{ENTITY};FFL{PS};L{MBS};I)V"), w=("method_3936", f"(L{STATE};L{PS};L{MBS};I)V"),
         recv="r", ei=0, si=0, wmap={1: 3, 2: 4, 3: 5}, pt=2, fill={1: "yaw"}),
    # EntityRenderer.getRenderOffset(T, partialTick) -> getRenderOffset(S)
    dict(owner=RENDERER, v=("method_23169", f"(L{ENTITY};F)L{MC}class_243;"), w=("method_23169", f"(L{STATE};)L{MC}class_243;"),
         recv="r", ei=0, si=0, wmap={}, pt=1, fill={}),
    # EntityRenderer.renderNameTag(T, Component, PoseStack, MultiBufferSource, light, partialTick) -> (S, Component, PoseStack, MultiBufferSource, light)
    dict(owner=RENDERER, v=("method_3926", f"(L{ENTITY};L{MC}class_2561;L{PS};L{MBS};IF)V"),
         w=("method_3926", f"(L{STATE};L{MC}class_2561;L{PS};L{MBS};I)V"),
         recv="r", ei=0, si=0, wmap={1: 1, 2: 2, 3: 3, 4: 4}, pt=5, fill={}),
    # EntityRenderer.getShadowRadius(T) -> getShadowRadius(S)
    dict(owner=RENDERER, v=("method_55831", f"(L{ENTITY};)F"), w=("method_55831", f"(L{STATE};)F"),
         recv="r", ei=0, si=0, wmap={}, pt=None, fill={}),
    # LivingEntityRenderer.getRenderType(T, bodyVisible, translucent, glowing) -> (S, ...)
    dict(owner=MC + "class_922", v=("method_24302", f"(L{MC}class_1309;ZZZ)L{MC}class_1921;"),
         w=("method_24302", f"(L{LSTATE};ZZZ)L{MC}class_1921;"), recv="r", ei=0, si=0, wmap={1: 1, 2: 2, 3: 3}, pt=None, fill={}),
    # LivingEntityRenderer.isBodyVisible(T) / isShaking(T) -> (S)
    dict(owner=MC + "class_922", v=("method_4056", f"(L{MC}class_1309;)Z"), w=("method_4056", f"(L{LSTATE};)Z"),
         recv="r", ei=0, si=0, wmap={}, pt=None, fill={}),
    dict(owner=MC + "class_922", v=("method_25450", f"(L{MC}class_1309;)Z"), w=("method_25450", f"(L{LSTATE};)Z"),
         recv="r", ei=0, si=0, wmap={}, pt=None, fill={}),
    # LivingEntityRenderer.setupRotations(T, PoseStack, bob, bodyRot, partialTick, scale) -> (S, PoseStack, bodyRot, scale)
    dict(owner=MC + "class_922", v=("method_4058", f"(L{MC}class_1309;L{PS};FFFF)V"), w=("method_4058", f"(L{LSTATE};L{PS};FF)V"),
         recv="r", ei=0, si=0, wmap={1: 1, 2: 3, 3: 5}, pt=4, fill={2: "bob"}),
    # LivingEntityRenderer.getWhiteOverlayProgress(T, partialTick) -> (S)
    dict(owner=MC + "class_922", v=("method_23185", f"(L{MC}class_1309;F)F"), w=("method_23185", f"(L{LSTATE};)F"),
         recv="r", ei=0, si=0, wmap={}, pt=1, fill={}),
    # LivingEntityRenderer.scale(T, PoseStack, partialTick) -> (S, PoseStack)
    dict(owner=MC + "class_922", v=("method_4042", f"(L{MC}class_1309;L{PS};F)V"), w=("method_4042", f"(L{LSTATE};L{PS};)V"),
         recv="r", ei=0, si=0, wmap={1: 1}, pt=2, fill={}),
    # LivingEntityRenderer.getFlipDegrees(T) -> getFlipDegrees() (called inside setupRotations(state), on the renderer's state)
    dict(owner=MC + "class_922", v=("method_4039", f"(L{MC}class_1309;)F"), w=("method_3919", "()F"),
         recv="r", ei=0, si=None, wmap={}, pt=None, fill={}),
    # RenderLayer.render(PoseStack, MultiBufferSource, light, T, limbSwing, limbSwingAmount, partialTick, ageInTicks,
    # netHeadYaw, headPitch) -> render(PoseStack, MultiBufferSource, light, S, yRot, xRot)
    dict(owner=MC + "class_3887", v=("method_4199", f"(L{PS};L{MBS};IL{ENTITY};FFFFFF)V"), w=("method_4199", f"(L{PS};L{MBS};IL{STATE};FF)V"),
         recv="l", ei=3, si=3, wmap={0: 0, 1: 1, 2: 2, 4: 8, 5: 9}, pt=6, fill={4: "walkPos", 5: "walkSpeed", 7: "bob"}),
]
STATE_MIXIN = "CbRenderState%sMixin"


def _args(desc):
    a, _ = classfile.split_args(desc)
    return a


def _ret(desc):
    return desc[desc.index(")") + 1:]


def family_state(V, W, targets):
    Wt = bridge_targets(os.path.join(DATA, "jars/client-1.21.2-intermediary-code.jar"))

    def fam(c, root, J, T):
        f = {root}
        grew = True
        while grew:
            grew = False
            for cc in chain(c, J):
                for b, t in T.get(cc, {}).items():
                    if b in f and t not in f:
                        f.add(t)
                        grew = True
        return f

    def wdecl(c, name, desc):
        return any(m.name == name and m.desc == desc for m in W[c].methods)

    roots = {}      # owner -> [spec index]
    per_class = {}  # class -> list of (spec index, member, bridges)
    hooks, adapters, covers, notes = [], [], [], []
    for k, sp in enumerate(SPECS):
        roots.setdefault(sp["owner"], []).append(k)
        for c, ci in sorted(V.items()):
            if sp["owner"] not in chain(c, V) or _anonymous(c):
                continue
            f = fam(c, sp["v"], V, targets)
            own = [m for m in ci.methods if (m.name, m.desc) in f and not m.access & (ACC_BRIDGE | ACC_SYNTH) and not m.access & 0x8]
            if not own:
                continue
            m = own[0]
            if c not in W:
                notes.append(f"{c}: gone in 1.21.2, {m.name}{m.desc} not put back")
                continue
            if wdecl(c, m.name, m.desc):
                notes.append(f"{c}: 1.21.2 declares {m.name}{m.desc} itself")
                continue
            bridges = [(bn, bd) for (bn, bd), t in targets.get(c, {}).items() if t == (m.name, m.desc) and (bn, bd) in f]
            clash = [b for b in bridges if wdecl(c, *b)]
            notes += [f"{c}: 1.21.2 declares {b[0]}{b[1]} itself, bridge left alone" for b in clash]
            bridges = [b for b in bridges if b not in clash]
            done = {(mm.name, mm.desc) for _, mm, _ in per_class.get(c, [])} | {b for _, _, bs in per_class.get(c, []) for b in bs}
            if (m.name, m.desc) in done:
                notes.append(f"{c}: {m.name}{m.desc} already put back by an earlier spec")
                continue
            bridges = [b for b in bridges if b not in done]
            per_class.setdefault(c, []).append((k, m, bridges))
            covers.append(f"{c}.{m.name}{m.desc}")
            covers += [f"{c}.{bn}{bd}" for bn, bd in bridges]
            # the 1.21.2 member a mod class under c overrides instead
            fw = fam(c, sp["w"], W, Wt)
            hit = None
            for x in chain(c, W):
                for mm in W[x].methods:
                    if (mm.name, mm.desc) in fw and not mm.access & (ACC_BRIDGE | ACC_SYNTH):
                        hit = (x, mm)
                        break
                if hit:
                    break
            if not hit or hit[1].access & 0x2 or hit[1].access & 0x10:
                notes.append(f"{c}: no overridable 1.21.2 {sp['w'][0]} above it: {m.name}{m.desc} put back, not hooked")
                continue
            adapters.append((k, c, m, hit[1]))

    # sources: rt/CbRenderStateGen.java (root trampolines' interfaces, hook adapters and bridges)
    src = ["package top.dext.centurybridge.step.v1_21_2.rt;", "",
           "import net.fabricmc.api.EnvType;", "import net.fabricmc.api.Environment;", "",
           "/** Generated by core/tools/gen_s7render.py: the state-moved renderer/layer methods (see CbRenderLiving). */",
           "@Environment(EnvType.CLIENT)", "public final class CbRenderStateGen {", "", "    private CbRenderStateGen() {", "    }"]
    for owner, ks in sorted(roots.items()):
        src += ["", f"    /** the 1.21.2 roots on {owner}, public (the root's generated mixin implements it) */",
                f"    public interface W{_num(owner)} {{"]
        for k in ks:
            sp = SPECS[k]
            n, d = sp["w"]
            ps = ", ".join(f"{jtype(t)} w{j}" for j, t in enumerate(_args(d)))
            src.append(f"        {jtype(_ret(d))} cb$w${n}({ps});")
        src.append("    }")

    def vcall_args(sp, vdesc, wnames, ent):
        out = []
        for i, t in enumerate(_args(vdesc)):
            if i == sp["ei"]:
                out.append(ent)
            elif i == sp["pt"]:
                out.append("pt")
            elif i in sp["wmap"].values():
                j = [jj for jj, vi in sp["wmap"].items() if vi == i][0]
                out.append(wnames[j])
            else:
                kind = sp["fill"][i]
                out.append({"yaw": "net.minecraft.class_3532.method_16439(pt, e.field_5982, e.method_36454())",
                            "bob": "CbRenderLiving.bob(self, e, pt)",
                            "walkPos": "a.walkPos()", "walkSpeed": "a.walkSpeed()"}[kind])
        return out

    for i, (k, c, m, wm) in enumerate(adapters):
        sp = SPECS[k]
        vargs, wargs = _args(m.desc), _args(wm.desc)
        et = jtype(vargs[sp["ei"]])
        rt_ = jtype(_ret(m.desc))
        ret = "" if rt_ == "void" else "return "
        wnames = [f"w{j}" for j in range(len(wargs))]
        st_expr = f"w{sp['si']}" if sp["si"] is not None else "((CbRenderTex.Renderer) self).cb$reusedState()"
        need_a = any(v in ("walkPos", "walkSpeed") for v in sp["fill"].values())
        iface = f"St{i}"
        src += ["", f"    /** a 1.21.1 class under {c} overriding {m.name}{m.desc} */", f"    public interface {iface} {{",
                f"        {rt_} {m.name}(" + ", ".join(f"{jtype(t)} p{j}" for j, t in enumerate(vargs)) + ");", "    }", "",
                f"    public static {jtype(_ret(wm.desc))} {iface.lower()}({iface} self" + "".join(f", {jtype(t)} w{j}" for j, t in enumerate(wargs)) + ") {",
                f"        net.minecraft.class_10017 state = {st_expr};",
                f"        if (CbRenderStateEntity.entityOf(state) instanceof {et} e) {{",
                "            float pt = CbRenderStateEntity.partialTickOf(state);"]
        if need_a:
            src.append("            CbRenderModels.Anim a = CbRenderLiving.anim(self, e, pt, state);")
        src += [f"            {ret}self.{m.name}(" + ", ".join(vcall_args(sp, m.desc, wnames, "e")) + ");"]
        if not ret:
            src.append("            return;")
        src.append("        }")
        if wm.access & 0x400:
            src.append(f"        throw new IllegalStateException(\"{sp['w'][0]}: a state no extraction filled reached a 1.21.1 override, "
                       f"and 1.21.2 has no implementation to fall back to\");")
        else:
            src.append(f"        {ret}((W{_num(sp['owner'])}) self).cb$w${sp['w'][0]}(" + ", ".join(wnames) + ");")
        src.append("    }")
        hooks.append({
            "old": m.name + m.desc, "new": wm.name + wm.desc, "onlyUnder": c,
            "adapter": f"{RT}/CbRenderStateGen${iface}",
            "bridge": f"{RT}/CbRenderStateGen.{iface.lower()}(L{RT}/CbRenderStateGen${iface};{wm.desc[1:wm.desc.index(')')]}){_ret(wm.desc)}",
            "guard": f"{RT}/Guard",
            "basis": f"1.21.1 {m.name}{m.desc} under {c}: 1.21.2 calls {wm.name}{wm.desc} with the render state; the synthesized "
                     f"one hands the mod's override the entity the state was extracted from and the arguments 1.21.1's caller "
                     f"computed (CbRenderStateGen.{iface.lower()})",
        })
    src.append("}")
    open(os.path.join(RT_SRC, "CbRenderStateGen.java"), "w", encoding="utf-8", newline="\n").write("\n".join(src) + "\n")

    # sources: one mixin per class
    for fn in os.listdir(MIXIN_SRC):
        if fn.startswith("CbRenderState") and fn.endswith("Mixin.java") and fn[len("CbRenderState"):-len("Mixin.java")].replace("_", "").isdigit():
            os.remove(os.path.join(MIXIN_SRC, fn))
    mixins = []
    for c in sorted(set(per_class) | set(roots)):
        name = STATE_MIXIN % _num(c)
        mixins.append(name)
        impls, body = [], []
        for k, m, bridges in per_class.get(c, []):
            sp = SPECS[k]
            vargs = _args(m.desc)
            ps = ", ".join(f"{jtype(t)} p{j}" for j, t in enumerate(vargs))
            rt_ = jtype(_ret(m.desc))
            ret = "" if rt_ == "void" else "return "
            ent = f"p{sp['ei']}"
            pt = f"p{sp['pt']}" if sp["pt"] is not None else "CbRenderTex.partialTick()"
            wargs = _args(sp["w"][1])
            call = []
            for j, t in enumerate(wargs):
                if j == sp["si"]:
                    call.append(f"({jtype(t)}) s")
                else:
                    call.append(f"p{sp['wmap'][j]}")
            body += ["", f"    /** 1.21.1 {m.name}{m.desc}: the 1.21.2 {sp['w'][0]} of a state extracted from the entity, as vanilla's */",
                     f"    public {rt_} {m.name}({ps}) {{"]
            if sp["si"] is not None:
                rend = "(net.minecraft.class_897<?, ?>) (Object) this" if sp["recv"] == "r" else f"CbRenderLiving.rendererOf(this, {ent})"
                body.append(f"        net.minecraft.class_10017 s = CbRenderLiving.stateOf({rend}, {ent}, {pt});")
            body += ["        boolean in = Guard.cbEnter(this);", "        try {",
                     f"            {ret}((CbRenderStateGen.W{_num(sp['owner'])}) (Object) this).cb$w${sp['w'][0]}(" + ", ".join(call) + ");",
                     "        } finally {", "            if (in) {", "                Guard.cbExit(this);", "            }", "        }", "    }"]
            for bn, bd in bridges:
                bargs = _args(bd)
                casts = [f"p{j}" if bargs[j] == vargs[j] else f"({jtype(vargs[j])}) p{j}" for j in range(len(vargs))]
                body += ["", f"    /** javac's 1.21.1 bridge {bn}{bd} to it */",
                         f"    public {rt_} {bn}(" + ", ".join(f"{jtype(t)} p{j}" for j, t in enumerate(bargs)) + ") {",
                         f"        {ret}this.{m.name}(" + ", ".join(casts) + ");", "    }"]
        shadows = []
        if c in roots:
            impls.append(f"CbRenderStateGen.W{_num(c)}")
            for k in roots[c]:
                n, d = SPECS[k]["w"]
                wargs = _args(d)
                wm = [mm for mm in W[c].methods if mm.name == n and mm.desc == d][0]
                vis = "public" if wm.access & 0x1 else "protected"
                ps = ", ".join(f"{jtype(t)} w{j}" for j, t in enumerate(wargs))
                rt_ = jtype(_ret(d))
                shadows += ["", "    @Shadow", f"    {vis} abstract {rt_} {n}({ps});"]
                body += ["", "    @Override", f"    public {rt_} cb$w${n}({ps}) {{",
                         f"        {'' if rt_ == 'void' else 'return '}this.{n}(" + ", ".join(f"w{j}" for j in range(len(wargs))) + ");", "    }"]
        out = ["package top.dext.centurybridge.step.v1_21_2.mixin;", "",
               "import net.fabricmc.api.EnvType;", "import net.fabricmc.api.Environment;",
               "import org.spongepowered.asm.mixin.Mixin;", "import org.spongepowered.asm.mixin.Shadow;",
               "import top.dext.centurybridge.step.v1_21_2.rt.CbRenderLiving;",
               "import top.dext.centurybridge.step.v1_21_2.rt.CbRenderStateGen;",
               "import top.dext.centurybridge.step.v1_21_2.rt.CbRenderTex;",
               "import top.dext.centurybridge.step.v1_21_2.rt.Guard;", "",
               "/** Generated by core/tools/gen_s7render.py: the 1.21.1 state-moved methods of this class put back (see CbRenderLiving). */",
               "@SuppressWarnings({\"unused\", \"unchecked\", \"rawtypes\"})", "@Environment(EnvType.CLIENT)",
               f"@Mixin({jtype('L' + c + ';')}.class)",
               f"public abstract class {name}" + (" implements " + ", ".join(impls) if impls else "") + " {"]
        out += shadows + body + ["}"]
        open(os.path.join(MIXIN_SRC, name + ".java"), "w", encoding="utf-8", newline="\n").write("\n".join(out) + "\n")
    for n in notes:
        print("  note:", n)
    print(f"state-moved methods: {sum(len(v) for v in per_class.values())} put back in {len(per_class)} classes, {len(hooks)} hooks")
    return hooks, mixins, covers



# ---------------------------------------------------------------------------------------------------------------------
# Family 5 -- EntityModel.setupAnim / prepareMobModel called on a 1.21.2 model (a mod's call, or a mod model's super
# call). For every 1.21.1 model class that declared an erasure of either (the `setup` / `prepare` maps of family 1),
# a generated mixin (CbRenderModel<n>Mixin) puts it and its javac bridges back:
#   setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch): the model's 1.21.2
#     setupAnim(state) on a state of the entity carrying these numbers (CbRenderModelCalls.setupAnim), run as vanilla's
#     own; the state type is the one the class's 1.21.2 setupAnim takes (read off W's bridges);
#   prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick): 1.21.2 folded what it did into setupAnim(state),
#     which reads it from the state; the call records the partial tick for the setupAnim that follows
#     (CbRenderModelCalls.prepare), as 1.21.1's renderer always called the two in that order.
# ---------------------------------------------------------------------------------------------------------------------
MODEL_MIXIN = "CbRenderModel%sMixin"


def family_models(V, W, targets, setup, prepare):
    Wt = bridge_targets(os.path.join(DATA, "jars/client-1.21.2-intermediary-code.jar"))

    def wfam(c, root):
        f = {root}
        grew = True
        while grew:
            grew = False
            for cc in chain(c, W):
                for b, t in Wt.get(cc, {}).items():
                    if b in f and t not in f:
                        f.add(t)
                        grew = True
        return f

    plans, notes, covers = {}, [], []
    for kind, table in (("setup", setup), ("prepare", prepare)):
        for (name, desc), owners in sorted(table.items()):
            for c in owners:
                if _anonymous(c):
                    continue
                if c not in W:
                    notes.append(f"{c}: gone in 1.21.2, {kind} {name}{desc} not put back")
                    continue
                if any(m.name == name and m.desc == desc for m in W[c].methods):
                    notes.append(f"{c}: 1.21.2 declares {name}{desc} itself")
                    continue
                bridges = [(bn, bd) for (bn, bd), t in targets.get(c, {}).items() if t == (name, desc)]
                bridges = [b for b in bridges if not any(m.name == b[0] and m.desc == b[1] for m in W[c].methods)]
                st = None
                if kind == "setup":
                    fw = wfam(c, W_SETUP)
                    for x in chain(c, W):
                        hit = [m for m in W[x].methods if (m.name, m.desc) in fw and not m.access & (ACC_BRIDGE | ACC_SYNTH)]
                        if hit:
                            st = hit[0].desc[2:hit[0].desc.index(";")]
                            break
                    if st is None:
                        notes.append(f"{c}: no 1.21.2 setupAnim above it: {name}{desc} not put back")
                        continue
                p = plans.setdefault(c, [])
                done = {(n, d) for _, n, d, _, _ in p} | {b for *_, bs, _ in p for b in bs}
                if (name, desc) in done:
                    continue
                bridges = [b for b in bridges if b not in done]
                p.append((kind, name, desc, bridges, st))
                covers.append(f"{c}.{name}{desc}")
                covers += [f"{c}.{bn}{bd}" for bn, bd in bridges]
    for fn in os.listdir(MIXIN_SRC):
        if fn.startswith("CbRenderModel") and fn.endswith("Mixin.java") and fn[len("CbRenderModel"):-len("Mixin.java")].replace("_", "").isdigit():
            os.remove(os.path.join(MIXIN_SRC, fn))
    mixins = []
    for c, p in sorted(plans.items()):
        name = MODEL_MIXIN % _num(c)
        mixins.append(name)
        out = ["package top.dext.centurybridge.step.v1_21_2.mixin;", "",
               "import net.fabricmc.api.EnvType;", "import net.fabricmc.api.Environment;",
               "import org.spongepowered.asm.mixin.Mixin;", "import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelCalls;", "",
               "/** Generated by core/tools/gen_s7render.py: 1.21.1 setupAnim / prepareMobModel of this model put back (see CbRenderModelCalls). */",
               "@Environment(EnvType.CLIENT)", f"@Mixin({jtype('L' + c + ';')}.class)", f"public abstract class {name} {{"]
        for kind, n, d, bridges, st in p:
            args = _args(d)
            et = jtype(args[0])
            if kind == "setup":
                out += ["", f"    /** 1.21.1 setupAnim{d}: this model's 1.21.2 setupAnim({st.split('/')[-1]}) on a state of the entity */",
                        f"    public void {n}({et} entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {{",
                        f"        CbRenderModelCalls.setupAnim(this, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, {jtype('L' + st + ';')}.class);",
                        "    }"]
                call = "limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch"
                sig = "float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch"
            else:
                out += ["", f"    /** 1.21.1 prepareMobModel{d}: 1.21.2's setupAnim(state) does what it did; the partial tick is kept for it */",
                        f"    public void {n}({et} entity, float limbSwing, float limbSwingAmount, float partialTick) {{",
                        "        CbRenderModelCalls.prepare(this, entity, partialTick);", "    }"]
                call = "limbSwing, limbSwingAmount, partialTick"
                sig = "float limbSwing, float limbSwingAmount, float partialTick"
            for bn, bd in bridges:
                bt = jtype(_args(bd)[0])
                out += ["", f"    /** javac's 1.21.1 bridge {bn}{bd} to it */",
                        f"    public void {bn}({bt} entity, {sig}) {{",
                        f"        this.{n}(({et}) entity, {call});", "    }"]
        out.append("}")
        open(os.path.join(MIXIN_SRC, name + ".java"), "w", encoding="utf-8", newline="\n").write("\n".join(out) + "\n")
    for n in notes:
        print("  note:", n)
    print(f"models: setupAnim/prepareMobModel put back in {len(plans)} classes")
    return mixins, covers



# ---------------------------------------------------------------------------------------------------------------------
# Family 6 -- AgeableListModel.headParts() / bodyParts() of 1.21.1's vanilla models (HumanoidModel, QuadrupedModel ...),
# gone with AgeableListModel in 1.21.2 (those models are root-built EntityModels there). Put back per class with the
# 1.21.1 body (CbRenderParts<n>Mixin): every one is `[Iterables.concat(super.x(), ] ImmutableList.of(this.f, ...) [)]`
# over the class's part fields, which 1.21.2 keeps (checked: same name, same type); a super call is inlined as the
# nearest 1.21.1 ancestor's body, so no mixin needs a super call. Fields are read through the declaring 1.21.2 class
# (public there) or @Shadow when the mixin's own class declares them.
# ---------------------------------------------------------------------------------------------------------------------
PARTS = ("method_22946", "method_22948")
PARTS_MIXIN = "CbRenderParts%sMixin"


def _parse_parts(code, refs, frefs):
    """[("super", owner, name)] / [("field", owner, name)] items of a parts body, or None for another shape"""
    i, items, out = 0, [], []
    while i < len(code):
        op = code[i]
        if op == 0x2a:  # aload_0
            i += 1
            continue
        if op == 0xb4:  # getfield
            f = frefs.get(struct.unpack(">H", code[i + 1:i + 3])[0])
            items.append(("field", f[0], f[1], f[2]))
            i += 3
            continue
        if op == 0xb7:  # invokespecial super.parts()
            r = refs.get(struct.unpack(">H", code[i + 1:i + 3])[0])
            if r[1] not in PARTS:
                return None
            out.append(("super", r[0], r[1]))
            i += 3
            continue
        if op == 0xb8:  # invokestatic ImmutableList.of / Iterables.concat
            r = refs.get(struct.unpack(">H", code[i + 1:i + 3])[0])
            if r[0] == "com/google/common/collect/ImmutableList" and r[1] == "of" and not r[2].startswith("([L"):
                out.append(("list", items))
                items = []
            elif r[0] == "com/google/common/collect/Iterables" and r[1] == "concat" and r[2].count("Ljava/lang/Iterable;") == 3:
                out.append(("concat",))
            else:
                return None
            i += 3
            continue
        if op == 0xb0:  # areturn
            return out
        return None
    return None


def family_parts(V, W):
    jar = os.path.join(DATA, "jars/client-1.21.1-intermediary-code.jar")
    bodies, notes = {}, []
    with zipfile.ZipFile(jar) as z:
        for c, ci in sorted(V.items()):
            if MC + "class_4592" not in chain(c, V) or _anonymous(c):
                continue
            mine = [m for m in ci.methods if m.name in PARTS and m.desc == "()Ljava/lang/Iterable;" and not m.access & 0x400]
            if not mine:
                continue
            b = z.read(c + ".class")
            cc = classfile.read(b, True)
            refs, frefs = _cp_methodrefs(b), _cp_fieldrefs(b)
            for m in cc.methods:
                if m.name in PARTS and m.desc == "()Ljava/lang/Iterable;" and m.code:
                    bodies[(c, m.name)] = _parse_parts(m.code, refs, frefs)

    def decl(c, name):
        """the nearest 1.21.1 class at or above c declaring a parts body"""
        for x in chain(c, V):
            if (x, name) in bodies:
                return x
        return None

    def expr(c, name, depth=0):
        shadows = shadows_of[target[0]]
        code = bodies.get((c, name))
        if code is None:
            return None
        stack = []
        for it in code:
            if it[0] == "super":
                up = decl(V[c].super, name)
                if up is None or up == MC + "class_4592":
                    return None
                e = expr(up, name, depth + 1)
                if e is None:
                    return None
                stack.append(e)
            elif it[0] == "list":
                parts = []
                for _, owner, fname, fdesc in it[1]:
                    wo = [x for x in chain(c, W) if any(f[0] == fname and f[1] == fdesc for f in W[x].fields)]
                    if not wo:
                        return None
                    acc = [f for f in W[wo[0]].fields if f[0] == fname][0][2]
                    if acc & 0x1:
                        parts.append(f"(({jtype('L' + wo[0] + ';')}) (Object) this).{fname}")
                    elif wo[0] == target[0]:
                        shadows.add((fname, fdesc, acc))
                        parts.append(f"this.{fname}")
                    else:
                        return None
                stack.append("com.google.common.collect.ImmutableList.of(" + ", ".join(parts) + ")")
            elif it[0] == "concat":
                b2, a2 = stack.pop(), stack.pop()
                stack.append(f"com.google.common.collect.Iterables.concat({a2}, {b2})")
        return stack[-1] if len(stack) == 1 else None

    for fn in os.listdir(MIXIN_SRC):
        if fn.startswith("CbRenderParts") and fn.endswith("Mixin.java"):
            os.remove(os.path.join(MIXIN_SRC, fn))
    mixins, covers = [], []
    by_class = {}
    target, shadows_of = [None], {}
    for (c, name) in sorted(bodies):
        if c not in W:
            notes.append(f"{c}: gone in 1.21.2, {name} not put back")
            continue
        target[0] = c
        shadows = shadows_of.setdefault(c, set())
        e = expr(c, name)
        if e is None:
            notes.append(f"{c}.{name}: 1.21.1 body not of the parts-list shape, or a field 1.21.2 does not keep public: not put back")
            continue
        by_class.setdefault(c, []).append((name, e))
    for c, ms in sorted(by_class.items()):
        nm = PARTS_MIXIN % _num(c)
        mixins.append(nm)
        out = ["package top.dext.centurybridge.step.v1_21_2.mixin;", "",
               "import net.fabricmc.api.EnvType;", "import net.fabricmc.api.Environment;", "import org.spongepowered.asm.mixin.Mixin;", "",
               "/** Generated by core/tools/gen_s7render.py: 1.21.1 AgeableListModel headParts / bodyParts of this model put back, 1.21.1's bodies. */",
               "@Environment(EnvType.CLIENT)", f"@Mixin({jtype('L' + c + ';')}.class)", f"public abstract class {nm} {{"]
        for fname, fdesc, acc in sorted(shadows_of.get(c, ())):
            vis = "private " if acc & 0x2 else "protected " if acc & 0x4 else ""
            out += ["", "    @org.spongepowered.asm.mixin.Shadow"]
            if acc & 0x10:
                out.append("    @org.spongepowered.asm.mixin.Final")
            out.append(f"    {vis}{jtype(fdesc)} {fname};")
        for name, e in ms:
            label = "headParts" if name == PARTS[0] else "bodyParts"
            out += ["", f"    /** 1.21.1 {label}() */", f"    protected Iterable<net.minecraft.class_630> {name}() {{", f"        return {e};", "    }"]
            covers.append(f"{c}.{name}()Ljava/lang/Iterable;")
        out.append("}")
        open(os.path.join(MIXIN_SRC, nm + ".java"), "w", encoding="utf-8", newline="\n").write("\n".join(out) + "\n")
    for n in notes:
        print("  note:", n)
    print(f"model parts: headParts/bodyParts put back in {len(by_class)} classes")
    return mixins, covers



# ---------------------------------------------------------------------------------------------------------------------
# Family 7 -- GameRenderer's static core-shader getters (getPositionShader() ... ), gone in 1.21.2: the core shaders are
# CoreShaders keys there, compiled and cached by the ShaderManager. Each 1.21.1 getter answered the ShaderInstance
# GameRenderer had loaded under a name (GameRenderer.reloadShaders / preloadUiShader: `new ShaderInstance(provider,
# "<name>", format)` then the static field the getter reads); 1.21.2's CoreShaders registers the same names. One
# staticRedirect per getter to CbRenderShadersGen.<same name>(), answering the ShaderManager's program for the
# CoreShaders key of that name (CbRenderShaders.program). Names are read off the bytecode on both sides; a getter whose
# name has no CoreShaders key is reported, not wired.
# ---------------------------------------------------------------------------------------------------------------------
GAME_RENDERER = MC + "class_757"
CORE_SHADERS = MC + "class_10142"
SHADER = MC + "class_5944"
PROGRAM_KEY = MC + "class_10156"


def family_shaders():
    sys.path.insert(0, HERE)
    import bytecode  # noqa: E402

    def klass(jar, c):
        with zipfile.ZipFile(jar) as z:
            return bytecode.read(z.read(c + ".class"))

    gv = klass(os.path.join(DATA, "jars/client-1.21.1-intermediary-code.jar"), GAME_RENDERER)
    cw = klass(os.path.join(DATA, "jars/client-1.21.2-intermediary-code.jar"), CORE_SHADERS)
    meths = {(m.name, m.desc): m for m in gv.methods}

    def putstatic_of(m):
        for op, arg in m.insns or []:
            if op == 0xb3 and arg[0] == "ref" and arg[2] == GAME_RENDERER and arg[4] == "L" + SHADER + ";":
                return arg[3]
        return None

    field_name = {}
    for m in gv.methods:
        last = None
        for op, arg in m.insns or []:
            if op in (0x12, 0x13) and isinstance(arg, tuple) and arg[0] == "str":
                last = arg[1]
            elif op == 0xb3 and arg[0] == "ref" and arg[2] == GAME_RENDERER and arg[4] == "L" + SHADER + ";" and last:
                field_name.setdefault(arg[3], last)
            elif op == 0xba and last:
                for a in arg[4]:
                    if isinstance(a, tuple) and a[0] == "mhandle" and a[2] == GAME_RENDERER:
                        lm = meths.get((a[3], a[4]))
                        f = putstatic_of(lm) if lm else None
                        if f:
                            field_name.setdefault(f, last)
    getters = {}
    for m in gv.methods:
        if m.access & 0x8 and m.access & 0x1 and m.desc == "()L" + SHADER + ";" and m.insns and len(m.insns) == 2 \
                and m.insns[0][0] == 0xb2 and m.insns[1][0] == 0xb0:
            getters[m.name] = m.insns[0][1][3]
    key_of = {}
    clinit = [m for m in cw.methods if m.name == "<clinit>"][0]
    last = None
    for op, arg in clinit.insns:
        if op in (0x12, 0x13) and isinstance(arg, tuple) and arg[0] == "str":
            last = arg[1]
        elif op == 0xb3 and arg[0] == "ref" and arg[4] == "L" + PROGRAM_KEY + ";" and last:
            key_of.setdefault(last, arg[3])
            last = None
    assigned = set()
    for m in gv.methods:
        for op, arg in m.insns or []:
            if op == 0xb3 and arg[0] == "ref" and arg[2] == GAME_RENDERER:
                assigned.add(arg[3])
    assigned |= {f for m in gv.methods for f in [putstatic_of(m)] if f}
    redirects, src, notes = {}, [], []
    for g, f in sorted(getters.items()):
        n = field_name.get(f)
        k = key_of.get(n) if n else None
        if f not in assigned:
            # 1.21.1 declares the field and the getter but never loads a shader into it: the getter answered null
            redirects[f"{GAME_RENDERER}.{g}()L{SHADER};"] = f"{RT}/CbRenderShadersGen"
            src += ["", f"    /** 1.21.1 GameRenderer.{g}(): 1.21.1 never loaded a shader into its field ({f}), it answered null */",
                    f"    public static net.minecraft.class_5944 {g}() {{", "        return null;", "    }"]
            continue
        if not k:
            notes.append(f"GameRenderer.{g}() (field {f}, shader {n!r}): no CoreShaders key of that name in 1.21.2: not wired")
            continue
        redirects[f"{GAME_RENDERER}.{g}()L{SHADER};"] = f"{RT}/CbRenderShadersGen"
        src += ["", f"    /** 1.21.1 GameRenderer.{g}(): the \"{n}\" core shader (CoreShaders.{k}) */",
                f"    public static net.minecraft.class_5944 {g}() {{",
                f"        return CbRenderShaders.program(net.minecraft.class_10142.{k});", "    }"]
    out = ["package top.dext.centurybridge.step.v1_21_2.rt;", "",
           "import net.fabricmc.api.EnvType;", "import net.fabricmc.api.Environment;", "",
           "/** Generated by core/tools/gen_s7render.py: 1.21.1 GameRenderer's core-shader getters (see CbRenderShaders). */",
           "@Environment(EnvType.CLIENT)", "public final class CbRenderShadersGen {", "", "    private CbRenderShadersGen() {", "    }"]
    src += ["", "    /** the CoreShaders key 1.21.2 registers under a 1.21.1 core shader name, or null */",
            "    public static net.minecraft.class_10156 byName(String name) {", "        return switch (name) {"]
    src += [f"            case \"{n}\" -> net.minecraft.class_10142.{k};" for n, k in sorted(key_of.items())]
    src += ["            default -> null;", "        };", "    }"]
    out += src + ["}"]
    open(os.path.join(RT_SRC, "CbRenderShadersGen.java"), "w", encoding="utf-8", newline="\n").write("\n".join(out) + "\n")
    for n in notes:
        print("  note:", n)
    print(f"shaders: {len(redirects)} of {len(getters)} GameRenderer getters wired")
    return redirects



class ClassInfoStub:
    fields = []


def write_mixin_config(generated):
    """the generated CbRenderTex<n>Mixin names in the client list, replacing the previous generation's"""
    cfg = json.load(open(MIXIN_CONFIG, encoding="utf-8"))
    def gen(m):
        for pre in ("CbRenderTex", "CbRenderState", "CbRenderModel", "CbRenderParts"):
            if m.startswith(pre) and m.endswith("Mixin") and m[len(pre):-len("Mixin")].replace("_", "").isdigit():
                return True
        return False
    keep = [m for m in cfg["client"] if not gen(m)]
    cfg["client"] = sorted(set(keep) | set(generated))
    text = open(MIXIN_CONFIG, encoding="utf-8").read()
    import re
    block = '  "client": [\n' + ",\n".join(f'    "{m}"' for m in cfg["client"]) + "\n  ],"
    text = re.sub(r'  "client": \[.*?\],', lambda _: block, text, count=1, flags=re.S)
    open(MIXIN_CONFIG, "w", encoding="utf-8", newline="\n").write(text)


def main():
    V = load(os.path.join(DATA, "jars/client-1.21.1-intermediary.jar"))
    W = load(os.path.join(DATA, "jars/client-1.21.2-intermediary.jar"))
    targets = bridge_targets(os.path.join(DATA, "jars/client-1.21.1-intermediary-code.jar"))

    def fam(c, root):
        f = {root}
        grew = True
        while grew:
            grew = False
            for cc in chain(c, V):
                for b, t in targets.get(cc, {}).items():
                    if b in f and t not in f:
                        f.add(t)
                        grew = True
        return f

    # every erasure of setupAnim / prepareMobModel V declares: (class, name, desc) of the non-bridge member
    setup, prepare = {}, {}
    for c, ci in sorted(V.items()):
        if ENTITY_MODEL not in chain(c, V):
            continue
        fs, fp = fam(c, SETUP), fam(c, PREPARE)
        for m in ci.methods:
            if m.access & (ACC_BRIDGE | ACC_SYNTH):
                continue
            if (m.name, m.desc) in fs:
                setup.setdefault((m.name, m.desc), []).append(c)
            if (m.name, m.desc) in fp:
                prepare.setdefault((m.name, m.desc), []).append(c)
    hooks, adapters = [], []
    for i, ((name, desc), owners) in enumerate(sorted(setup.items())):
        ent = desc[1:desc.index(";") + 1]
        # the topmost V class declaring this erasure: a mod class under it overrides this shape
        top = min(owners, key=lambda c: len(chain(c, V)))
        # the prepareMobModel erasure a model under `top` overrides: the one declared on top's chain nearest it
        pshape = PREPARE
        for cc in chain(top, V):
            hit = [s for s, os_ in prepare.items() if cc in os_]
            if hit:
                pshape = hit[0]
                break
        iface = f"Anim{i}"
        adapters.append((iface, name, desc, ent, pshape))
        hooks.append({
            "old": name + desc,
            "new": W_SETUP[0] + W_SETUP[1],
            "onlyUnder": top,
            "adapter": f"{RT}/CbRenderModelsGen${iface}",
            "bridge": f"{RT}/CbRenderModelsGen.{iface.lower()}(L{RT}/CbRenderModelsGen${iface};L{STATE};)V",
            "guard": f"{RT}/Guard",
            "basis": f"1.21.1 setupAnim{desc} of {top} (javac's erasure of EntityModel.setupAnim there): 1.21.2 calls "
                     f"EntityModel.setupAnim(state); the synthesized one hands the mod's method what 1.21.1's renderer "
                     f"passed, from the entity the state was extracted from (CbRenderModels.setupAnim)",
        })
    os.makedirs(RT_SRC, exist_ok=True)
    src = [
        "package top.dext.centurybridge.step.v1_21_2.rt;",
        "",
        "import net.fabricmc.api.EnvType;",
        "import net.fabricmc.api.Environment;",
        "",
        "/** Generated by core/tools/gen_s7render.py: one adapter per 1.21.1 setupAnim erasure (see CbRenderModels). */",
        "@Environment(EnvType.CLIENT)",
        "public final class CbRenderModelsGen {",
        "",
        "    private CbRenderModelsGen() {",
        "    }",
    ]
    for iface, name, desc, ent, pshape in adapters:
        et = jtype(ent)
        pt = jtype(pshape[1][1:pshape[1].index(";") + 1])
        src += [
            "",
            f"    /** a 1.21.1 model overriding setupAnim{desc} */",
            f"    public interface {iface} {{",
            f"        void {name}({et} entity, float limbAngle, float limbDistance, float ageInTicks, float headYaw, float headPitch);",
            "",
            f"        /** prepareMobModel{pshape[1]}: 1.21.1's EntityModel one is empty; a model overriding it implements it */",
            f"        default void {pshape[0]}({pt} entity, float limbAngle, float limbDistance, float partialTick) {{",
            "        }",
            "    }",
            "",
            f"    public static void {iface.lower()}({iface} self, net.minecraft.class_10017 state) {{",
            "        CbRenderModels.setupAnim(self, state, (e, a) -> {",
            f"            if (e instanceof {et} t) {{",
            f"                if (e instanceof {pt} p) {{",
            f"                    self.{pshape[0]}(p, a.walkPos(), a.walkSpeed(), a.partialTick());",
            "                }",
            f"                self.{name}(t, a.walkPos(), a.walkSpeed(), a.bob(), a.headYaw(), a.headPitch());",
            "            }",
            "        });",
            "    }",
        ]
    src.append("}")
    open(os.path.join(RT_SRC, "CbRenderModelsGen.java"), "w", encoding="utf-8", newline="\n").write("\n".join(src) + "\n")
    print(f"setupAnim erasures: {len(setup)}, prepareMobModel erasures: {len(prepare)}; hooks {len(hooks)}")
    tex_hooks, fills, mixins, covers = family_texture(V, W, targets)
    hooks += tex_hooks
    st_hooks, st_mixins, st_covers = family_state(V, W, targets)
    hooks += st_hooks
    mixins += st_mixins
    covers += st_covers
    md_mixins, md_covers = family_models(V, W, targets, setup, prepare)
    mixins += md_mixins
    covers += md_covers
    pt_mixins, pt_covers = family_parts(V, W)
    mixins += pt_mixins
    covers += pt_covers
    shaders = family_shaders()
    frag = {"overrideHooks": hooks, "abstractFills": fills, "staticRedirects": shaders, "covers": sorted(covers)}
    open(FRAGMENT, "w", encoding="utf-8", newline="\n").write(json.dumps(frag, indent=1, ensure_ascii=False) + "\n")
    write_mixin_config(mixins)


if __name__ == "__main__":
    main()
