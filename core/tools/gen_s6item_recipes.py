"""Step 1.20.6 -> 1.21, area s6item: the recipe classes' matches / assemble from their 1.20.6 container to 1.21's
recipe input, read off the damage table and both releases' bytecode.

1.20.6's recipes took the container itself (Recipe<C extends Container>: a crafting grid's
CraftingContainer, a furnace's Container); 1.21's take a RecipeInput built from it (CraftingInput,
SingleRecipeInput, SmithingRecipeInput). For every damage row of a vanilla recipe class's
matches / assemble shape (the class's own name for the typed method, or Recipe's for the classes
that implemented Recipe<Container>):

  calls       -> "callReshapes": the 1.20.6 container converted to the input the 1.21 method takes
                 (CbItemRecipes.cbCraftingInput / cbSingleRecipeInput / cbSmithingRecipeInput, which
                 remember the container so a 1.20.6 recipe asked by 1.21 gets it back);
  overrides   -> "overrideHooks": the 1.21 shape vanilla calls, forwarding to the mod's 1.20.6 method
                 with the input turned back into its container (cbCraftingContainer / cbContainer).

Where 1.21's shape is a bridge onto a typed method of another name (AbstractCookingRecipe's
matches(RecipeInput) onto matches(SingleRecipeInput)), the hook gives the mod class the typed
method: vanilla reaches it through either shape (the bridge calls it). A mod's super call cannot
take the reshaped call there -- super's bridge would call the typed method virtually, that is the
mod's own hook again -- so it goes through a super bridge ("superBridge", generated into
rt/CbItemRecipeSupers.java): the container converted to the typed input and handed to super's
typed method through an accessor the class gets (cb$super$<name>, an INVOKESPECIAL on its
superclass). No guard: no path leads back into the hook.

getRemainingItems (whose per-slot answer needs the grid behind the input) and Recipe's own
interface-level shapes (whose input form only the receiver's type tells) are wired by hand in
s6item.json.

  python gen_s6item_recipes.py <damage.tsv> <V stub jar> <W stub jar> <W code jar> <fragment.json> <supers.java>
"""
import json
import os
import re
import subprocess
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

PKG = "top/dext/centurybridge/step/v1_21/rt"
R = f"{PKG}/CbItemRecipes"
S = f"{PKG}/CbItemRecipeSupers"
RECIPE = "net/minecraft/class_1860"
TO_W = {  # (1.20.6 parameter, the called 1.21 parameter, the typed method's parameter) -> converter
    ("Lnet/minecraft/class_8566;", "Lnet/minecraft/class_9694;", "Lnet/minecraft/class_9694;"):
        f"{R}#cbCraftingInput(Lnet/minecraft/class_8566;)Lnet/minecraft/class_9694;",
    ("Lnet/minecraft/class_1263;", "Lnet/minecraft/class_9695;", "Lnet/minecraft/class_9696;"):
        f"{R}#cbSingleRecipeInput(Lnet/minecraft/class_1263;)Lnet/minecraft/class_9695;",
    ("Lnet/minecraft/class_1263;", "Lnet/minecraft/class_9695;", "Lnet/minecraft/class_9697;"):
        f"{R}#cbSmithingRecipeInput(Lnet/minecraft/class_1263;)Lnet/minecraft/class_9695;",
}
TO_TYPED = {  # (1.20.6 parameter, the typed method's parameter) -> converter, in Java, for the super bridges
    ("Lnet/minecraft/class_8566;", "Lnet/minecraft/class_9694;"): "CbItemRecipes.cbCraftingInput",
    ("Lnet/minecraft/class_1263;", "Lnet/minecraft/class_9696;"): "CbItemRecipes.cbSingleInput",
    ("Lnet/minecraft/class_1263;", "Lnet/minecraft/class_9697;"): "CbItemRecipes.cbSmithingInput",
}
TO_V = {  # 1.20.6 parameter -> converter from the 1.21 input
    "Lnet/minecraft/class_8566;": f"{R}.cbCraftingContainer(Lnet/minecraft/class_9694;)Lnet/minecraft/class_8566;",
    "Lnet/minecraft/class_1263;": f"{R}.cbContainer(Lnet/minecraft/class_9695;)Lnet/minecraft/class_1263;",
}
CALLS = ("call", "call-inherited", "call-protected")
OVERRIDES = ("override+super", "implement", "implement-inherited")
JAVA_PRIM = {"Z": "boolean", "B": "byte", "C": "char", "S": "short", "I": "int", "J": "long", "F": "float", "D": "double", "V": "void"}


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith("net/minecraft/"):
                c = classfile.read(z.read(n))
                if c:
                    out[c.name] = c
    return out


