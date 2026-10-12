"""Generated bridges for climb step 1.20.4 -> 1.20.5, area s4item (owners under world/item,
world/effect, core, advancements, world/level/storage, data, world/food).

Rows of the step's damage table that share one change get one rule here instead of one hand-written
bridge each. Every rule is checked against both releases' class files before it emits anything:

  static fields (get-static)
    holder     1.20.5 holds the registry holder where 1.20.4 held the value (MobEffects, Potions):
               the getter answers holder.value() -- the registered object 1.20.4 handed out.
    location   1.20.5 holds a ResourceKey where 1.20.4 held the ResourceLocation naming it
               (BuiltInLootTables): the getter answers key.location(), the same location.
    codec      1.20.5 holds the MapCodec where 1.20.4 held the record Codec built from it
               (RecordCodecBuilder.create is mapCodec(..).codec()): the getter answers
               mapCodec.codec(), made once. Emitted only when 1.20.4's initializer built the field
               with RecordCodecBuilder.create or MapCodec.codec(), i.e. the old value was a map
               codec's codec too.
    constant   the field is gone and 1.20.4 declared it a compile-time constant: the getter answers
               1.20.4's ConstantValue (TAG_* key names, size limits).

A row no rule covers is reported as "design" and left to hand-written wiring. Output: the getter
class (rt/CbItemFields.java), a wiring fragment (fieldGetters), a report of every row.

  python core/tools/gen_s4item.py <damage.tsv> <V code jar> <W stub jar> <inter-to-mojang-classes.tsv>
         <inter-to-mojang-1.21.11.tsv> <layer src root> <out fragment.json> <out report.tsv>
"""
import json
import os
import re
import subprocess
import sys
import zipfile

AREA = re.compile(r"^net/minecraft/(world/item/|world/effect/|core/|advancements/|world/level/storage/|data/|world/food/)")
PKG = "top/dext/centurybridge/step/v1_20_5/rt"
FIELDS_CLASS = PKG + "/CbItemFields"

RESOURCE_LOCATION = "net/minecraft/class_2960"
RESOURCE_KEY = "net/minecraft/class_5321"
HOLDER = "net/minecraft/class_6880"
CODEC = "com/mojang/serialization/Codec"
MAP_CODEC = "com/mojang/serialization/MapCodec"


