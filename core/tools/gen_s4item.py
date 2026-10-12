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
    # a class the hand-written fragment renames to a facade keeps its fields there (classRenames)
    renamed = set()
    for f in sorted(os.listdir(os.path.dirname(out_frag))):
        if f.endswith(".json") and f != os.path.basename(out_frag):
            renamed |= set(json.load(open(os.path.join(os.path.dirname(out_frag), f), encoding="utf-8")).get("classRenames", {}))
    for usage, owner, name, desc, kinds, w in rows:
        if usage != "get-static" or "FIELD" not in kinds.split(","):
            continue
        key = (owner, name, desc)
        if key in getters:
            continue
        if owner in renamed:
            report.append((usage, owner, name, desc, "facade", "the class is renamed to a facade that keeps the field"))
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
        body.append(f"    public static {typ} {name}() {{")
        if rule == "codec":
            # one Codec per map codec, shared with every other place a 1.20.4 codec is answered
            body.append(f"        return {PKG.replace('/', '.')}.CbCodecs.fromMapCodec({code});")
        else:
            body.append(f"        {code}")
        body.append("    }")
        body.append("")
    java = [
        "package " + PKG.replace("/", ".") + ";",
        "",
        "/**",
        " * Static fields of 1.20.4 that 1.20.5 holds in another form, read back in 1.20.4's (generated by",
        " * core/tools/gen_s4item.py from the step's damage table; do not edit). A mod's GETSTATIC of the",
        " * old field becomes a call of the getter of the same name (fieldGetters). Names in the comments",
        " * are 1.21.11's where the member lived that long.",
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
    methods_stage(rows, vjar, wjar, os.path.dirname(out_frag), frag, report)
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


# ---------------------------------------------------------------------------------------- methods
#
# A call, constructor or override whose 1.20.5 shape differs from 1.20.4's only by values CbItemHolders,
# CbCodecs and CbRegistries convert (a registered value and its holder, a location and its
# key, a codec and its map codec, an equipment slot and its group, a byte buffer and its
# registry-aware view, a RegistryAccess and the lookup provider that is one), position for position:
#   calls (call, call-static, protected calls; a mod's super call too) -> callReshapes: each old
#     argument converted to the new type ("conv:i") or passed where the new shape takes a supertype
#     ("keep:i T"), the new return value converted back ("ret conv") or passed when it is a subtype
#   constructors (new, super(..))                                      -> superCtorReshapes, the same
#   overrides (a mod method vanilla called in 1.20.4)                   -> overrideHooks: the new shape
#     vanilla calls hands the old method its arguments converted back ("wrap": one converter, so one
#     changed parameter type), the old method's answer converted forward ("unwrap"); the old method's
#     own super call is served by the call reshape of the same key.
# Holder and key conversions are emitted only when the new shape's generic signature names the old
# type as the holder's / key's argument. Members the hand-written fragment (s4item.json) or another
# area wires are skipped.

RT = "top/dext/centurybridge/step/v1_20_5/rt/"
HOLDERS = RT + "CbItemHolders"
SHARED = RT + "CbHolders"  # the step's shared registry value <-> holder pairs
CODECS = RT + "CbCodecs"
REGS = HOLDERS
POTION, EFFECT, ATTRIBUTE, SOUND = "net/minecraft/class_1842", "net/minecraft/class_1291", "net/minecraft/class_1320", "net/minecraft/class_3414"
LOOT_TABLE, LOOT_FUNCTION, LOOT_CONDITION = "net/minecraft/class_52", "net/minecraft/class_117", "net/minecraft/class_5341"

# (old internal name, new internal name) -> (old->new converter or None for a plain subtype pass,
#                                            new->old converter or None for a plain subtype pass,
#                                            the holder / key argument the new generic type must name)
CONV = {
    (POTION, HOLDER): (f"{SHARED}#potion", f"{SHARED}#potionValue", POTION),
    (EFFECT, HOLDER): (f"{SHARED}#mobEffect", f"{SHARED}#mobEffectValue", EFFECT),
    (ATTRIBUTE, HOLDER): (f"{HOLDERS}#attributeHolder", f"{HOLDERS}#attribute", ATTRIBUTE),
    (SOUND, HOLDER): (f"{SHARED}#soundEvent", f"{SHARED}#soundEventValue", SOUND),
    (CODEC, MAP_CODEC): (f"{CODECS}#toMapCodec", f"{CODECS}#fromMapCodec", None),
    ("net/minecraft/class_1304", "net/minecraft/class_9274"): (f"{HOLDERS}#slotGroup", "", None),
    ("java/lang/Integer", "net/minecraft/class_9209"): (f"{HOLDERS}#mapId", "", None),
    ("com/mojang/serialization/Lifecycle", "net/minecraft/class_9248"): (f"{HOLDERS}#registrationInfo", f"{HOLDERS}#lifecycle", None),
    ("java/util/stream/Stream", "java/lang/Iterable"): (f"{HOLDERS}#iterable", "", None),
    ("net/minecraft/class_2540", "net/minecraft/class_9129"): (f"{REGS}#registryBuf", None, None),
    ("net/minecraft/class_5455", "net/minecraft/class_7225$class_7874"): (None, f"{REGS}#access", None),
}
# a location becomes a key of the registry its new generic type names
KEY_OF = {LOOT_TABLE: f"{HOLDERS}#lootTableKey", LOOT_FUNCTION: f"{HOLDERS}#itemModifierKey",
          LOOT_CONDITION: f"{HOLDERS}#predicateKey"}
CONV_DESCS = {  # converter -> its (param, return) descriptors
    f"{SHARED}#potion": (POTION, HOLDER), f"{SHARED}#potionValue": (HOLDER, POTION),
    f"{SHARED}#mobEffect": (EFFECT, HOLDER), f"{SHARED}#mobEffectValue": (HOLDER, EFFECT),
    f"{SHARED}#soundEvent": (SOUND, HOLDER), f"{SHARED}#soundEventValue": (HOLDER, SOUND),
    f"{HOLDERS}#attributeHolder": (ATTRIBUTE, HOLDER), f"{HOLDERS}#attribute": (HOLDER, ATTRIBUTE),
    f"{CODECS}#toMapCodec": (CODEC, MAP_CODEC), f"{CODECS}#fromMapCodec": (MAP_CODEC, CODEC),
    f"{HOLDERS}#slotGroup": ("net/minecraft/class_1304", "net/minecraft/class_9274"),
    f"{HOLDERS}#mapId": ("java/lang/Integer", "net/minecraft/class_9209"),
    f"{HOLDERS}#registrationInfo": ("com/mojang/serialization/Lifecycle", "net/minecraft/class_9248"),
    f"{HOLDERS}#lifecycle": ("net/minecraft/class_9248", "com/mojang/serialization/Lifecycle"),
    f"{HOLDERS}#iterable": ("java/util/stream/Stream", "java/lang/Iterable"),
    f"{REGS}#registryBuf": ("net/minecraft/class_2540", "net/minecraft/class_9129"),
    f"{REGS}#access": ("net/minecraft/class_7225$class_7874", "net/minecraft/class_5455"),
    f"{HOLDERS}#lootTableKey": (RESOURCE_LOCATION, RESOURCE_KEY), f"{HOLDERS}#itemModifierKey": (RESOURCE_LOCATION, RESOURCE_KEY),
    f"{HOLDERS}#predicateKey": (RESOURCE_LOCATION, RESOURCE_KEY), f"{HOLDERS}#location": (RESOURCE_KEY, RESOURCE_LOCATION),
}


def conv_ref(conv):
    p, r = CONV_DESCS[conv]
    return f"{conv}(L{p};)L{r};"


def sig_args(sig):
    """generic parameter / return type signatures of a method signature, or None"""
    sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
    import classfile
    return classfile.split_signature(sig) if sig else None


def generic_arg(tsig):
    """'Lnet/minecraft/class_6880<Lnet/minecraft/class_1842;>;' -> 'net/minecraft/class_1842'"""
    m = re.match(r"L[^<;]+<L([^<;]+)[;<]", tsig or "")
    return m.group(1) if m else None


def convert(old, new, new_sig, hw, direction):
    """how a value of descriptor old reaches a parameter of descriptor new (direction 'in': a 1.20.4
    value handed to 1.20.5) or how a 1.20.5 value of descriptor new comes back as old ('out').
    Returns ('same', None) / ('keep', None) / ('conv', ref) / None when no rule applies."""
    if old == new:
        return ("same", None)
    if not (old.startswith("L") and new.startswith("L")):
        return None
    o, n = old[1:-1], new[1:-1]
    if direction == "in" and hw.assignable(old, new):
        return ("keep", None)
    if direction == "out" and hw.assignable(new, old):
        return ("keep", None)
    if (o, n) == (RESOURCE_LOCATION, RESOURCE_KEY):
        if direction == "out":
            return ("conv", conv_ref(f"{HOLDERS}#location"))
        target = generic_arg(new_sig)
        return ("conv", conv_ref(KEY_OF[target])) if target in KEY_OF else None
    rule = CONV.get((o, n))
    if rule is None:
        return None
    to_new, to_old, needs = rule
    if needs is not None and generic_arg(new_sig) != needs:
        return None
    c = to_new if direction == "in" else to_old
    if c is None:
        return ("keep", None)
    return ("conv", conv_ref(c)) if c else None


# rows whose types convert but whose 1.20.5 member does something else: bridged by hand (s4item.json)
HAND = {
    ("net/minecraft/class_1844", "method_8062", "(Lnet/minecraft/class_1842;)I"):
        "PotionUtils.getColor(Potion): 1.20.5 answers the colour with an alpha byte and has no empty potion",
}
# old shapes reconnected by adapter hooks instead (a whole interface changed): an override hook would
# synthesize the same new shape a second time
NO_HOOK = {
    "method_53736()Lcom/mojang/serialization/Codec;": "RecipeSerializer.codec(): adapterHooks with streamCodec()",
    "method_29138()Lcom/mojang/serialization/Codec;": "ParticleType.codec(): adapterHooks with streamCodec()",
}


def methods_stage(rows, vjar, wjar, frag_dir, frag, report):
    sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
    import gen_step_reshapes as gsr
    from classfile import ACC_FINAL, ACC_PRIVATE, ACC_STATIC, split_args
    V = gsr.load(vjar, False)
    W = gsr.load(wjar, False)
    hv, hw = gsr.Hierarchy(V), gsr.Hierarchy(W)
    # what other fragments wire (hand-written areas, mechanical): never wired twice
    wired = set()
    for f in sorted(os.listdir(frag_dir)):
        if f.endswith(".json") and f != "s4item-gen.json":
            d = json.load(open(os.path.join(frag_dir, f), encoding="utf-8"))
            for sec in ("callReshapes", "superCtorReshapes", "staticRedirects", "instanceRedirects",
                        "superRedirects", "ctorRedirects"):
                wired |= set(d.get(sec, {}))
            for h in d.get("overrideHooks", []):
                wired.add("hook " + h["old"])
            wired |= {"class " + c for c in d.get("classRenames", {})}
    calls, ctors, hooks = {}, {}, []

    def wmethod(owner, name, desc):
        for c in hw.supers(owner):
            k = W.get(c)
            if k:
                m = next((m for m in k.methods if m.name == name and m.desc == desc), None)
                if m:
                    return c, m
        return None, None

    def reshape(old_desc, new_desc, new_sig):
        oa, orr = split_args(old_desc)
        na, nr = split_args(new_desc)
        if len(oa) != len(na):
            return None, "arity"
        sigs = sig_args(new_sig)
        ops = []
        changed = False
        for i, (o, n) in enumerate(zip(oa, na)):
            c = convert(o, n, sigs[0][i] if sigs else None, hw, "in")
            if c is None:
                return None, f"argument {i}: {o} -> {n}"
            if c[0] == "same":
                ops.append(f"keep:{i}")
            elif c[0] == "keep":
                ops.append(f"keep:{i} {n}")
                changed = True
            else:
                ops.append(f"conv:{i} {c[1]}")
                changed = True
        ret = None
        if orr != nr:
            if orr == "V":
                ret = "pop"
            else:
                c = convert(orr, nr, sigs[1] if sigs else None, hw, "out")
                if c is None:
                    return None, f"return {nr} -> {orr}"
                ret = "keep" if c[0] == "keep" else f"conv {c[1]}"
            changed = True
        if not changed:
            return None, "no conversion"
        return (ops, ret), None

    for usage, owner, name, desc, kinds, w in rows:
        if usage.startswith(("get", "put")) or usage in ("extend", "implement-interface", "lambda") or not name:
            continue
        if "class " + owner in wired:
            continue
        key = f"{owner}.{name}{desc}"
        if name == "<init>":
            if not w.startswith("constructors "):
                continue
            cands = re.findall(r"\(([^)]*)\)V", w)
            best = None
            for c in cands:
                nd = "(" + c + ")V"
                k = W.get(owner)
                m = next((m for m in k.methods if m.name == "<init>" and m.desc == nd), None) if k else None
                res, why = reshape(desc, nd, m.signature if m else None)
                if res:
                    best = (nd, res)
                    break
            if not best or key in wired or key in ctors:
                continue
            nd, (ops, _) = best
            ctors[key] = {"desc": nd, "ops": ops, "basis": "s4item: converted arguments (gen_s4item.py)"}
            report.append((usage, owner, name, desc, "ctor-reshape", nd))
            continue
        mm = re.match(r"method (\S+?)(\([^ ]*) (.*)", w)
        if not mm or mm.group(1) != name:
            continue
        nd = mm.group(2)
        wo, wm = wmethod(owner, name, nd)
        if wm is None or wm.access & ACC_PRIVATE:
            continue
        if (owner, name, desc) in HAND:
            report.append((usage, owner, name, desc, "design", HAND[(owner, name, desc)]))
            continue
        # a call keeps its opcode: a static call cannot reach an instance method, nor the reverse
        if usage.startswith("call") and bool(wm.access & ACC_STATIC) != usage.startswith("call-static"):
            report.append((usage, owner, name, desc, "design", "static <-> instance"))
            continue
        res, why = reshape(desc, nd, wm.signature)
        if not res:
            report.append((usage, owner, name, desc, "design", "methods: " + why))
            continue
        ops, ret = res
        if key not in wired and key not in calls:
            entry = {"desc": nd, "ops": ops}
            if ret:
                entry["ret"] = ret
            entry["basis"] = "s4item: converted arguments / return (gen_s4item.py)"
            calls[key] = entry
            report.append((usage, owner, name, desc, "call-reshape", nd))
        if usage in ("override+super", "implement", "implement-inherited") and not wm.access & (ACC_FINAL | ACC_STATIC):
            if "hook " + name + desc in wired or name + desc in NO_HOOK:
                continue
            oa, orr = split_args(desc)
            na, nr = split_args(nd)
            sigs = sig_args(wm.signature)
            wraps = set()
            ok = True
            for i, (o, n) in enumerate(zip(oa, na)):
                if o == n:
                    continue
                c = convert(o, n, sigs[0][i] if sigs else None, hw, "out")
                if c is None:
                    ok = False
                elif c[0] == "conv":
                    wraps.add(c[1])
            unwrap = None
            if orr != nr:
                c = convert(orr, nr, sigs[1] if sigs else None, hw, "in")
                if c is None:
                    ok = False
                elif c[0] == "conv":
                    unwrap = c[1]
            if not ok or len(wraps) > 1:
                report.append((usage, owner, name, desc, "design", "hook: arguments need more than one converter"))
                continue
            h = {"old": name + desc, "new": name + nd, "onlyUnder": owner}
            if wraps:
                r = wraps.pop()
                h["wrap"] = r.replace("#", ".", 1)
            if unwrap:
                h["unwrap"] = unwrap.replace("#", ".", 1)
            h["basis"] = "s4item: old override reconnected through converted values (gen_s4item.py)"
            hooks.append(h)
            report.append((usage, owner, name, desc, "override-hook", nd))
    # one hook per shape change, on the topmost owner (a mod class under two owners of one group
    # would otherwise get the new shape twice)
    groups = {}
    for h in hooks:
        groups.setdefault((h["old"], h["new"]), []).append(h)
    hooks = [h for g in groups.values() for h in g
             if not any(o is not h and o["onlyUnder"] != h["onlyUnder"] and o["onlyUnder"] in hw.supers(h["onlyUnder"]) for o in g)]
    seen = set()
    uniq = []
    for h in hooks:
        k = (h["old"], h["new"], h["onlyUnder"])
        if k not in seen:
            seen.add(k)
            uniq.append(h)
    if calls:
        frag["callReshapes"] = dict(sorted(calls.items()))
    if ctors:
        frag["superCtorReshapes"] = dict(sorted(ctors.items()))
    if uniq:
        frag["overrideHooks"] = uniq


main()
