"""Facades for the 1.20.1 loot serializer classes 1.20.2 deleted, one per class, generated.

1.20.1 kept a JsonSerializer next to every loot value class (SetCountLootFunction.Serializer,
RandomChanceLootCondition.Serializer, ItemEntry.Serializer, ConstantLootNumberProvider's two,
LootTable.Serializer...). 1.20.2 replaced them all with codecs and deleted the classes; a 1.20.1
mod may construct one, call it, subclass it and override it. Each facade keeps the 1.20.1 class:
the same superclass (the hand-written base facades class_120$class_123 / class_85$class_90 /
class_5335 / class_5330$class_5332, or Gson's interfaces), a public no-argument constructor, and
every public/protected method 1.20.1 declared -- its own toJson/fromJson under their intermediary
names and the bridge methods javac generated, with their exact erased descriptors and finality,
so a mod subclass's own javac bridges override them as they did. The bodies answer through
rt/v1_20_2/CbLootSerializers: read = the 1.20.1 JSON upgraded by the shared data-format rules and
decoded by the value's 1.20.2 codec, the fields the 1.20.1 base serializer handed in set as given;
write = the codec's fields copied into the caller's object beside what the base writes.

The superclass is named raw so the facade's method table is exactly 1.20.1's erasures; javac
would otherwise add bridges of its own choosing. Every member is public (protected in 1.20.1 or
not): see acc() below.

Hand-written, not generated: the abstract bases (class_120$class_123, class_79$class_5337,
class_85$class_90, class_186$class_188), the composite condition serializers (their create() is
the 1.20.1 hook), LootContext.EntityTarget.Serializer (a Gson TypeAdapter).

  python gen_loot_serializer_facades.py <1.20.1 code jar> <1.20.2 code jar> <inter-to-mojang-1.21.11.tsv>
         <facade source dir> <wiring fragment.json> [<owner regex>]

Only serializers whose owning class's Mojang name matches the regex (default: the loot and data
packages, ^net/minecraft/(world/level/storage|data)/) -- other dead serializers (AdvancementProgress's)
belong to other bridge areas.

Writes one <facade dir>/<class>.java per serializer and sets their classRenames in the fragment
(other sections of the fragment are left as they are).
"""
import json
import os
import re
import struct
import sys
import zipfile

ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL = 0x1, 0x2, 0x4, 0x8, 0x10
ACC_INTERFACE, ACC_ABSTRACT, ACC_SYNTHETIC = 0x200, 0x400, 0x1000

FACADE_PKG = "top.dext.centurybridge.facade.v1_20_2"
FACADE_INT = FACADE_PKG.replace(".", "/")
RT = "top.dext.centurybridge.rt.v1_20_2.CbLootSerializers"

GSON = "com/google/gson/"
JO, JE = "Lcom/google/gson/JsonObject;", "Lcom/google/gson/JsonElement;"
JSC, JDC = "Lcom/google/gson/JsonSerializationContext;", "Lcom/google/gson/JsonDeserializationContext;"
TYPE = "Ljava/lang/reflect/Type;"
CONDS, FUNCS = "[Lnet/minecraft/class_5341;", "[Lnet/minecraft/class_117;"

FN_BASE = "net/minecraft/class_120$class_123"
LEAF_BASE = "net/minecraft/class_85$class_90"
SERIALIZER = "net/minecraft/class_5335"
INLINE = "net/minecraft/class_5330$class_5332"

# the registries whose 1.20.1 static initializer names each serializer it registers
REGISTRIES = {
    "net/minecraft/class_217": "loot condition",
    "net/minecraft/class_131": "loot function",
    "net/minecraft/class_75": "loot pool entry",
    "net/minecraft/class_5659": "number provider",
    "net/minecraft/class_5652": "nbt provider",
    "net/minecraft/class_5671": "score provider",
}
CONDITION = "net/minecraft/class_5341"


class Member:
    def __init__(self, name, desc, acc, sig, code):
        self.name, self.desc, self.acc, self.sig, self.code = name, desc, acc, sig, code


class Cls:
    pass


