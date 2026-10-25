"""Recipe bridges for climb step 1.20.1 -> 1.20.2, derived from the step's damage table.

1.20.2 took the recipe id out of the recipe (Recipe.getId(), the id argument of every recipe
constructor, the id argument of RecipeSerializer.read(id, ..)) and put it into RecipeEntry
(class_8786), which now travels wherever 1.20.1 passed a bare Recipe (recipe book, crafting menus,
recipe unlocker, grid placement, advancements trigger...). Four families of damage, each derived
here from core/segments/damage/1.20.1__1.20.2.damage.tsv against the two releases' class files:

  ctors    a recipe constructor that lost its Identifier argument (the rest unchanged): NEW sites
           go to a factory that builds the 1.20.2 recipe and records the id (CbRecipeIds); a
           super(id, ..) delegation is reshaped in place, its id dropped into CbRecipeIds.stash,
           which the recipe base class claims when its constructor returns (CbRecipeIdClaim).
  readers  the vanilla serializers' read(id, json) / read(id, buf): a converted subclass that
           overrides them gets an adapter interface plus the 1.20.2 shapes vanilla calls (codec(),
           read(buf)) synthesized to reach its own readers. read(id, buf) calls run the subclass's
           reader when it has one, vanilla's otherwise, and its super calls run vanilla's own
           implementation on it; read(id, json) is a member of the vanilla serializers again (the
           layer's CbRecipeJson* mixins), so its calls and super calls are left to link there.
  entries  a method whose Recipe parameter or return became RecipeEntry: calls (and a mod's
           super calls) are reshaped in place, the recipe converted to the entry vanilla holds for
           it and back; a mod override of the old shape gets the new shape synthesized, forwarding.
  (getId, the protected id fields and the special-recipe factory are wired by hand in the same
  fragment; see s1misc.json.)

Writes rt/v1_20_2/CbRecipeCtors.java, rt/v1_20_2/CbLegacySerializers.java and
rt/v1_20_2/CbRecipeSerializers.java in the 1.20.2 tree, and updates its own entries in
core/segments/shims-1.20.2.json.d/s1misc.json (other entries of that fragment are left alone).

  python core/tools/gen_recipe_bridges_1202.py <data dir>
"""
import collections
import json
import os
import re
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402
from classfile import ACC_ABSTRACT, ACC_FINAL, ACC_INTERFACE, ACC_PUBLIC, ACC_STATIC, split_args  # noqa: E402

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
DAMAGE = os.path.join(ROOT, "core/segments/damage/1.20.1__1.20.2.damage.tsv")
FRAGMENT = os.path.join(ROOT, "core/segments/shims-1.20.2.json.d/s1misc.json")
SRC = os.path.join(ROOT, "core/shims/1.20.2/src/top/dext/centurybridge/rt/v1_20_2")
PKG = "top/dext/centurybridge/rt/v1_20_2"

ID = "Lnet/minecraft/class_2960;"
RECIPE = "Lnet/minecraft/class_1860;"
ENTRY = "Lnet/minecraft/class_8786;"
JSON = "Lcom/google/gson/JsonObject;"
BUF = "Lnet/minecraft/class_2540;"
SERIALIZER = "net/minecraft/class_1865"
CODEC_SHAPE = "method_53736()Lcom/mojang/serialization/Codec;"
# this generator's area of the step (the owners' mojang names): the other step-1 areas wire their own
AREA = re.compile(r"^(?!net/minecraft/(advancements|server/advancements|server/PlayerAdvancements|world/level/storage"
                  r"|data/|network|server/network|client/|realms)|com/mojang/realmsclient)")
# the recipe base classes whose 1.20.1 constructors took the id; CbRecipeIdClaim claims the stash in each
CLAIMERS = ["net/minecraft/class_1852", "net/minecraft/class_1867", "net/minecraft/class_1869",
            "net/minecraft/class_1874", "net/minecraft/class_3972", "net/minecraft/class_8060",
            "net/minecraft/class_8062"]


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith("net/minecraft/"):
                c = classfile.read(z.read(n))
                if c:
                    out[c.name] = c
    return out


