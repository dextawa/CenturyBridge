"""Private / package fields whose type 1.20.5 changed while keeping the value's meaning (step 1.20.4 -> 1.20.5, s205):
instanceFieldGetters / fieldGetters (and setters where mods' mixins write a field W keeps writable) that read 1.20.5's field and
answer 1.20.4's type, so a mod's @Shadow / @Accessor of the field (MixinBridge carries them through the getter) reads what
1.20.4 kept.

  python3 core/tools/gen_s205_fields.py <cause-mixin.tsv>      (from the repository root; reads $CB_DATA (default data)/jars/*-code.jar)
writes core/segments/steps/1.20.4__1.20.5.json.d/s205-fields-gen.json, the layer class
core/shims/steps/1.20.5/src/top/dext/centurybridge/step/v1_20_5/rt/CbFieldReshapesGen.java and the generated block of the
layer's access widener (the getters read the private fields; a class W made package-private is opened for the getter's
parameter). Regenerate, never hand-edit.

Fields taken: the rows of cause "target-private-reshape" naming a field, whose V and W types are one of CONVERSIONS (each
checked against both releases' bytecode: the same field, the value stored in place of V's):
  ResourceLocation -> ResourceKey<T>     the key's location; written: the key of that location in T's registry (Registries,
                                         the static field whose signature is ResourceKey<Registry<T>>)
  MobEffect / SoundEvent / Attribute / Item / GameEvent -> Holder<..>   the holder's value; written: the step's holder of the value
                                         (CbHolders / CbItemHolders, Item: its built-in registry holder)
  Codec -> MapCodec                      1.20.4 stored the map codec's codec() (V's store is preceded by MapCodec.codec(), or
                                         takes a Codec parameter built as RecordCodecBuilder.create = mapCodec(..).codec(), or
                                         is RecordCodecBuilder.create(f) = mapCodec(f).codec());
                                         read only
  List<SuspiciousEffectHolder.Entry> -> SuspiciousStewEffects   its entries (CbBlockSuspicious); written: the stew of them
  double -> float                        widened on read; written: narrowed (1.20.5 keeps a float)
Anything else is listed as skipped with its types.
"""
import csv
import json
import os
import re
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import bytecode  # noqa: E402

csv.field_size_limit(10 ** 9)
N = "net/minecraft/"
DATA = os.environ.get("CB_DATA", "data")
VJ = zipfile.ZipFile(DATA + "/jars/client-1.20.4-intermediary-code.jar")
WJ = zipfile.ZipFile(DATA + "/jars/client-1.20.5-intermediary-code.jar")
OUT_JSON = "core/segments/steps/1.20.4__1.20.5.json.d/s205-fields-gen.json"
OUT_JAVA = "core/shims/steps/1.20.5/src/top/dext/centurybridge/step/v1_20_5/rt/CbFieldReshapesGen{}.java"
AW = "core/shims/steps/1.20.5/resources/cb-step-1.20.5.accesswidener"
RT = "top/dext/centurybridge/step/v1_20_5/rt/CbFieldReshapesGen"
MOJ = {}
for _l in open(DATA + "/mappings/inter-to-mojang-classes.tsv", encoding="utf-8"):
    _p = _l.rstrip("\n").split("\t")
    if len(_p) >= 3 and _p[0] == "C":
        MOJ[_p[1]] = _p[2]


def client_only(owner):
    """a class of the client (its Mojang name, as of the last release that had it): getters of its fields go to the
    @Environment(CLIENT) class, which a dedicated server never loads"""
    m = MOJ.get(owner) or MOJ.get(owner.split("$")[0], "")
    return m.startswith(("net/minecraft/client/", "com/mojang/blaze3d/", "com/mojang/realmsclient/"))

_cache = {}


def klass(jar, name):
    k = (id(jar), name)
    if k not in _cache:
        try:
            _cache[k] = bytecode.read(jar.read(name + ".class"))
        except KeyError:
            _cache[k] = None
    return _cache[k]


def field(jar, owner, name):
    k = klass(jar, owner)
    return None if k is None else next((f for f in k.fields if f.name == name), None)


def jtype(desc):
    """Java source type of a descriptor (raw)"""
    dims = 0
    while desc.startswith("["):
        dims, desc = dims + 1, desc[1:]
    prim = {"Z": "boolean", "B": "byte", "C": "char", "S": "short", "I": "int", "J": "long", "F": "float", "D": "double"}
    t = prim.get(desc) or desc[1:-1].replace("/", ".").replace("$", ".")
    return t + "[]" * dims


def holder_arg(sig):
    m = re.match(r"Lnet/minecraft/class_6880<L([^;<]+);>;", sig or "")
    return m.group(1) if m else None