def parse(b):
    n = struct.unpack(">H", b[8:10])[0]
    i, idx = 10, 1
    utf8, cls, strs = {}, {}, {}
    while idx < n:
        tag = b[i]
        if tag == 1:
            ln = struct.unpack(">H", b[i + 1:i + 3])[0]
            utf8[idx] = b[i + 3:i + 3 + ln].decode("utf-8", "replace")
            i += 3 + ln
        elif tag in (3, 4):
            i += 5
        elif tag in (5, 6):
            i += 9
            idx += 1
        elif tag == 7:
            cls[idx] = struct.unpack(">H", b[i + 1:i + 3])[0]
            i += 3
        elif tag == 8:
            strs[idx] = struct.unpack(">H", b[i + 1:i + 3])[0]
            i += 3
        elif tag in (16, 19, 20):
            i += 3
        elif tag in (9, 10, 11, 12, 17, 18):
            i += 5
        elif tag == 15:
            i += 4
        else:
            raise ValueError("constant pool tag %d" % tag)
        idx += 1
    c = Cls()
    c.utf8, c.cls_ref, c.str_ref = utf8, cls, strs
    c.access = struct.unpack(">H", b[i:i + 2])[0]
    c.name = utf8[cls[struct.unpack(">H", b[i + 2:i + 4])[0]]]
    sup = struct.unpack(">H", b[i + 4:i + 6])[0]
    c.super = utf8[cls[sup]] if sup else None
    i += 6
    nif = struct.unpack(">H", b[i:i + 2])[0]
    c.interfaces = [utf8[cls[struct.unpack(">H", b[i + 2 + 2 * k:i + 4 + 2 * k])[0]]] for k in range(nif)]
    i += 2 + 2 * nif

    def members(i):
        cnt = struct.unpack(">H", b[i:i + 2])[0]
        i += 2
        out = []
        for _ in range(cnt):
            a, ni, di, na = struct.unpack(">HHHH", b[i:i + 8])
            i += 8
            sig = code = None
            for _ in range(na):
                an = utf8[struct.unpack(">H", b[i:i + 2])[0]]
                ln = struct.unpack(">I", b[i + 2:i + 6])[0]
                if an == "Signature":
                    sig = utf8[struct.unpack(">H", b[i + 6:i + 8])[0]]
                elif an == "Code":
                    clen = struct.unpack(">I", b[i + 10:i + 14])[0]
                    code = b[i + 14:i + 14 + clen]
                i += 6 + ln
            out.append(Member(utf8[ni], utf8[di], a, sig, code))
        return i, out

    i, c.fields = members(i)
    i, c.methods = members(i)
    c.inner_flags = {}
    na = struct.unpack(">H", b[i:i + 2])[0]
    i += 2
    for _ in range(na):
        an = utf8[struct.unpack(">H", b[i:i + 2])[0]]
        ln = struct.unpack(">I", b[i + 2:i + 6])[0]
        if an == "InnerClasses":
            k = struct.unpack(">H", b[i + 6:i + 8])[0]
            for j in range(k):
                inner_i, _o, _n, flags = struct.unpack(">HHHH", b[i + 8 + 8 * j:i + 16 + 8 * j])
                c.inner_flags[utf8[cls[inner_i]]] = flags
        i += 6 + ln
    return c


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.startswith("net/minecraft/") and n.endswith(".class"):
                out[n[:-6]] = parse(z.read(n))
    return out


# opcode -> operand bytes (tableswitch / lookupswitch / wide handled apart)
OPLEN = {0x10: 1, 0x11: 2, 0x12: 1, 0x13: 2, 0x14: 2, 0x15: 1, 0x16: 1, 0x17: 1, 0x18: 1, 0x19: 1,
         0x36: 1, 0x37: 1, 0x38: 1, 0x39: 1, 0x3a: 1, 0x84: 2, 0xa9: 1, 0xbc: 1,
         0xb2: 2, 0xb3: 2, 0xb4: 2, 0xb5: 2, 0xb6: 2, 0xb7: 2, 0xb8: 2, 0xb9: 4, 0xba: 4,
         0xbb: 2, 0xbd: 2, 0xc0: 2, 0xc1: 2, 0xc5: 3, 0xc6: 2, 0xc7: 2, 0xc8: 4, 0xc9: 4}