def mojang(data):
    m = {}
    for line in open(os.path.join(data, "mappings/inter-to-mojang-1.21.11.tsv"), encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "C":
            m[f[1]] = f[2]
    # a class gone before 1.21.11 carries its name from the last release that had it (class_names.py),
    # as step_check names the areas
    names = os.path.join(data, "mappings/inter-to-mojang-classes.tsv")
    if os.path.exists(names):
        for line in open(names, encoding="utf-8"):
            f = line.rstrip("\n").split("\t")
            if len(f) >= 3 and f[0] == "C":
                m[f[1]] = f[2]
    return m


def mojang_name(c, moj):
    if c in moj:
        return moj[c]
    d = c.rfind("$")
    return mojang_name(c[:d], moj) + c[d:] if d > 0 else c


def in_area(c, moj):
    return AREA.search(mojang_name(c, moj)) is not None


class Hier:
    def __init__(self, classes):
        self.c = classes

    def supers(self, name):
        out, todo = [], [name]
        while todo:
            x = todo.pop(0)
            if x is None or x in out:
                continue
            out.append(x)
            k = self.c.get(x)
            if k:
                todo.append(k.super)
                todo.extend(k.interfaces)
        return out

    def method(self, owner, name, desc):
        k = self.c.get(owner)
        return next((m for m in k.methods if m.name == name and m.desc == desc), None) if k else None

    def declarer(self, owner, name, desc):
        """the topmost class/interface of owner's hierarchy declaring name+desc (the root a hook keys on)"""
        root = None
        for s in self.supers(owner):
            if self.method(s, name, desc):
                root = s
        return root


def java_type(d):
    if d.startswith("["):
        return java_type(d[1:]) + "[]"
    prim = {"Z": "boolean", "B": "byte", "C": "char", "S": "short", "I": "int", "J": "long",
            "F": "float", "D": "double", "V": "void"}
    if d in prim:
        return prim[d]
    return d[1:-1].replace("/", ".").replace("$", ".")


def simple(cls, moj):
    """a readable Java identifier for a class: its mojang simple name when it has one"""
    def name(c):
        if c in moj:
            return moj[c].rsplit("/", 1)[1]
        d = c.rfind("$")
        return name(c[:d]) + "_" + c[d + 1:] if d > 0 else c.rsplit("/", 1)[1]
    return name(cls).replace("$", "_")


def rows():
    out = []
    with open(DAMAGE, encoding="utf-8") as f:
        next(f)
        for line in f:
            r = line.rstrip("\r\n").split("\t")
            out.append({"usage": r[0], "owner": r[1], "name": r[2], "desc": r[3], "kinds": r[4]})
    return out


def main():
    data = sys.argv[1]
    V = load(os.path.join(data, "jars/client-1.20.1-intermediary.jar"))
    W = load(os.path.join(data, "jars/client-1.20.2-intermediary.jar"))
    hv, hw = Hier(V), Hier(W)
    moj = mojang(data)
    dmg = rows()
    base = json.load(open(os.path.join(ROOT, "core/segments/shims-1.20.2.json"), encoding="utf-8"))
    base_static = set(base.get("staticRedirects", {}))
    wiring = collections.defaultdict(dict)
    arrays = collections.defaultdict(list)
    report = []

    # ------------------------------------------------------------------ ctors
    ctor_rows = collections.defaultdict(set)
    for r in dmg:
        if r["name"] == "<init>" and r["usage"] in ("new", "super-ctor"):
            ctor_rows[(r["owner"], r["desc"])].add(r["usage"])
    factories = []
    for (owner, desc), usages in sorted(ctor_rows.items()):
        args, ret = split_args(desc)
        if args.count(ID) != 1 or owner not in W or not any(c in hw.supers(owner) for c in CLAIMERS):
            continue
        k = args.index(ID)
        ndesc = "(" + "".join(a for i, a in enumerate(args) if i != k) + ")V"
        wc = hw.method(owner, "<init>", ndesc)
        if wc is None:
            report.append(f"ctor {owner}{desc}: no 1.20.2 constructor {ndesc}")
            continue
        key = f"{owner}.<init>{desc}"
        if "super-ctor" in usages:
            ops = []
            for i in range(len(args)):
                ops.append(f"drop:{i} {PKG}/CbRecipeIds#stash({ID})V" if i == k else f"keep:{i}")
            wiring["superCtorReshapes"][key] = {
                "desc": ndesc, "ops": ops,
                "basis": "1.20.2 dropped the recipe id argument; the id goes to CbRecipeIds.stash, claimed by the "
                         "recipe base constructor as it returns"}
        if "new" in usages and not W[owner].access & ACC_ABSTRACT:
            wiring["ctorRedirects"][key] = f"{PKG}/CbRecipeCtors#cbNew{simple(owner, moj)}"
            factories.append((owner, args, k, wc.access & ACC_PUBLIC != 0))
    write_ctors(factories, moj)

    # ------------------------------------------------------------------ serializer readers
    readers = collections.defaultdict(dict)  # owner -> {"json": (name, desc), "buf": (name, desc)}
    for r in dmg:
        o = r["owner"]
        if o not in V or V[o].access & ACC_INTERFACE or SERIALIZER not in hv.supers(o):
            continue
        a, ret = split_args(r["desc"]) if r["desc"].startswith("(") else ([], None)
        if a == [ID, JSON]:
            readers[o]["json"] = (r["name"], r["desc"])
        elif a == [ID, BUF]:
            readers[o]["buf"] = (r["name"], r["desc"])
    sers = []
    for o in sorted(readers):
        j, b = readers[o].get("json"), readers[o].get("buf")
        if not j or not b:
            report.append(f"serializer {o}: readers incomplete {readers[o]}")
            continue
        bnew = "(" + BUF + ")" + split_args(b[1])[1]
        if hw.method(o, b[0], bnew) is None or hw.method(o, "method_53736", "()Lcom/mojang/serialization/Codec;") is None:
            report.append(f"serializer {o}: 1.20.2 lacks {b[0]}{bnew} or codec()")
            continue
        sers.append((o, j, b, bnew))
    write_serializers(sers, moj)
    dropped = collections.defaultdict(list)
    for o, j, b, bnew in sers:
        s = simple(o, moj)
        jt, bt = f"{PKG}/CbLegacySerializers${s}Json", f"{PKG}/CbLegacySerializers${s}Buf"
        arrays["adapterHooks"].append({
            "under": o, "adapter": jt, "requires": [j[0] + j[1]],
            "synth": [{"new": CODEC_SHAPE, "call": f"{PKG}/CbRecipeSerializers.legacyCodec(L{jt};)Lcom/mojang/serialization/Codec;"}]})
        arrays["adapterHooks"].append({
            "under": o, "adapter": bt, "requires": [b[0] + b[1]],
            "synth": [{"new": b[0] + bnew, "call": f"{PKG}/CbRecipeSerializers.legacyRead(L{bt};{BUF}){split_args(bnew)[1]}"}]})
        # read(id, json) is a member of the vanilla serializer again (the layer's CbRecipeJson* mixins, 1.20.1's
        # body; 1.20.2's RecipeManager reads through it): calls and super calls link to it as they did on 1.20.1
        dropped["instanceRedirects"].append(f"{o}.{j[0]}{j[1]}")
        dropped["superRedirects"].append(f"{o}.{j[0]}{j[1]}")
        wiring["instanceRedirects"][f"{o}.{b[0]}{b[1]}"] = f"{PKG}/CbRecipeSerializers"
        wiring["superRedirects"][f"{o}.{b[0]}{b[1]}"] = f"{PKG}/CbRecipeSerializers#super_{b[0]}"

    # ------------------------------------------------------------------ Recipe <-> RecipeEntry
    seen = set()
    for r in dmg:
        o, n, d = r["owner"], r["name"], r["desc"]
        if not d.startswith("(") or n.startswith("<") or RECIPE not in d or (o, n, d) in seen:
            continue
        seen.add((o, n, d))
        nd = d.replace(RECIPE, ENTRY)
        root = hv.declarer(o, n, d)
        if root is None or root not in W or not in_area(root, moj):
            continue
        if f"{root}.{n}{d}" in base_static:
            continue  # a static redirect already answers it
        wm = None
        for s in hw.supers(root):
            wm = hw.method(s, n, nd)
            if wm:
                break
        if wm is None:
            report.append(f"entries {o}.{n}{d}: 1.20.2 has no {n}{nd}")
            continue
        vm = hv.method(root, n, d)
        a, ret = split_args(d)
        key = f"{root}.{n}{d}"
        if key in wiring["callReshapes"]:
            continue
        ops = [f"conv:{i} {PKG}/CbRecipeIds#entry({RECIPE}){ENTRY}" if t == RECIPE else f"keep:{i}" for i, t in enumerate(a)]
        cr = {"desc": nd, "ops": ops}
        if ret == RECIPE:
            cr["ret"] = f"conv {PKG}/CbRecipeIds#value({ENTRY}){RECIPE}"
        cr["basis"] = "1.20.2 passes the RecipeEntry where 1.20.1 passed the bare Recipe: the recipe goes in as the entry vanilla holds for it (equal by id), comes back as its recipe"
        wiring["callReshapes"][key] = cr
        if vm.access & (ACC_STATIC | ACC_FINAL) == 0 and not V[root].access & ACC_FINAL:
            hook = {"old": n + d, "new": n + nd, "onlyUnder": root}
            if RECIPE in a:
                hook["wrap"] = f"{PKG}/CbRecipeIds.value({ENTRY}){RECIPE}"
            if ret == RECIPE:
                hook["unwrap"] = f"{PKG}/CbRecipeIds.entry({RECIPE}){ENTRY}"
            hook["basis"] = ("vanilla 1.20.2 calls the RecipeEntry shape; it forwards to the mod's Recipe override, whose "
                             "own super call is reshaped to the new shape (callReshapes), so nothing comes back here")
            arrays["overrideHooks"].append(hook)

    merge(wiring, arrays, dropped)
    for line in report:
        print("  !", line)
    print(f"ctors: {len(wiring['superCtorReshapes'])} super reshapes, {len(wiring['ctorRedirects'])} factories; "
          f"serializers: {len(sers)}; entry reshapes: {len(wiring['callReshapes'])}, "
          f"override hooks: {len(arrays['overrideHooks'])}")


def write_ctors(factories, moj):
    lines = []
    for owner, args, k, public in factories:
        jt = java_type("L" + owner + ";")
        params = ", ".join(f"{java_type(a)} a{i}" for i, a in enumerate(args))
        rest = ", ".join(f"a{i}" for i in range(len(args)) if i != k)
        make = f"new {jt}({rest})" if public else f"top.dext.centurybridge.shims.v1_20_2.{simple(owner, moj)}Ctor.cb$new({rest})"
        lines.append(f"    /** new {simple(owner, moj)}(id, ...) */\n"
                     f"    public static {jt} cbNew{simple(owner, moj)}({params}) {{\n"
                     f"        return CbRecipeIds.remember({make}, a{k});\n"
                     f"    }}\n")
    body = "\n".join(lines)
    src = f"""package top.dext.centurybridge.rt.v1_20_2;

/**
 * GENERATED by core/tools/gen_recipe_bridges_1202.py -- do not edit.
 *
 * 1.20.1 recipe constructors at NEW sites (ctorRedirects): 1.20.2 dropped the id argument, so the
 * recipe is built with the 1.20.2 constructor and its id recorded where every converted
 * getId() / RecipeEntry conversion finds it (CbRecipeIds).
 */
@SuppressWarnings({{"rawtypes", "unchecked"}})
public final class CbRecipeCtors {{

{body}
    private CbRecipeCtors() {{
    }}
}}
"""
    open(os.path.join(SRC, "CbRecipeCtors.java"), "w", encoding="utf-8", newline="\n").write(src)


def write_serializers(sers, moj):
    adapters, bridges = [], []
    for o, j, b, bnew in sers:
        s = simple(o, moj)
        jt, bt = f"{s}Json", f"{s}Buf"
        cls = java_type("L" + o + ";")
        jr = java_type(split_args(j[1])[1])
        br = java_type(split_args(b[1])[1])
        adapters.append(f"""    /** {s}.read(id, json), declared by a converted subclass */
    public interface {jt} {{
        {jr} {j[0]}(net.minecraft.class_2960 id, com.google.gson.JsonObject json);
    }}

    /** {s}.read(id, buf), declared by a converted subclass */
    public interface {bt} {{
        {br} {b[0]}(net.minecraft.class_2960 id, net.minecraft.class_2540 buf);
    }}
""")
        bridges.append(f"""    // ---- {s} ({o})

    /** codec() synthesized into a subclass with its own read(id, json): JSON reaches that reader */
    public static com.mojang.serialization.Codec legacyCodec(CbLegacySerializers.{jt} self) {{
        return CbRecipeSerializerSupport.codecOf(self, (id, json) -> self.{j[0]}(id, json));
    }}

    /** read(buf) synthesized into a subclass with its own read(id, buf), as vanilla now calls it */
    public static {br} legacyRead(CbLegacySerializers.{bt} self, net.minecraft.class_2540 buf) {{
        return self.{b[0]}(CbRecipeSerializerSupport.currentId(), buf);
    }}

    /** serializer.read(id, buf): the serializer's own reader when it declares one, vanilla's otherwise */
    public static {br} {b[0]}({cls} self, net.minecraft.class_2960 id, net.minecraft.class_2540 buf) {{
        if (self instanceof CbLegacySerializers.{bt} own) {{
            return own.{b[0]}(id, buf);
        }}
        return CbRecipeSerializerSupport.withId(id, () -> self.{b[0]}(buf));
    }}

    /** super.read(id, buf) in a subclass: vanilla's reader, run on this serializer */
    public static {br} super_{b[0]}({cls} self, net.minecraft.class_2960 id, net.minecraft.class_2540 buf) {{
        java.lang.invoke.MethodHandle h = CbSuper.special({cls}.class, "{b[0]}",
            java.lang.invoke.MethodType.methodType({br}.class, net.minecraft.class_2540.class));
        return CbRecipeSerializerSupport.withId(id, () -> {{
            try {{
                return ({br}) h.invoke(self, buf);
            }} catch (Throwable t) {{
                throw CbSuper.rethrow(t);
            }}
        }});
    }}
""")
    open(os.path.join(SRC, "CbLegacySerializers.java"), "w", encoding="utf-8", newline="\n").write(f"""package top.dext.centurybridge.rt.v1_20_2;

/**
 * GENERATED by core/tools/gen_recipe_bridges_1202.py -- do not edit.
 *
 * The 1.20.1 reader shapes of the vanilla recipe serializers. 1.20.2 replaced read(id, json) with
 * codec() and read(id, buf) with read(buf); a converted subclass that overrides an old reader gets
 * the matching interface added (adapterHooks), so its own reader stays reachable, typed.
 */
public final class CbLegacySerializers {{

{chr(10).join(adapters)}
    private CbLegacySerializers() {{
    }}
}}
""")
    open(os.path.join(SRC, "CbRecipeSerializers.java"), "w", encoding="utf-8", newline="\n").write(f"""package top.dext.centurybridge.rt.v1_20_2;

/**
 * GENERATED by core/tools/gen_recipe_bridges_1202.py -- do not edit.
 *
 * The 1.20.1 reader contract of the vanilla recipe serializers on the 1.20.2 API (see
 * {{@link CbRecipeSerializerSupport}} for the shared pieces): what vanilla calls now reaches a
 * converted subclass's own readers, a call to an old reader dispatches as 1.20.1 did, and a
 * subclass's super call runs vanilla's own implementation on that subclass.
 */
@SuppressWarnings({{"rawtypes", "unchecked"}})
public final class CbRecipeSerializers {{

{chr(10).join(bridges)}
    private CbRecipeSerializers() {{
    }}
}}
""")


def merge(wiring, arrays, dropped):
    frag = {}
    if os.path.exists(FRAGMENT):
        frag = json.load(open(FRAGMENT, encoding="utf-8"))
    for sec, keys in dropped.items():
        for k in keys:
            frag.get(sec, {}).pop(k, None)
    for sec, entries in wiring.items():
        frag.setdefault(sec, {}).update(entries)
    for sec, items in arrays.items():
        have = frag.setdefault(sec, [])

        def ident(x):
            return (x.get("under"), x.get("adapter"), x.get("old"), x.get("onlyUnder"))
        mine = {ident(x): x for x in items}
        # in place, so a regeneration that changes nothing leaves the fragment as it was
        kept = [mine.pop(ident(x)) if ident(x) in mine else x for x in have]
        frag[sec] = kept + [x for x in items if ident(x) in mine]
    with open(FRAGMENT, "w", encoding="utf-8", newline="\n") as f:
        json.dump(frag, f, indent=1, ensure_ascii=False)
        f.write("\n")


if __name__ == "__main__":
    main()