REGISTRY_KEYS = {}


def registry_key(elem):
    """Registries' static ResourceKey<Registry<elem>> field (W)"""
    if not REGISTRY_KEYS:
        for f in klass(WJ, N + "class_7924").fields:
            m = re.match(r"Lnet/minecraft/class_5321<Lnet/minecraft/class_2378<L([^;<]+);>;>;", f.signature or "")
            if m:
                REGISTRY_KEYS.setdefault(m.group(1), []).append(f.name)
    keys = REGISTRY_KEYS.get(elem, [])
    return keys[0] if len(keys) == 1 else None


HOLDERS = {
    N + "class_1291": ("top.dext.centurybridge.step.v1_20_5.rt.CbHolders.mobEffect(v)", "MobEffect"),
    N + "class_3414": ("top.dext.centurybridge.step.v1_20_5.rt.CbHolders.soundEvent(v)", "SoundEvent"),
    N + "class_1320": ("top.dext.centurybridge.step.v1_20_5.rt.CbItemHolders.attributeHolder(v)", "Attribute"),
    N + "class_1792": ("(v == null ? null : v.method_40131())", "Item (built-in registry holder)"),
    N + "class_5712": ("top.dext.centurybridge.step.v1_20_5.rt.CbHolders.gameEvent(v)", "GameEvent"),
}


def v_stores_codec_of_mapcodec(owner, name):
    k = klass(VJ, owner)
    for m in k.methods if k else ():
        ins = m.insns or []
        for i, (op, x) in enumerate(ins):
            if op in (0xb5, 0xb3) and isinstance(x, tuple) and x[0] == "ref" and x[2] == owner and x[3] == name:
                prev = ins[i - 1][1] if i else None
                if isinstance(prev, tuple) and prev[0] == "ref" and prev[3] == "codec" and prev[2] == "com/mojang/serialization/MapCodec":
                    return "V stores MapCodec.codec() of the map codec W stores (javap -c, the same chain)"
                if isinstance(prev, tuple) and prev[0] == "ref" and prev[2] == "com/mojang/serialization/codecs/RecordCodecBuilder" and prev[3] == "create":
                    return "V stores RecordCodecBuilder.create(f), which is RecordCodecBuilder.mapCodec(f).codec() of the map codec W stores"
                if ins[i - 1][0] in (0x2b, 0x19):  # aload_1 / aload: a constructor parameter
                    return "V stores its Codec parameter, which 1.20.4's callers built as RecordCodecBuilder.create(..) = mapCodec(..).codec() of the map codec 1.20.5's callers pass"
    return None


def conversion(owner, name, vdesc, wdesc, wsig):
    """(getter expr over w, setter expr over v or None, basis) or None"""
    if vdesc == "L" + N + "class_2960;" and wdesc == "L" + N + "class_5321;":
        m = re.match(r"Lnet/minecraft/class_5321<L([^;<]+);>;", wsig or "")
        rk = registry_key(m.group(1)) if m else None
        if rk is None:
            return None
        return ("w == null ? null : w.method_29177()",
                f"v == null ? null : net.minecraft.class_5321.method_29179(net.minecraft.class_7924.{rk}, v)",
                f"ResourceLocation -> ResourceKey of {m.group(1).split('/')[-1]} (Registries.{rk}): the key's location")
    if wdesc == "L" + N + "class_6880;" and vdesc[1:-1] in HOLDERS and holder_arg(wsig) == vdesc[1:-1]:
        to, what = HOLDERS[vdesc[1:-1]]
        return ("w == null ? null : (" + jtype(vdesc) + ") w.comp_349()", to, f"{what} -> Holder<{what.split()[0]}>: the holder's value")
    if vdesc == "Lcom/mojang/serialization/Codec;" and wdesc == "Lcom/mojang/serialization/MapCodec;":
        why = v_stores_codec_of_mapcodec(owner, name)
        return ("w == null ? null : w.codec()", None, "Codec -> MapCodec: " + why) if why else None
    if vdesc == "Ljava/util/List;" and wdesc == "L" + N + "class_9298;":
        return ("top.dext.centurybridge.step.v1_20_5.rt.CbBlockSuspicious.entries(w)",
                "top.dext.centurybridge.step.v1_20_5.rt.CbBlockSuspicious.stew(v)",
                "List<SuspiciousEffectHolder.Entry> -> SuspiciousStewEffects: its entries")
    if vdesc == "D" and wdesc == "F":
        return ("(double) w", "(float) v", "double -> float: widened on read, narrowed on write")
    return None


