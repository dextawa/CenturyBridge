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
    frag = {"overrideHooks": hooks}
    open(FRAGMENT, "w", encoding="utf-8", newline="\n").write(json.dumps(frag, indent=1, ensure_ascii=False) + "\n")
    print(f"setupAnim erasures: {len(setup)}, prepareMobModel erasures: {len(prepare)}; hooks {len(hooks)}")


if __name__ == "__main__":
    main()
