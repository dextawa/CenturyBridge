"""Fabric API convention tags across the 1.21.11 -> 26.1 wall, from both releases' bytecode and data.

26.1's Fabric API dropped fabric-convention-tags-v1 (the v1 tag holder classes) and took 33 deprecated fields
off convention-tags-v2's ConventionalItemTags / ConventionalBlockTags, together with the tag files behind them
and the optional references by which v2's tags took in v1's (c:tools/bow included #c:bows). A tag holder is
nothing but TagKeys: each field is TagKey.create(<registry>, c:<path>), read here off the class initializer
(ldc path, invokestatic register, putstatic field) and the registry its register helper's TagRegistration
names. So:

  v1 holders     -> compat/fapi/tag/convention/v1/<Class>.java, the same fields with the same tag ids
                    (classRenames; v1 TagUtil only forwarded to v2's TagUtil, a plain rename in
                    swfapi-renames.tsv)
  dropped v2     -> rt/CbFapiConvention<Item|Block>Tags.java, the same fields with the same ids
                    (fieldRedirects: a GETSTATIC reads the layer's same-name field)
  tag data       -> resources/data/c/tags/...: for every tag file of V's convention-tags-v2 the values
                    W's no longer has, so the loaded tag is V's (tag files of all packs merge, values add)
                    -- every value an optional tag reference or a vanilla id W still has (checked)

  python gen_swfapi_tags.py <V fabric-api.jar> <W fabric-api.jar> <W game jar> <inter-to-mojang-1.21.11.tsv>
         <layer dir core/shims/steps/26.1> <step wiring.json>
writes the sources and data under the layer and <step wiring>.d/swfapi-tags-gen.json -- regenerate, never
hand-edit.
"""
import io
import json
import os
import struct
import sys
import zipfile

PKG = "top/dext/centurybridge/step/v26_1"
V1 = "net/fabricmc/fabric/api/tag/convention/v1/"
V2 = "net/fabricmc/fabric/api/tag/convention/v2/"
# Registries field -> the element type its TagKeys are of (javac checks each against Registries' own signature)
ELEMENT = {
    "ITEM": "net.minecraft.world.item.Item", "BLOCK": "net.minecraft.world.level.block.Block",
    "BIOME": "net.minecraft.world.level.biome.Biome", "FLUID": "net.minecraft.world.level.material.Fluid",
    "ENTITY_TYPE": "net.minecraft.world.entity.EntityType<?>",
    "ENCHANTMENT": "net.minecraft.world.item.enchantment.Enchantment",
    "STRUCTURE": "net.minecraft.world.level.levelgen.structure.Structure",
}
# the data folder of each registry's tags
FOLDER = {"ITEM": "item", "BLOCK": "block", "BIOME": "worldgen/biome", "FLUID": "fluid", "ENTITY_TYPE": "entity_type",
          "ENCHANTMENT": "enchantment", "STRUCTURE": "worldgen/structure"}


class Cls:
    """a class file's constant pool and methods' code, enough to read static initializers"""

    def __init__(self, b):
        n = struct.unpack(">H", b[8:10])[0]
        self.cp = [None] * n
        i, idx = 10, 1
        while idx < n:
            t = b[i]
            if t == 1:
                ln = struct.unpack(">H", b[i + 1:i + 3])[0]
                self.cp[idx] = ("u", b[i + 3:i + 3 + ln].decode("utf-8"))
                i += 3 + ln
            elif t in (3, 4):
                i += 5
            elif t in (5, 6):
                i += 9
                idx += 1
            elif t in (7, 8, 16, 19, 20):
                self.cp[idx] = ({7: "c", 8: "s"}.get(t, "x"), struct.unpack(">H", b[i + 1:i + 3])[0])
                i += 3
            elif t in (9, 10, 11, 12):
                a, c = struct.unpack(">HH", b[i + 1:i + 5])
                self.cp[idx] = ({9: "f", 10: "m", 11: "m", 12: "nt"}[t], a, c)
                i += 5
            elif t in (17, 18):
                i += 5
            elif t == 15:
                i += 4
            else:
                raise ValueError(t)
            idx += 1
        i += 6
        nif = struct.unpack(">H", b[i:i + 2])[0]
        i += 2 + 2 * nif
        self.fields, self.methods = [], {}
        for kind in ("f", "m"):
            cnt = struct.unpack(">H", b[i:i + 2])[0]
            i += 2
            for _ in range(cnt):
                acc, nm, ds, na = struct.unpack(">HHHH", b[i:i + 8])
                i += 8
                code = None
                for _ in range(na):
                    an, ln = struct.unpack(">HI", b[i:i + 6])
                    if self.u(an) == "Code":
                        clen = struct.unpack(">I", b[i + 10:i + 14])[0]
                        code = b[i + 14:i + 14 + clen]
                    i += 6 + ln
                if kind == "f":
                    self.fields.append((acc, self.u(nm), self.u(ds)))
                else:
                    self.methods[self.u(nm) + self.u(ds)] = code

    def u(self, k):
        return self.cp[k][1]

    def ref(self, k):
        """(owner, name, desc) of a field/method ref"""
        _, c, nt = self.cp[k]
        _, n, d = self.cp[nt]
        return self.u(self.cp[c][1]), self.u(n), self.u(d)

    def ldc_string(self, k):
        e = self.cp[k]
        return self.u(e[1]) if e and e[0] == "s" else None

    def insns(self, code):
        """(opcode, operand) of a straight-line static initializer (enough opcodes for tag holders)"""
        i = 0
        while i < len(code):
            op = code[i]
            if op == 0x12:  # ldc
                yield op, code[i + 1]
                i += 2
            elif op in (0x13, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xbb, 0xc0):  # ldc_w, field, invoke, new, checkcast
                yield op, struct.unpack(">H", code[i + 1:i + 3])[0]
                i += 3
            elif op == 0xb9 or op == 0xba:
                yield op, struct.unpack(">H", code[i + 1:i + 3])[0]
                i += 5
            elif op in (0x59, 0xb1, 0xb0, 0x2a, 0x2b, 0x4c, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x02, 0x01, 0x57):
                yield op, None
                i += 1
            elif op in (0x10, 0x15, 0x19, 0x36, 0x3a, 0xbc):
                yield op, code[i + 1]
                i += 2
            elif op == 0x11:
                i += 3
            elif op == 0xbd:
                yield op, struct.unpack(">H", code[i + 1:i + 3])[0]
                i += 3
            elif 0x4f <= op <= 0x56 or 0x2e <= op <= 0x35:
                yield op, None
                i += 1
            else:
                raise ValueError(f"opcode 0x{op:02x}")