for op in range(0x99, 0xa9):
    OPLEN[op] = 2


def ldc_new_pairs(c):
    """(string, class) for every `ldc "name"` followed by `new Class` in the static initializer"""
    clinit = next((m for m in c.methods if m.name == "<clinit>"), None)
    if clinit is None or clinit.code is None:
        return []
    code, pc, last, out = clinit.code, 0, None, []
    while pc < len(code):
        op = code[pc]
        if op in (0x12, 0x13):
            ref = code[pc + 1] if op == 0x12 else struct.unpack(">H", code[pc + 1:pc + 3])[0]
            last = c.utf8.get(c.str_ref.get(ref)) if ref in c.str_ref else None
        elif op == 0xbb:
            ref = struct.unpack(">H", code[pc + 1:pc + 3])[0]
            if last is not None:
                out.append((last, c.utf8[c.cls_ref[ref]]))
            last = None
        if op == 0xaa:  # tableswitch
            p = (pc + 4) & ~3
            lo, hi = struct.unpack(">ii", code[p + 4:p + 12])
            pc = p + 12 + 4 * (hi - lo + 1)
            continue
        if op == 0xab:  # lookupswitch
            p = (pc + 4) & ~3
            npairs = struct.unpack(">i", code[p + 4:p + 8])[0]
            pc = p + 8 + 8 * npairs
            continue
        if op == 0xc4:  # wide
            pc += 6 if code[pc + 1] == 0x84 else 4
            continue
        pc += 1 + OPLEN.get(op, 0)
    return out


def java_type(d):
    """descriptor of one type -> Java source type"""
    dims = 0
    while d.startswith("["):
        dims += 1
        d = d[1:]
    prim = {"V": "void", "Z": "boolean", "B": "byte", "C": "char", "S": "short", "I": "int",
            "J": "long", "F": "float", "D": "double"}
    base = prim[d] if d in prim else d[1:-1].replace("/", ".").replace("$", ".")
    if base.startswith("net.minecraft.") and d[1:-1] in DEAD:
        base = FACADE_PKG + "." + d[1:-1].split("/")[-1]  # a dead class: its facade (flat, $ in the name)
    return base + "[]" * dims


def split(desc):
    args, i = [], 1
    while desc[i] != ")":
        j = i
        while desc[j] == "[":
            j += 1
        if desc[j] == "L":
            j = desc.index(";", j)
        args.append(desc[i:j + 1])
        i = j + 1
    return args, desc[i + 1:]


DEAD = set()

# Private members of a serializer that a mod's own access widener could open (wiring-check holds a facade
# to every member the dead class had): ported by hand, one per class that has one.
EXTRA = {
    # ExplorationMapFunction.Serializer.readDestination(json): 1.20.1's body
    "net/minecraft/class_111$class_113": [
        "    /** readDestination(json), private in 1.20.1: \"destination\" as a structure tag, else the default */",
        "    public static net.minecraft.class_6862<net.minecraft.class_3195> method_29039(JsonObject json) {",
        "        if (json.has(\"destination\")) {",
        "            String s = net.minecraft.class_3518.method_15265(json, \"destination\");",
        "            return net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41246, new net.minecraft.class_2960(s));",
        "        }",
        "        return net.minecraft.class_111.field_25032;",
        "    }",
    ],
}