def class_names(path):
    cm = {}
    for line in open(path, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "C":
            cm[f[1]] = f[2]
    return cm


def member_names(path):
    """intermediary owner.name -> mojang simple name (1.21.11 table: members that lived that long)"""
    mm = {}
    for line in open(path, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] in ("M", "F"):
            mm[f[1]] = f[2].rsplit(".", 1)[-1]
    return mm


def mojang(c, cm):
    if c in cm:
        return cm[c]
    d = c.rfind("$")
    return mojang(c[:d], cm) + c[d:] if d > 0 else c


_javap = {}


def javap_fields(jar, cls):
    """name -> (source type string, static, final, constant literal or None) for one class"""
    key = (jar, cls)
    if key in _javap:
        return _javap[key]
    out = subprocess.run(["javap", "-constants", "-p", "-cp", jar, cls.replace("/", ".")],
                         capture_output=True, text=True, encoding="utf-8", errors="replace").stdout
    fields = {}
    for line in out.splitlines():
        s = line.strip()
        if not s.endswith(";") or "(" in s:
            continue
        s = s[:-1]
        const = None
        if " = " in s:
            s, const = s.split(" = ", 1)
        parts = s.split(" ")
        name = parts[-1]
        mods = set(parts[:-1])
        # the type is everything between the modifiers and the name (generic types hold no spaces
        # in javap's output except after commas)
        typ = " ".join(p for p in parts[:-1] if p not in
                       ("public", "private", "protected", "static", "final", "volatile", "transient"))
        fields[name] = (typ, "static" in mods, "final" in mods, const)
    _javap[key] = fields
    return fields


def source_type(desc):
    """a field descriptor as a Java source type (raw)"""
    prim = {"Z": "boolean", "B": "byte", "C": "char", "S": "short", "I": "int", "J": "long", "F": "float",
            "D": "double"}
    dims = 0
    while desc.startswith("["):
        dims += 1
        desc = desc[1:]
    base = prim[desc] if desc in prim else desc[1:-1].replace("/", ".")
    return base + "[]" * dims


def raw_and_args(typ):
    """'a.B<c.D, e.F<g.H>>' -> ('a/B', ['c/D', 'e/F'])"""
    if "<" not in typ:
        return typ.replace(".", "/"), []
    raw = typ[:typ.index("<")]
    inner = typ[typ.index("<") + 1:typ.rindex(">")]
    args, depth, cur = [], 0, ""
    for ch in inner:
        if ch == "<":
            depth += 1
        elif ch == ">":
            depth -= 1
        if ch == "," and depth == 0:
            args.append(cur.strip())
            cur = ""
        else:
            cur += ch
    if cur.strip():
        args.append(cur.strip())
    return raw.replace(".", "/"), [a.split("<")[0].replace(".", "/").replace("? extends ", "") for a in args]


def disassemble(jar, owner):
    key = ("c", jar, owner)
    if key not in _javap:
        _javap[key] = subprocess.run(["javap", "-c", "-p", "-cp", jar, owner.replace("/", ".")],
                                     capture_output=True, text=True, encoding="utf-8", errors="replace").stdout.splitlines()
    return _javap[key]


# calls whose result is a map codec's codec when their codec argument is: DFU's Codec.unit is
# MapCodec.unit(..).codec(); 1.20.4's ExtraCodecs.validate(Codec, fn) unwraps a MapCodecCodec,
# validates the map codec and answers its codec() again
MAP_CODEC_MAKERS = ("com/mojang/serialization/codecs/RecordCodecBuilder.create:",
                    "com/mojang/serialization/MapCodec.codec:", "com/mojang/serialization/Codec.unit:")
MAP_CODEC_KEEPERS = ("net/minecraft/class_5699.method_48112:",)


def _invokes_before(lines, i):
    """the invoke instructions before line i, nearest first (invokedynamic -- a lambda -- skipped)"""
    for j in range(i - 1, max(i - 40, -1), -1):
        if "invoke" in lines[j] and "invokedynamic" not in lines[j]:
            yield lines[j]
        if lines[j].strip().endswith(":") or "areturn" in lines[j] or "putstatic" in lines[j]:
            return


def _is_map_codec_codec(jar, owner, lines, i, depth=0):
    for t in _invokes_before(lines, i):
        if any(m in t for m in MAP_CODEC_MAKERS):
            return True
        if any(k in t for k in MAP_CODEC_KEEPERS):
            continue  # judged by the codec it was handed, the invoke before it
        # a static helper of the game (CompositeEntryBase.createCodec, CompositeLootItemCondition.createCodec)
        m = re.search(r"Method (?:(net/minecraft/[\w$]+)\.)?(method_\d+):(\([^)]*\)Lcom/mojang/serialization/Codec;)", t)
        if m and "invokestatic" in t and depth < 2:
            # an inherited static is named through the calling class: walk up to its declaration
            hown = m.group(1) or owner
            while hown:
                body = disassemble(jar, hown)
                start = next((k for k, ln in enumerate(body) if f" {m.group(2)}(" in ln and ln.strip().endswith(";")), None)
                if start is not None:
                    break
                sup = next((re.search(r" extends ([\w.$]+)", ln) for ln in body if " class " in ln and "{" in ln), None)
                hown = sup.group(1).replace(".", "/") if sup and sup.group(1).startswith("net.minecraft.") else None
            if not hown:
                return False
            for k in range(start, len(body)):
                if "areturn" in body[k]:
                    return _is_map_codec_codec(jar, hown, body, k, depth + 1)
            return False
        return False
    return False


def clinit_builds_map_codec(jar, owner, field):
    """whether 1.20.4's static initializer stores into owner.field a map codec's codec: the value
    made by RecordCodecBuilder.create / MapCodec.codec() / Codec.unit, possibly through
    ExtraCodecs.validate or a static helper of the game that returns one"""
    lines = disassemble(jar, owner)
    for i, line in enumerate(lines):
        # javap names the class's own fields without the owner
        if "putstatic" in line and (f"// Field {field}:" in line or f"// Field {owner}.{field}:" in line):
            return _is_map_codec_codec(jar, owner, lines, i)
    return False


def java_literal(typ, const):
    if typ == "java.lang.String":
        return json.dumps(const.strip('"').encode("utf-8").decode("unicode_escape"))
    if typ == "float":
        return const if const.endswith("f") else const + "f"
    if typ == "long":
        return const if const.endswith("l") or const.endswith("L") else const + "L"
    if typ == "char":
        return "(char) " + str(ord(const)) if len(const) == 1 else const
    return const


def main():
    damage, vjar, wjar, cnames, mnames, src_root, out_frag, out_report = sys.argv[1:9]
    cm = class_names(cnames)
    mm = member_names(mnames)
    with zipfile.ZipFile(wjar) as z:
        wclasses = {n[:-6] for n in z.namelist() if n.endswith(".class")}
    rows = []
    for i, line in enumerate(open(damage, encoding="utf-8")):
        if i == 0:
            continue
        r = line.rstrip("\n").split("\t")
        if AREA.search(mojang(r[1], cm)):
            rows.append(r)

    getters = {}   # (owner, name, desc) -> (rule, java body, comment)
    report = []
    for usage, owner, name, desc, kinds, w in rows:
        if usage != "get-static" or "FIELD" not in kinds.split(","):
            continue
        key = (owner, name, desc)
        if key in getters:
            continue
        wf = javap_fields(wjar, owner).get(name) if owner in wclasses else None
        vf = javap_fields(vjar, owner).get(name)
        what = f"{mojang(owner, cm).rsplit('/', 1)[-1]}.{mm.get(owner + '.' + name, name)}"
        old = desc[1:-1] if desc.startswith("L") else None
        rule = None
        if wf is not None and wf[1]:
            raw, targs = raw_and_args(wf[0])
            src = owner.replace("/", ".").replace("$", ".") + "." + name
            if old == RESOURCE_LOCATION and raw == RESOURCE_KEY:
                rule = ("location", f"return {src}.method_29177();",
                        f"{what}: 1.20.5 holds the ResourceKey, 1.20.4 the location naming it")
            elif old == CODEC and raw == MAP_CODEC:
                if clinit_builds_map_codec(vjar, owner, name):
                    rule = ("codec", src, f"{what}: 1.20.5 holds the MapCodec, 1.20.4 its record codec")
                else:
                    report.append((usage, owner, name, desc, "design", "1.20.4's codec was not a map codec's codec"))
                    continue
            elif raw == HOLDER and old is not None and targs == [old]:
                rule = ("holder", f"return {src}.comp_349();",
                        f"{what}: 1.20.5 holds the registry holder, 1.20.4 the registered value")
        elif wf is None and vf is not None and vf[1] and vf[2] and vf[3] is not None:
            rule = ("constant", f"return {java_literal(vf[0], vf[3])};",
                    f"{what}: gone in 1.20.5; 1.20.4's constant")
        if rule is None:
            report.append((usage, owner, name, desc, "design", "no rule: " + (wf[0] if wf else "gone in 1.20.5")))
            continue
        getters[key] = rule
        report.append((usage, owner, name, desc, rule[0], rule[2]))

    # ---- the getter class
    body = []
    for (owner, name, desc), (rule, code, comment) in sorted(getters.items(), key=lambda kv: (kv[0][0], kv[0][1])):
        typ = source_type(desc)
        body.append(f"    /** {comment} */")
        if rule == "codec":
            body.append(f"    private static com.mojang.serialization.Codec<?> codec_{name};")
            body.append("")
            body.append(f"    public static {typ} {name}() {{")
            body.append(f"        com.mojang.serialization.Codec<?> c = codec_{name};")
            body.append("        if (c == null) {")
            body.append(f"            codec_{name} = c = {code}.codec();")
            body.append("        }")
            body.append("        return c;")
            body.append("    }")
        else:
            body.append(f"    public static {typ} {name}() {{")
            body.append(f"        {code}")
            body.append("    }")
        body.append("")
    java = [
        "package " + PKG.replace("/", ".") + ";",
        "",
        "/**",
        " * Static fields of 1.20.4 that 1.20.5 holds in another form, read back in 1.20.4's (generated by",
        " * core/tools/gen_s4item.py from the step's damage table; do not edit). A mod's GETSTATIC of the",
        " * old field becomes a call of the getter of the same name (fieldGetters).",
        " */",
        "@SuppressWarnings({\"rawtypes\", \"unchecked\"})",
        "public final class CbItemFields {",
        "",
    ] + body + ["    private CbItemFields() {", "    }", "}", ""]
    path = os.path.join(src_root, FIELDS_CLASS + ".java")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(java))

    frag = {"fieldGetters": {f"{o}.{n}:{d}": FIELDS_CLASS for (o, n, d) in sorted(getters)}}
    with open(out_frag, "w", encoding="utf-8", newline="\n") as f:
        json.dump(frag, f, indent=1)
        f.write("\n")
    with open(out_report, "w", encoding="utf-8", newline="\n") as f:
        f.write("usage\towner\tname\tdesc\trule\tbasis\n")
        for r in report:
            f.write("\t".join(r) + "\n")
    by = {}
    for r in report:
        by[r[4]] = by.get(r[4], 0) + 1
    print("rows:", by, "->", path, out_frag)


main()