def main():
    rows = [r for r in csv.DictReader(open(sys.argv[1], encoding="utf-8"), delimiter="\t")
            if r["cause"].startswith("target-private-reshape") and "(" not in r["target"]]
    want = {}
    for r in rows:
        name, vdesc = r["target"].split(":", 1)
        k = (r["owner"], name, vdesc)
        want.setdefault(k, set()).add(r["usage"])
    getters, sgetters, setters, ssetters, aw, skipped = {}, {}, {}, {}, set(), []
    javas = {"": [], "Client": []}
    for (owner, name, vdesc), usages in sorted(want.items()):
        wf = field(WJ, owner, name)
        vf = field(VJ, owner, name)
        if wf is None or vf is None:
            skipped.append(f"{owner}.{name}: not declared on both")
            continue
        conv = conversion(owner, name, vdesc, wf.desc, wf.signature)
        if conv is None:
            skipped.append(f"{owner}.{name}: {vdesc} -> {wf.desc} {wf.signature or ''}")
            continue
        get, put, basis = conv
        static = bool(wf.access & bytecode.ACC_STATIC)
        writes = any(u in ("accessor-set", "shadow-field-put") for u in usages)
        final = bool(wf.access & bytecode.ACC_FINAL)
        owner_t = jtype("L" + owner + ";")
        mname = name if name.startswith("field_") else name + "_" + owner.split("/")[-1].replace("$", "_")
        key = f"{owner}.{name}:{vdesc}"
        recv = owner_t if static else "o"
        side = "Client" if client_only(owner) else ""
        java = javas[side]
        rt = RT + side
        java.append(f"    /** {owner.split('/')[-1]}.{name}: {basis} */")
        java.append(f"    public static {jtype(vdesc)} {mname}({'' if static else owner_t + ' o'}) {{")
        java.append(f"        {jtype(wf.desc)} w = {recv}.{name};")
        java.append(f"        return {get};")
        java.append("    }")
        (sgetters if static else getters)[key] = f"{rt}#{mname}"
        if not wf.access & bytecode.ACC_PUBLIC:
            aw.add(f"accessible field {owner} {name} {wf.desc}")
        wk = klass(WJ, owner)
        if not wk.access & bytecode.ACC_PUBLIC:
            aw.add(f"accessible class {owner}")
        if writes and put and not final:
            java.append(f"    /** writing {owner.split('/')[-1]}.{name} */")
            java.append(f"    public static void {mname}({'' if static else owner_t + ' o, '}{jtype(vdesc)} v) {{")
            java.append(f"        {recv}.{name} = {put};")
            java.append("    }")
            (ssetters if static else setters)[key] = f"{rt}#{mname}"
        elif writes:
            skipped.append(f"{owner}.{name}: written by mods' mixins, " + ("final in 1.20.5" if put else "no conversion back") + " (read given back only)")
    root = {"_s205_fields_basis": "gen_s205_fields.py (V/W bytecode); regenerate, never hand-edit"}
    for sec, d in (("instanceFieldGetters", getters), ("fieldGetters", sgetters), ("instanceFieldSetters", setters), ("staticFieldSetters", ssetters)):
        if d:
            root[sec] = dict(sorted(d.items()))
    with open(OUT_JSON, "w", encoding="utf-8", newline="\n") as o:
        json.dump(root, o, indent=1)
        o.write("\n")
    for side, java in javas.items():
        with open(OUT_JAVA.format(side), "w", encoding="utf-8", newline="\n") as o:
            o.write("package top.dext.centurybridge.step.v1_20_5.rt;\n\n")
            o.write("/** Generated by core/tools/gen_s205_fields.py: 1.20.4's view of private fields 1.20.5 retyped"
                    + (" (client classes)" if side else "") + ". Never hand-edit. */\n")
            if side:
                o.write("@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)\n")
            o.write("@SuppressWarnings({\"rawtypes\", \"unchecked\"})\npublic final class CbFieldReshapesGen" + side + " {\n\n")
            o.write("\n".join(java) + "\n\n    private CbFieldReshapesGen" + side + "() {\n    }\n}\n")
    begin, end = "# ---- BEGIN GENERATED (s205 fields): core/tools/gen_s205_fields.py ----", "# ---- END GENERATED (s205 fields) ----"
    text = open(AW, encoding="utf-8").read()
    block = begin + "\n" + "\n".join(sorted(aw)) + "\n" + end + "\n"
    if begin in text:
        text = text[:text.index(begin)] + block + text[text.index(end) + len(end) + 1:]
    else:
        text = text.rstrip("\n") + "\n" + block
    with open(AW, "w", encoding="utf-8", newline="\n") as o:
        o.write(text)
    print(f"{len(getters) + len(sgetters)} getters, {len(setters) + len(ssetters)} setters, {len(aw)} AW lines; skipped {len(skipped)}:")
    for s in skipped:
        print("  " + s)


if __name__ == "__main__":
    main()