def supers(classes, c):
    out, todo = [], [c]
    while todo:
        x = todo.pop(0)
        if x is None or x in out:
            continue
        out.append(x)
        k = classes.get(x)
        if k:
            todo.append(k.super)
            todo.extend(k.interfaces)
    return out


def args(desc):
    return classfile.split_args(desc)[0]


def ret(desc):
    return desc[desc.index(")") + 1:]


def java_type(d):
    dims = len(d) - len(d.lstrip("["))
    d = d[dims:]
    t = JAVA_PRIM[d] if d in JAVA_PRIM else d[1:-1].replace("/", ".").replace("$", ".")
    return t + "[]" * dims


def bridge_target(code_jar, owner, name, desc):
    """the typed method a 1.21 bridge calls: (name, desc), from its bytecode"""
    out = subprocess.run(["javap", "-c", "-p", "-cp", code_jar, owner.replace("/", ".")], capture_output=True, text=True, check=True).stdout
    body, inside = [], False
    sig = f"{name}("
    for line in out.splitlines():
        if line.startswith("  ") and not line.startswith("   ") and "(" in line:
            inside = sig in line and "bridge" not in line
            continue
        if inside:
            body.append(line)
    for line in body:
        m = re.search(r"invokevirtual .*// Method (?:[\w/$]+\.)?(method_\d+):(\([^)]*\)\S+)", line)
        if m:
            return m.group(1), m.group(2)
    return None


def main():
    damage, vjar, wjar, wcode, frag, supers_java = sys.argv[1:7]
    v, w = load(vjar), load(wjar)
    rows = [l.rstrip("\n").split("\t") for l in open(damage, encoding="utf-8")][1:]
    calls, hooks, seen = {}, [], set()
    typed_shapes = {}   # hook owner -> [typed name + desc] its super-access interface declares
    bridges = []        # (owner, old name, old desc, typed name, typed desc)
    for r in rows:
        usage, owner, name, desc = r[0], r[1], r[2], r[3]
        if owner == RECIPE or not name.startswith("method_") or RECIPE not in supers(v, owner):
            continue
        if ret(desc) == "Lnet/minecraft/class_2371;":
            continue  # getRemainingItems: by hand (the per-slot answer needs the grid behind the input)
        m = re.search(r"method " + re.escape(name) + r"(\([^)]*\)\S+)", r[5])
        if not m:
            continue
        wdesc = m.group(1)
        # where 1.21 declares it, and whether it is a bridge onto a typed method
        wm, home = None, None
        for c in supers(w, owner):
            k = w.get(c)
            if k:
                for x in k.methods:
                    if x.name == name and x.desc == wdesc:
                        wm, home = x, c
                        break
            if wm:
                break
        if wm is None:
            continue
        typed = (name, wdesc)
        if wm.access & classfile.ACC_BRIDGE:
            t = bridge_target(wcode, home, name, wdesc)
            if t is None:
                raise SystemExit(f"{home}.{name}{wdesc}: a bridge whose target is not an invokevirtual")
            typed = t
        va, wa, ta = args(desc), args(wdesc), args(typed[1])
        if usage in CALLS:
            ops = []
            for a, b, t in zip(va, wa, ta):
                ops.append("keep" if a == b else "conv " + TO_W[(a, b, t)])
            calls[f"{owner}.{name}{desc}"] = {"desc": wdesc, "ops": ops,
                "basis": f"generated: the 1.20.6 container as the input 1.21's {typed[0]} takes" + (" (through its bridge)" if typed != (name, wdesc) else "")}
        elif usage in OVERRIDES:
            changed = [a for a, b in zip(va, ta) if a != b]
            if len(changed) != 1:
                raise SystemExit(f"{owner}.{name}{desc}: {len(changed)} changed parameters")
            key = (owner, name + desc)
            if key in seen:
                continue
            seen.add(key)
            h = {"old": name + desc, "new": typed[0] + typed[1], "onlyUnder": owner, "wrap": TO_V[changed[0]]}
            if typed != (name, wdesc):
                access = f"{S}$Super_{owner.rsplit('/', 1)[1]}"
                h["superBridge"] = f"{S}.super_{typed[0]}(L{access};{desc[1:]}"
                h["superAccess"] = access
                typed_shapes.setdefault(owner, [])
                if typed[0] + typed[1] not in typed_shapes[owner]:
                    typed_shapes[owner].append(typed[0] + typed[1])
                bridges.append((owner, name, desc, typed[0], typed[1]))
                h["basis"] = (f"generated: 1.21 calls {name}{wdesc}, a bridge onto {typed[0]}: the mod class gets {typed[0]}, forwarding"
                              " to its 1.20.6 method with the input as its container; its super call reaches super's"
                              f" {typed[0]} (CbItemRecipeSupers)")
            else:
                h["basis"] = "generated: 1.21 hands the recipe input, the mod's 1.20.6 method takes the container it was made from"
            hooks.append(h)
    for h in hooks:
        if "superAccess" in h:
            h["superAccessors"] = list(typed_shapes[h["onlyUnder"]])
            h["basis"] = h.pop("basis")  # keep the basis last
    data = json.load(open(frag, encoding="utf-8")) if os.path.exists(frag) else {}
    data["callReshapes"] = dict(sorted(calls.items()))
    data["overrideHooks"] = hooks
    with open(frag, "w", encoding="utf-8", newline="\n") as o:
        json.dump(data, o, indent=1)
        o.write("\n")
    write_supers(supers_java, typed_shapes, bridges)
    print(f"{len(calls)} callReshapes, {len(hooks)} overrideHooks ({len(bridges)} through super bridges) -> {frag}, {supers_java}")