def main():
    j1_path, j2_path, tsv, out_dir, frag = sys.argv[1:6]
    area = re.compile(sys.argv[6] if len(sys.argv) > 6 else r"^net/minecraft/(world/level/storage|data)/")
    v1, v2 = load(j1_path), load(j2_path)
    DEAD.update(n for n in v1 if n not in v2)
    mfield = {}
    mclass = {}
    for line in open(tsv, encoding="utf-8"):
        p = line.rstrip("\n").split("\t")
        if len(p) >= 3 and p[0] == "F":
            mfield[p[1]] = p[2].rsplit(".", 1)[1]
        elif len(p) >= 3 and p[0] == "C":
            mclass[p[1]] = p[2]
    ids = {}
    for reg, what in REGISTRIES.items():
        for name, ser in ldc_new_pairs(v1[reg]):
            ids[ser] = (what, name if ":" in name else "minecraft:" + name)

    inner_flags = {}
    for c in v1.values():
        for k, f in c.inner_flags.items():
            inner_flags.setdefault(k, f)

    def simple(n):
        m = mclass.get(n)
        if m:
            return m.split("/")[-1]
        if "$" in n:
            o, i = n.rsplit("$", 1)
            return simple(o) + "$" + i
        return n.split("/")[-1]

    renames = {}
    written = []
    for name in sorted(DEAD):
        c = v1[name]
        if "$" not in name or c.access & (ACC_INTERFACE | ACC_ABSTRACT) or not c.access & ACC_PUBLIC:
            continue
        flags = inner_flags.get(name, 0)
        if not flags & ACC_PUBLIC or not flags & ACC_STATIC:
            continue
        outer = name.rsplit("$", 1)[0]
        if outer not in v2 or not area.search(mclass.get(outer, "")):
            continue
        if c.super == FN_BASE:
            kind = "fn"
        elif c.super == LEAF_BASE:
            kind = "leaf"
        elif c.super == "java/lang/Object" and c.interfaces == [SERIALIZER]:
            kind = "ser"
        elif c.super == "java/lang/Object" and c.interfaces == [INLINE]:
            kind = "inline"
        elif c.super == "java/lang/Object" and sorted(c.interfaces) == [GSON + "JsonDeserializer", GSON + "JsonSerializer"]:
            kind = "gson"
        else:
            continue
        codecs = {}
        for f in v2[outer].fields:
            if f.acc & ACC_STATIC and f.acc & ACC_PUBLIC and f.desc == "Lcom/mojang/serialization/Codec;" \
                    and f.sig == "Lcom/mojang/serialization/Codec<L%s;>;" % outer:
                codecs[mfield.get(outer + "." + f.name, f.name)] = f.name
        if kind == "inline":
            codec = codecs.get("INLINE_CODEC")
        else:
            codec = codecs.get("CODEC") or codecs.get("MAP_CODEC")
            if codec is None:
                plain = [v for k, v in codecs.items() if k != "INLINE_CODEC"]
                codec = plain[0] if len(plain) == 1 else None
        if codec is None:
            raise SystemExit(f"{name}: no 1.20.2 codec on {outer}: {codecs}")
        what = ids.get(name, (None, None))
        is_condition = outer in v2 and CONDITION in v2[outer].interfaces
        label = (what[0] + " " + what[1]) if what[0] else simple(outer)
        src = emit(c, kind, outer, codec, label, what[1] if what[0] == "loot condition" or is_condition else None,
                   simple(name), simple(outer))
        if src is None:
            raise SystemExit(f"{name}: unrecognised method shape for kind {kind}")
        fname = name.split("/")[-1]
        with open(os.path.join(out_dir, fname + ".java"), "w", encoding="utf-8", newline="\n") as o:
            o.write(src)
        renames[name] = FACADE_INT + "/" + fname
        written.append((name, kind, codec))

    data = json.load(open(frag, encoding="utf-8")) if os.path.exists(frag) else {}
    cr = data.setdefault("classRenames", {})
    for k, v in renames.items():
        cr[k] = v
    data["classRenames"] = dict(sorted(cr.items()))
    with open(frag, "w", encoding="utf-8", newline="\n") as o:
        json.dump(data, o, indent=1, ensure_ascii=False)
        o.write("\n")
    for w in written:
        print("\t".join(w))
    print(f"{len(written)} facades", file=sys.stderr)