def classes_of(jar, prefix):
    out = {}

    def scan(z):
        for n in z.namelist():
            if n.startswith(prefix) and n.endswith(".class"):
                out.setdefault(n[:-6], z.read(n))
            elif n.startswith("META-INF/jars/") and n.endswith(".jar"):
                scan(zipfile.ZipFile(io.BytesIO(z.read(n))))
    scan(zipfile.ZipFile(jar))
    return out


def resources_of(jar, prefix):
    out = {}

    def scan(z):
        for n in z.namelist():
            if n.startswith(prefix) and n.endswith(".json"):
                out.setdefault(n, json.loads(z.read(n)))
            elif n.startswith("META-INF/jars/") and n.endswith(".jar"):
                scan(zipfile.ZipFile(io.BytesIO(z.read(n))))
    scan(zipfile.ZipFile(jar))
    return out


def main():
    vjar, wjar, wgame, table, layer, wiring = sys.argv[1:7]
    mm = {}
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "F":
            mm[f[1]] = f[2]
    vcls = classes_of(vjar, "net/fabricmc/fabric/")
    wcls = classes_of(wjar, "net/fabricmc/fabric/")

    def registrations(impl):
        """TagRegistration field -> Registries field name, read off its static initializer"""
        c = Cls(vcls[impl])
        out, last = {}, None
        for op, a in c.insns(c.methods["<clinit>()V"]):
            if op == 0xb2:
                o, n, _ = c.ref(a)
                last = mm.get(f"{o}.{n}", "").rsplit(".", 1)[-1] or None
            elif op == 0xb3:
                o, n, _ = c.ref(a)
                if o == impl and last:
                    out[n] = last
                last = None
        return out

    def holder(name, regs):
        """[(field, Registries field, path)] of a tag holder class, in declaration order"""
        c = Cls(vcls[name])
        # its register helper(s): which TagRegistration, which namespace
        helpers = {}
        for sig, code in c.methods.items():
            if sig.startswith("register(") and code:
                reg, ns = None, None
                for op, a in c.insns(code):
                    if op == 0xb2:
                        o, n, _ = c.ref(a)
                        reg = regs[o].get(n)
                    elif op == 0xb6:
                        _, n, _ = c.ref(a)
                        ns = {"registerC": "c", "registerFabric": "fabric"}.get(n)
                if not reg or not ns:
                    raise SystemExit(f"{name}.{sig}: not a TagRegistration helper")
                helpers[sig] = (reg, ns)
        out, last = [], None
        for op, a in c.insns(c.methods["<clinit>()V"]):
            if op in (0x12, 0x13):
                last = c.ldc_string(a)
            elif op == 0xb8:
                o, n, d = c.ref(a)
                if o != name or (n + d) not in helpers or last is None:
                    raise SystemExit(f"{name}: unexpected static initializer call {o}.{n}{d}")
                reg, ns = helpers[n + d]
                pending = (reg, ns, last)
                last = None
            elif op == 0xb3:
                o, n, _ = c.ref(a)
                if o == name:
                    out.append((n, pending[0], pending[1], pending[2]))
        return out

    regs = {}
    for impl in vcls:
        if impl.endswith("/TagRegistration"):
            regs[impl] = registrations(impl)
    v1 = sorted(n for n in vcls if n.startswith(V1) and n.rsplit("/", 1)[1].startswith("Conventional"))
    frag = {"classRenames": {}, "fieldRedirects": {}}
    srcroot = os.path.join(layer, "src")
    written = []

    def write_holder(cls_internal, simple, fields, doc):
        path = os.path.join(srcroot, cls_internal + ".java")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        pkg = cls_internal.rsplit("/", 1)[0].replace("/", ".")
        lines = ["// generated by core/tools/gen_swfapi_tags.py -- regenerate, never hand-edit", f"package {pkg};", "",
                 "import net.minecraft.core.registries.Registries;", "import net.minecraft.resources.Identifier;",
                 "import net.minecraft.tags.TagKey;", "", "/**"] + [f" * {l}" for l in doc] + [" */",
                 f"public final class {simple} {{", "", f"    private {simple}() {{", "    }", ""]
        for f, reg, ns, p in fields:
            lines.append(f"    public static final TagKey<{ELEMENT[reg]}> {f} = TagKey.create(Registries.{reg}, "
                         f"Identifier.fromNamespaceAndPath(\"{ns}\", \"{p}\"));")
        lines += ["}", ""]
        with open(path, "w", encoding="utf-8", newline="\n") as o:
            o.write("\n".join(lines))
        written.append(path)

    for name in v1:
        fields = holder(name, regs)
        simple = name.rsplit("/", 1)[1]
        target = f"{PKG}/compat/fapi/tag/convention/v1/{simple}"
        write_holder(target, simple, fields, [
            f"1.21.11's {name.replace('/', '.')} (fabric-convention-tags-v1, which 26.1's Fabric API dropped): every",
            "field the same tag key, c:<path> in the same registry, read off 1.21.11's class initializer."])
        frag["classRenames"][name] = target
    # v2 fields 26.1 no longer declares
    for name in sorted(n for n in vcls if n.startswith(V2) and n.rsplit("/", 1)[1].startswith("Conventional")):
        if name not in wcls:
            continue
        have = {f[1] for f in Cls(wcls[name]).fields}
        gone = [f for f in holder(name, regs) if f[0] not in have]
        if not gone:
            continue
        simple = "CbFapiConvention" + name.rsplit("/", 1)[1][len("Conventional"):]
        target = f"{PKG}/rt/{simple}"
        write_holder(target, simple, gone, [
            f"The fields 26.1 took off {name.replace('/', '.')} (deprecated in 1.21.11, the same keys),",
            "read off 1.21.11's class initializer; a 1.21.11 mod's GETSTATIC reads them here (fieldRedirects)."])
        desc = Cls(vcls[name]).fields
        for f, *_ in gone:
            d = next(x[2] for x in desc if x[1] == f)
            frag["fieldRedirects"][f"{name}.{f}:{d}"] = target
    # tag data: V's values W's files lack
    vres = resources_of(vjar, "data/c/tags/")
    wres = resources_of(wjar, "data/c/tags/")
    with zipfile.ZipFile(wgame) as z:
        game = set(z.namelist())
    key = lambda v: json.dumps(v, sort_keys=True)
    datadir = os.path.join(layer, "resources")
    nfiles = nvals = 0
    for rel, j in sorted(vres.items()):
        have = {key(x) for x in wres.get(rel, {}).get("values", [])}
        extra = [x for x in j.get("values", []) if key(x) not in have]
        if j.get("replace"):
            raise SystemExit(f"{rel}: a replacing tag file")
        if not extra:
            continue
        folder = rel[len("data/c/tags/"):].rsplit("/", 1)[0]
        for x in extra:
            i = x if isinstance(x, str) else x["id"]
            if i.startswith("#"):
                ns, p = i[1:].split(":")
                if ns == "minecraft" and f"data/minecraft/tags/{folder.split('/')[0] if folder.startswith(('item', 'block')) else folder}/{p}.json" not in game \
                        and not (isinstance(x, dict) and x.get("required") is False):
                    raise SystemExit(f"{rel}: required reference {i} W lacks")
            elif isinstance(x, str):
                ns, p = i.split(":")
                kind = rel[len("data/c/tags/"):].split("/")[0]
                probe = {"item": f"assets/minecraft/items/{p}.json", "block": f"assets/minecraft/blockstates/{p}.json"}.get(kind)
                if ns != "minecraft" or (probe and probe not in game):
                    raise SystemExit(f"{rel}: value {i} W does not have")
        out = os.path.join(datadir, rel)
        os.makedirs(os.path.dirname(out), exist_ok=True)
        with open(out, "w", encoding="utf-8", newline="\n") as o:
            o.write(json.dumps({"values": extra}, indent=2) + "\n")
        nfiles += 1
        nvals += len(extra)
    d = wiring + ".d"
    with open(os.path.join(d, "swfapi-tags-gen.json"), "w", encoding="utf-8", newline="\n") as o:
        o.write(json.dumps(frag, indent=1) + "\n")
    print(f"{len(v1)} v1 holders, {len(frag['fieldRedirects'])} v2 fields, {nfiles} tag files ({nvals} values) restored")
    print(f"  -> {len(written)} sources, {d}/swfapi-tags-gen.json")


if __name__ == "__main__":
    main()