def write_supers(path, typed_shapes, bridges):
    out = ["package top.dext.centurybridge.step.v1_21.rt;", "",
           "/**",
           " * GENERATED by core/tools/gen_s6item_recipes.py from the step's damage table and 1.21's bytecode -- regenerate,",
           " * never edit.",
           " *",
           " * <p>Super calls of a 1.20.6 recipe override whose 1.21 shape is a bridge onto a typed method of another name",
           " * (1.21's AbstractCookingRecipe.matches(RecipeInput, Level) calls matches(SingleRecipeInput, Level)): the hook gives",
           " * the mod class the typed method, so super's bridge would come back into it. A mod's super.old(container, ..)",
           " * comes here instead (the hook's \"superBridge\") and reaches super's typed method through the accessor the class",
           " * gets with the interface (cb$super$&lt;name&gt;, an INVOKESPECIAL on its superclass), the container converted to",
           " * the input that method takes (CbItemRecipes, which remembers the container).",
           " */",
           "public final class CbItemRecipeSupers {", "",
           "    private CbItemRecipeSupers() {",
           "    }"]
    for owner in sorted(typed_shapes):
        simple = owner.rsplit("/", 1)[1]
        out += ["", f"    /** the super accessors a mod class under {owner} gets with the hooks' super bridges */",
                f"    public interface Super_{simple} {{"]
        for shape in typed_shapes[owner]:
            n, d = shape[:shape.index("(")], shape[shape.index("("):]
            ps = ", ".join(f"{java_type(a)} a{i}" for i, a in enumerate(args(d)))
            out.append(f"        {java_type(ret(d))} cb$super${n}({ps});")
        out.append("    }")
    for owner, name, desc, tname, tdesc in bridges:
        simple = owner.rsplit("/", 1)[1]
        va, ta = args(desc), args(tdesc)
        ps = ", ".join([f"Super_{simple} self"] + [f"{java_type(a)} a{i}" for i, a in enumerate(va)])
        passed = []
        for i, (a, t) in enumerate(zip(va, ta)):
            passed.append(f"a{i}" if a == t else f"{TO_TYPED[(a, t)]}(a{i})")
        r = ret(desc)
        if r != ret(tdesc):
            raise SystemExit(f"{owner}.{name}{desc}: the typed method {tname}{tdesc} returns another type")
        call = f"self.cb$super${tname}({', '.join(passed)})"
        out += ["", f"    /** super.{name}{desc} under {owner}: super's {tname}{tdesc} */",
                f"    public static {java_type(r)} super_{tname}({ps}) {{",
                f"        {'' if r == 'V' else 'return '}{call};",
                "    }"]
    out += ["}", ""]
    with open(path, "w", encoding="utf-8", newline="\n") as o:
        o.write("\n".join(out))


if __name__ == "__main__":
    main()