def emit(c, kind, outer, codec, label, cond_id, simple_name, simple_outer):
    O = java_type("L" + outer + ";")
    fname = c.name.split("/")[-1]
    CODEC = f"{O}.{codec}"
    lines = []
    # every public/protected method 1.20.1 declared, constructor aside
    methods = [m for m in c.methods if m.name not in ("<init>", "<clinit>") and m.acc & (ACC_PUBLIC | ACC_PROTECTED)]

    def acc(m):
        # every member public: an access widener never reaches a CenturyBridge class, so a protected
        # 1.20.1 member a mod opened with its own AW must already be open here (wiring-check's rule);
        # a 1.20.1 subclass overriding it as protected still overrides it (the JVM does not hold an
        # override to the overridden method's access)
        return "public" + (" final" if m.acc & ACC_FINAL else "")

    def find(pred):
        r = [m for m in methods if pred(m)]
        return r[0] if r else None

    o_ = "L" + outer + ";"
    if kind == "fn":
        write = find(lambda m: not m.acc & ACC_SYNTHETIC and m.desc == f"({JO}{o_}{JSC})V")
        read = find(lambda m: not m.acc & ACC_SYNTHETIC and m.desc == f"({JO}{JDC}{CONDS}){o_}")
        if read is None:
            return None
        if write:
            lines.append(f"    /** toJson: the conditions as the base writes them, then this function's own fields */")
            lines.append(f"    {acc(write)} void {write.name}(JsonObject json, {O} value, JsonSerializationContext context) {{")
            lines.append(f"        super.method_529(json, value, context);")
            lines.append(f"        {RT}.writeFields(json, {CODEC}, value, \"{label}\", \"conditions\");")
            lines.append(f"    }}")
            lines.append("")
        lines.append(f"    /** fromJson(json, context, conditions) */")
        lines.append(f"    {acc(read)} {O} {read.name}(JsonObject json, JsonDeserializationContext context, net.minecraft.class_5341[] conditions) {{")
        lines.append(f"        return {RT}.readFunction({CODEC}, json, conditions, \"{label}\");")
        lines.append(f"    }}")
        for m in methods:
            if m is write or m is read:
                continue
            if m.name == "method_530" and m.desc == f"({JO}{JDC}{CONDS})Lnet/minecraft/class_120;":
                lines += ["", "    @Override",
                          f"    {acc(m)} net.minecraft.class_120 method_530(JsonObject json, JsonDeserializationContext context, net.minecraft.class_5341[] conditions) {{",
                          f"        return {read.name}(json, context, conditions);", "    }"]
            elif m.name == "method_529" and m.desc == f"({JO}Lnet/minecraft/class_120;{JSC})V" and write:
                lines += ["", "    @Override",
                          f"    {acc(m)} void method_529(JsonObject json, net.minecraft.class_120 value, JsonSerializationContext context) {{",
                          f"        {write.name}(json, ({O}) value, context);", "    }"]
            elif m.name == "method_516" and m.acc & ACC_SYNTHETIC:
                pass  # the base facade's own bridge forwards to method_529, as this one did
            else:
                return None
        head = f"public class {fname} extends {FACADE_PKG}.class_120$class_123"
    elif kind == "leaf":
        write = find(lambda m: not m.acc & ACC_SYNTHETIC and m.desc == f"({JO}{o_}{JSC})V")
        read = find(lambda m: not m.acc & ACC_SYNTHETIC and m.desc == f"({JO}{JDC}II{CONDS}{FUNCS}){o_}")
        if read is None:
            return None
        if write:
            lines.append(f"    /** addEntryFields: weight, quality and functions as the base writes them, then this entry's own fields */")
            lines.append(f"    {acc(write)} void {write.name}(JsonObject json, {O} value, JsonSerializationContext context) {{")
            lines.append(f"        super.method_442(json, value, context);")
            lines.append(f"        {RT}.writeFields(json, {CODEC}, value, \"{label}\", \"conditions\", \"functions\", \"weight\", \"quality\");")
            lines.append(f"    }}")
            lines.append("")
        lines.append(f"    /** fromJson(json, context, weight, quality, conditions, functions) */")
        lines.append(f"    {acc(read)} {O} {read.name}(JsonObject json, JsonDeserializationContext context, int weight, int quality,")
        lines.append(f"            net.minecraft.class_5341[] conditions, net.minecraft.class_117[] functions) {{")
        lines.append(f"        return {RT}.readLeaf({CODEC}, json, weight, quality, conditions, functions, \"{label}\");")
        lines.append(f"    }}")
        for m in methods:
            if m is write or m is read:
                continue
            if m.name == "method_443" and m.desc == f"({JO}{JDC}II{CONDS}{FUNCS})Lnet/minecraft/class_85;":
                lines += ["", "    @Override",
                          f"    {acc(m)} net.minecraft.class_85 method_443(JsonObject json, JsonDeserializationContext context, int weight, int quality,",
                          "            net.minecraft.class_5341[] conditions, net.minecraft.class_117[] functions) {",
                          f"        return {read.name}(json, context, weight, quality, conditions, functions);", "    }"]
            elif m.name == "method_442" and m.desc == f"({JO}Lnet/minecraft/class_85;{JSC})V" and write:
                lines += ["", "    @Override",
                          f"    {acc(m)} void method_442(JsonObject json, net.minecraft.class_85 value, JsonSerializationContext context) {{",
                          f"        {write.name}(json, ({O}) value, context);", "    }"]
            elif m.name == "method_422" and m.acc & ACC_SYNTHETIC:
                pass  # the base facade's bridge forwards to method_442, as this one did
            else:
                return None
        head = f"public class {fname} extends {FACADE_PKG}.class_85$class_90"
    elif kind == "ser":
        write = find(lambda m: not m.acc & ACC_SYNTHETIC and m.desc == f"({JO}{o_}{JSC})V")
        read = find(lambda m: not m.acc & ACC_SYNTHETIC and m.desc == f"({JO}{JDC}){o_}")
        if read is None or write is None:
            return None
        reader = "read" if cond_id else "readProvider"
        lines.append(f"    /** toJson */")
        lines.append(f"    {acc(write)} void {write.name}(JsonObject json, {O} value, JsonSerializationContext context) {{")
        lines.append(f"        {RT}.writeFields(json, {CODEC}, value, \"{label}\");")
        lines.append(f"    }}")
        lines.append("")
        lines.append(f"    /** fromJson */")
        lines.append(f"    {acc(read)} {O} {read.name}(JsonObject json, JsonDeserializationContext context) {{")
        if cond_id:
            lines.append(f"        return {RT}.read({CODEC}, json, \"{cond_id}\", \"{label}\");")
        else:
            lines.append(f"        return {RT}.readProvider({CODEC}, json, \"{label}\");")
        lines.append(f"    }}")
        for m in methods:
            if m is write or m is read:
                continue
            if m.name == "method_516" and m.desc == f"({JO}Ljava/lang/Object;{JSC})V":
                lines += ["", "    @Override",
                          f"    {acc(m)} void method_516(JsonObject json, Object value, JsonSerializationContext context) {{",
                          f"        {write.name}(json, ({O}) value, context);", "    }"]
            elif m.name == "method_517" and m.desc == f"({JO}{JDC})Ljava/lang/Object;":
                lines += ["", "    @Override",
                          f"    {acc(m)} Object method_517(JsonObject json, JsonDeserializationContext context) {{",
                          f"        return {read.name}(json, context);", "    }"]
            else:
                return None
        head = f"public class {fname} implements {FACADE_PKG}.class_5335"
    elif kind == "inline":
        write = find(lambda m: not m.acc & ACC_SYNTHETIC and m.desc == f"({o_}{JSC}){JE}")
        read = find(lambda m: not m.acc & ACC_SYNTHETIC and m.desc == f"({JE}{JDC}){o_}")
        if read is None or write is None:
            return None
        lines.append(f"    /** the inline form (a bare value) */")
        lines.append(f"    {acc(write)} JsonElement {write.name}({O} value, JsonSerializationContext context) {{")
        lines.append(f"        return {RT}.encode({CODEC}, value, \"{label}\");")
        lines.append(f"    }}")
        lines.append("")
        lines.append(f"    {acc(read)} {O} {read.name}(JsonElement json, JsonDeserializationContext context) {{")
        lines.append(f"        return {RT}.readProvider({CODEC}, json, \"{label}\");")
        lines.append(f"    }}")
        for m in methods:
            if m is write or m is read:
                continue
            if m.name == "method_29309" and m.desc == f"(Ljava/lang/Object;{JSC}){JE}":
                lines += ["", "    @Override",
                          f"    {acc(m)} JsonElement method_29309(Object value, JsonSerializationContext context) {{",
                          f"        return {write.name}(({O}) value, context);", "    }"]
            elif m.name == "method_29308" and m.desc == f"({JE}{JDC})Ljava/lang/Object;":
                lines += ["", "    @Override",
                          f"    {acc(m)} Object method_29308(JsonElement json, JsonDeserializationContext context) {{",
                          f"        return {read.name}(json, context);", "    }"]
            else:
                return None
        head = f"public class {fname} implements {FACADE_PKG}.class_5330$class_5332"
    elif kind == "gson":
        read = find(lambda m: not m.acc & ACC_SYNTHETIC and m.desc == f"({JE}{TYPE}{JDC}){o_}")
        write = find(lambda m: not m.acc & ACC_SYNTHETIC and m.desc == f"({o_}{TYPE}{JSC}){JE}")
        if read is None or write is None:
            return None
        rule = {"net/minecraft/class_52": "legacyTable", "net/minecraft/class_55": "legacyPool"}.get(outer)
        lines.append(f"    {acc(read)} {O} {read.name}(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {{")
        if rule:
            lines.append(f"        return {RT}.decode({CODEC}, json.isJsonObject() ? {RT}.{rule}(json.getAsJsonObject()) : json, \"{label}\");")
        else:
            lines.append(f"        return {RT}.decode({CODEC}, json, \"{label}\");")
        lines.append(f"    }}")
        lines.append("")
        lines.append(f"    {acc(write)} JsonElement {write.name}({O} value, Type type, JsonSerializationContext context) {{")
        lines.append(f"        return {RT}.encode({CODEC}, value, \"{label}\");")
        lines.append(f"    }}")
        lines.append("")
        lines.append(f"    @Override")
        lines.append(f"    public {O} deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {{")
        lines.append(f"        return {read.name}(json, type, context);")
        lines.append(f"    }}")
        lines.append("")
        lines.append(f"    @Override")
        lines.append(f"    public JsonElement serialize({O} value, Type type, JsonSerializationContext context) {{")
        lines.append(f"        return {write.name}(value, type, context);")
        lines.append(f"    }}")
        for m in methods:
            if m is write or m is read or (m.acc & ACC_SYNTHETIC and m.name in ("serialize", "deserialize")):
                continue
            return None
        head = f"public class {fname} implements com.google.gson.JsonDeserializer<{O}>, com.google.gson.JsonSerializer<{O}>"
    else:
        return None

    if c.name in EXTRA:
        lines += [""] + EXTRA[c.name]
    elif any(m.acc & ACC_PRIVATE and not m.acc & ACC_SYNTHETIC for m in c.methods)             or any(f.acc & ACC_PRIVATE for f in c.fields):
        return None  # a private member with no port: add one to EXTRA
    imports = ["com.google.gson.JsonDeserializationContext", "com.google.gson.JsonElement", "com.google.gson.JsonObject",
               "com.google.gson.JsonParseException", "com.google.gson.JsonSerializationContext", "java.lang.reflect.Type"]
    body = "\n".join(lines)
    used = [i for i in imports if re.search(r"\b" + i.rsplit(".", 1)[1] + r"\b", body)]
    out = [f"package {FACADE_PKG};", ""]
    out += [f"import {i};" for i in used]
    out += ["",
            "/**",
            f" * Facade for 1.20.1 {c.name} ({simple_name}), deleted in 1.20.2's loot codec rework.",
            f" * Generated by core/tools/gen_loot_serializer_facades.py -- do not edit; see that tool and",
            f" * rt/v1_20_2/CbLootSerializers for what the bodies do. Read and written through",
            f" * {simple_outer}'s 1.20.2 codec ({codec}).",
            " */",
            '@SuppressWarnings({"rawtypes", "unchecked"})',
            head + " {",
            "",
            f"    public {fname}() {{",
            "    }",
            "",
            body,
            "}",
            ""]
    return "\n".join(out)


if __name__ == "__main__":
    main()
