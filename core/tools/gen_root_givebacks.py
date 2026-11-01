#!/usr/bin/env python3
"""gen_root_givebacks.py <V> <W> <shimDir> <wiringDir>: the root-registry names a step must give back (s121g, Kilt E9).

V's BuiltInRegistries (class_7923) registers each built-in registry in its <clinit> as
    getstatic class_7924.<key> ... invokestatic class_7923.<register>(...) ... putstatic class_7923.<field>
and the root registry REGISTRY (field_41167) holds it under the key's name. A field V has and W does not is a registry W
no longer builds in (1.21: enchantment, painting_variant became datapack registries), so W's root answers its name with
nothing. Where the step's wiring gives the field back (fieldGetters "class_7923.<field>:<desc>" -> a layer class with a
static <field>()<desc>), a converted mod's by-name root lookup of that name must answer the same object.

Writes <shimDir>/src/<package>/rt/CbRootRegistryGen.java (generated; regenerate, never hand-edit): the key of each such
registry and the getter answering it. A removed field the wiring does not give back is listed (stdout) and left out: there
is no object to answer with. Reads V's and W's code jars (data/jars/client-<ver>-intermediary-code.jar).
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import bytecode  # noqa: E402

DATA = os.environ.get("CB_DATA", "data")
BIR = "net/minecraft/class_7923"
KEYS = "net/minecraft/class_7924"
GETSTATIC, PUTSTATIC = 0xb2, 0xb3


def klass(ver, name):
    with zipfile.ZipFile(os.path.join(DATA, "jars", f"client-{ver}-intermediary-code.jar")) as z:
        return bytecode.read(z.read(name + ".class"))


def static_fields(k):
    return {(f.name, f.desc) for f in k.fields if f.access & bytecode.ACC_STATIC}


def registrations(k):
    """field -> the class_7924 key read last before its putstatic in <clinit>"""
    out = {}
    for m in k.methods:
        if m.name != "<clinit>" or not m.insns:
            continue
        key = None
        for op, arg in m.insns:
            # a field ref is ('ref', 'F', owner, name, desc)
            if not (isinstance(arg, tuple) and len(arg) == 5 and arg[0] == "ref" and arg[1] == "F"):
                continue
            owner, name, desc = arg[2], arg[3], arg[4]
            if op == GETSTATIC and owner == KEYS and desc == "Lnet/minecraft/class_5321;":
                key = name
            elif op == PUTSTATIC and owner == BIR and key is not None:
                out[name] = key
                key = None
    return out


def key_names(k):
    """class_7924 key field -> its registry name: the ldc string read last before method_47517 and the putstatic in <clinit>"""
    out = {}
    for m in k.methods:
        if m.name != "<clinit>" or not m.insns:
            continue
        s = None
        for op, arg in m.insns:
            if isinstance(arg, tuple) and len(arg) == 2 and arg[0] == "str":
                s = arg[1]
            elif op == PUTSTATIC and isinstance(arg, tuple) and len(arg) == 5 and arg[2] == KEYS and s is not None:
                out[arg[3]] = s
                s = None
    return out


def key_expr(key, w_keys, v_names):
    """the key's name as an Identifier: through W's key field when W still declares it, else by V's name (W renamed or
    dropped the key field; the name the root held in V is what a converted mod looks up)"""
    if key in w_keys:
        return f"net.minecraft.class_7924.{key}.method_29177()"
    # Identifier.withDefaultNamespace (method_60656) exists from 1.21 on; a W of 1.20.x would need new class_2960(name)
    return f'net.minecraft.class_2960.method_60656("{v_names[key]}")'


def field_getters(wdir):
    out = {}
    for fn in sorted(os.listdir(wdir)):
        if fn.endswith(".json"):
            d = json.load(open(os.path.join(wdir, fn), encoding="utf-8"))
            for k, v in d.get("fieldGetters", {}).items():
                out[k] = v
    return out


def main():
    v, w, shim, wdir = sys.argv[1:5]
    vk, wk = klass(v, BIR), klass(w, BIR)
    gone = sorted(static_fields(vk) - static_fields(wk))
    reg = registrations(vk)
    getters = field_getters(wdir)
    if w.startswith("1.20."):
        sys.exit(f"{w}: the generated key spelling uses Identifier.withDefaultNamespace (method_60656), which {w} lacks")
    w_keys = {n for n, _ in static_fields(klass(w, KEYS))}
    v_names = key_names(klass(v, KEYS))
    rows = []
    for name, desc in gone:
        if name not in reg:
            print(f"skip {name}{desc}: not a registration in {v}'s <clinit>")
            continue
        owner = getters.get(f"{BIR}.{name}:{desc}")
        if owner is None:
            print(f"OPEN {name} (key class_7924.{reg[name]}): {w} has no such registry and the wiring gives no getter")
            continue
        rows.append((reg[name], owner, name, desc))
        print(f"give back {name} (key class_7924.{reg[name]}) -> {owner}.{name}(){desc}")
    pkg = "top/dext/centurybridge/step/v" + w.replace(".", "_")
    path = os.path.join(shim, "src", pkg, "rt", "CbRootRegistryGen.java")
    lines = [
        f"package {pkg.replace('/', '.')}.rt;",
        "",
        "/**",
        f" * GENERATED by core/tools/gen_root_givebacks.py {v} {w} -- regenerate, never hand-edit. The built-in registries {v}'s",
        f" * BuiltInRegistries registered (and its root held by name) that {w} no longer builds, each with the getter the step's",
        " * fieldGetters give it back through: CbRootRegistry answers a converted mod's root lookup of the name with that object.",
        " */",
        "final class CbRootRegistryGen {",
        "",
        "    private CbRootRegistryGen() {",
        "    }",
        "",
        "    /** the registry the step gives back for a root name, or null (vanilla's answer stands) */",
        "    static Object lookup(net.minecraft.class_2960 id) {",
    ]
    for key, owner, name, desc in rows:
        lines.append(f"        if ({key_expr(key, w_keys, v_names)}.equals(id)) {{")
        lines.append(f"            return {owner.replace('/', '.')}.{name}(); // class_7923.{name}{desc}")
        lines.append("        }")
    lines += ["        return null;", "    }", "", "    /** the given-back root names */",
              "    static java.util.List<net.minecraft.class_2960> names() {",
              "        return java.util.List.of(" + ", ".join(key_expr(k, w_keys, v_names) for k, _, _, _ in rows) + ");",
              "    }", "}", ""]
    os.makedirs(os.path.dirname(path), exist_ok=True)
    open(path, "w", encoding="utf-8", newline="\n").write("\n".join(lines))
    print(f"-> {path} ({len(rows)} names)")


if __name__ == "__main__":
    main()
