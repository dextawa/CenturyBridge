"""ResourceKey constants 26.1 dropped from the datagen key holders: given back as the keys 1.21.11 held.

A ResourceKey is a value -- a registry and an id -- and 1.21.11's VegetationFeatures.TREES_PLAINS (or
TradeRebalanceEnchantmentProviders.*) was ResourceKey.create(<registry>, minecraft:<id>). 26.1 removed some
of those constants (the features or providers were renamed or folded); a 1.21.11 mod reading one gets the
same key back, which names what it named in 1.21.11 -- whether 26.1's data registers that id is data, as it
was for any key in 1.21.11.

Only get-static rows of the step's damage table typed ResourceKey whose owner 26.1 lacks the field are
considered, and only where 1.21.11's static initializer sets the field from `ldc "<id>"; invokestatic
<helper>(String)ResourceKey` and the helper is exactly ResourceKey.create(<Registries field>,
Identifier.withDefaultNamespace(name)). Everything else is listed in the report.

  python gen_wall_keys.py <damage.tsv> <V code jar> <W jar> <wall table> <fragment.json> <runtime class> <src root> <report.tsv> [--tags]

  --tags: the same for TagKey constants (BlockTags.X = TagKey.create(<Registries field>, withDefaultNamespace(name))),
          given back as TagKey.create of the same registry and id.
  --tag-data <V jar with data/>: with --tags, a dropped tag 26.1 only renamed is answered with 26.1's tag instead:
          the one 26.1 vanilla tag of the registry whose resolved members equal the 1.21.11 tag's AND whose 26.1
          readers (classes naming its constant, data generators aside) are exactly the 1.21.11 readers of the
          dropped constant taken through the wall table (1.21.11 BlockTags.BIG_DRIPLEAF_PLACEABLE is 26.1
          SUPPORTS_BIG_DRIPLEAF: same eleven blocks, read by BigDripleafBlock and BigDripleafStemBlock in both).
          Then what a mod reads is the tag vanilla 26.1 acts on, with whatever 26.1 data adds to it.

Regenerating (from the repo root):
  python3 core/tools/gen_wall_keys.py core/segments/damage/1.21.11__26.1.damage.tsv \
      data/jars/client-1.21.11-intermediary-code.jar data/jars/client-26.1.jar data/mappings/inter-to-mojang-1.21.11-26.1.tsv \
      core/segments/steps/1.21.11__26.1.json.d/swall-keys-gen.json top/dext/centurybridge/step/v26_1/rt/CbWallKeys \
      core/shims/steps/26.1/src <tmp>/keys-report.tsv
  python3 core/tools/gen_wall_keys.py core/segments/damage/1.21.11__26.1.damage.tsv       data/jars/client-1.21.11-intermediary-code.jar data/jars/client-26.1.jar data/mappings/inter-to-mojang-1.21.11-26.1.tsv       core/segments/steps/1.21.11__26.1.json.d/swall-tags-gen.json top/dext/centurybridge/step/v26_1/rt/CbWallTags       core/shims/steps/26.1/src <tmp>/tags-report.tsv --tags --tag-data data/jars/client-1.21.11-obf.jar
"""
import json
import os
import re
import struct
import subprocess
import sys
import zipfile

KEY = "Lnet/minecraft/class_5321;"
LDC = re.compile(r"ldc(?:_w)?\s+#\d+\s+// String (\S+)")
INV = re.compile(r"invokestatic\s+#\d+\s+// (?:Interface)?Method (?:([\w/$]+)\.)?(\w+):\(Ljava/lang/String;\)Lnet/minecraft/class_5321;")
PUT = re.compile(r"putstatic\s+#\d+\s+// Field (field_\d+):Lnet/minecraft/class_(?:5321|6862);")
REG = re.compile(r"getstatic\s+#\d+\s+// Field net/minecraft/class_7924\.(field_\d+):Lnet/minecraft/class_5321;")


def javap(jar, cls):
    return subprocess.run(["javap", "-c", "-p", "-cp", jar, cls.replace("/", ".")],
                          capture_output=True, text=True, errors="replace").stdout


def method_body(text, name):
    m = re.search(r"\n  [^\n]*\b" + re.escape(name) + r"\(java\.lang\.String\);\n(.*?)\n\n", text, re.S)
    return m.group(1) if m else ""


def field_refs(data):
    """the (class, name) of every Fieldref in a class file's constant pool"""
    n = struct.unpack(">H", data[8:10])[0]
    cp, i, k = [None] * n, 10, 1
    while k < n:
        tag = data[i]
        if tag == 1:
            ln = struct.unpack(">H", data[i + 1:i + 3])[0]
            cp[k] = data[i + 3:i + 3 + ln].decode("utf-8", "replace")
            i += 3 + ln
        elif tag in (7, 8, 16, 19, 20):
            cp[k] = ("ref", struct.unpack(">H", data[i + 1:i + 3])[0])
            i += 3
        elif tag == 15:
            i += 4
        elif tag in (3, 4):
            i += 5
        elif tag in (5, 6):
            i += 9
            k += 1
        elif tag in (9, 10, 11, 12, 17, 18):
            a, b = struct.unpack(">HH", data[i + 1:i + 5])
            cp[k] = (tag, a, b)
            i += 5
        else:
            raise ValueError("constant pool tag %d" % tag)
        k += 1
    out = set()
    for e in cp:
        if isinstance(e, tuple) and e[0] == 9:
            cls = cp[cp[e[1]][1]]
            nat = cp[e[2]]
            out.add((cls, cp[nat[1]]))
    return out


def readers(jar, owner, field):
    """classes of a jar (the owner aside) naming owner.field"""
    found = set()
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n[:-6] != owner:
                data = z.read(n)
                if field.encode() in data and (owner, field) in field_refs(data):
                    found.add(n[:-6])
    return found


def tag_members(jar, registry_dir):
    """every vanilla tag of a registry in a jar's data, resolved to its member ids"""
    with zipfile.ZipFile(jar) as z:
        pre = "data/minecraft/tags/" + registry_dir + "/"
        raw = {n[len(pre):-5]: json.loads(z.read(n)) for n in z.namelist() if n.startswith(pre) and n.endswith(".json")}
    done = {}

    def res(t, seen=()):
        if t in done:
            return done[t]
        out = set()
        for v in raw.get(t, {}).get("values", []):
            i = v["id"] if isinstance(v, dict) else v
            if i.startswith("#"):
                sub = i[1:].split(":", 1)
                if sub[0] == "minecraft" and sub[1] not in seen:
                    out |= res(sub[1], seen + (t,))
                else:
                    out.add(i)
            else:
                out.add(i if ":" in i else "minecraft:" + i)
        done[t] = out
        return out
    return {t: res(t) for t in raw}


def main():
    global KEY, INV
    dmg, vjar, wjar, table, frag, rtcls, src, report = sys.argv[1:9]
    tags = "--tags" in sys.argv[9:]
    if tags:
        KEY = "Lnet/minecraft/class_6862;"
        INV = re.compile(r"invokestatic\s+#\d+\s+// (?:Interface)?Method (?:([\w/$]+)\.)?(\w+):\(Ljava/lang/String;\)Lnet/minecraft/class_6862;")
    create = "class_6862.method_40092:" if tags else "class_5321.method_29179:"
    kind, wtype = ("TagKey", "net.minecraft.tags.TagKey") if tags else ("ResourceKey", "net.minecraft.resources.ResourceKey")
    fmap = {}
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "F":
            fmap[f[1]] = f[2].rsplit(".", 1)[1]
    cmap = {}
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "C":
            cmap[f[1]] = f[2]
    declared = {}

    def w_has(owner, field):
        wo = cmap.get(owner, owner)
        if wo not in declared:
            out = subprocess.run(["javap", "-p", "-cp", wjar, wo.replace("/", ".")],
                                 capture_output=True, text=True, errors="replace").stdout
            declared[wo] = set(re.findall(r" (\w+);$", out, re.M))
        return fmap.get(f"{owner}.{field}", field) in declared[wo]

    owners = {}
    for line in open(dmg, encoding="utf-8"):
        r = line.rstrip("\n").split("\t")
        if len(r) >= 4 and r[0] == "get-static" and r[3] == KEY and not w_has(r[1], r[2]):
            owners.setdefault(r[1], set()).add(r[2])
    rep, getters = [], {}
    helpers = {}
    for owner, fields in sorted(owners.items()):
        text = javap(vjar, owner)
        clinit = text[text.find("static {};"):]
        last, inv = None, None
        found = {}
        for line in clinit.splitlines():
            m = LDC.search(line)
            if m:
                last, inv = m.group(1), None
                continue
            m = INV.search(line)
            if m and last:
                inv = (m.group(1) or owner, m.group(2))  # a helper of the class itself: javap names no owner
                continue
            m = PUT.search(line)
            if m:
                if last and inv:
                    found[m.group(1)] = (last, inv)
                last, inv = None, None
        for field in sorted(fields):
            if field not in found:
                rep.append((owner, field, "no ldc/helper/putstatic pattern in 1.21.11's static initializer"))
                continue
            ident, (hown, hname) = found[field]
            hk = (hown, hname)
            if hk not in helpers:
                body = method_body(javap(vjar, hown), hname)
                regs = REG.findall(body)
                ok = (len(regs) == 1 and "class_2960.method_60656:(Ljava/lang/String;)" in body
                      and create in body and body.count("invoke") == 2)
                helpers[hk] = regs[0] if ok else None
            reg = helpers[hk]
            if not reg:
                rep.append((owner, field, f"helper {hown}.{hname} is not {kind}.create(Registries.X, withDefaultNamespace(name))"))
                continue
            wreg = fmap.get(f"net/minecraft/class_7924.{reg}")
            if not wreg:
                rep.append((owner, field, f"no 26.1 name for Registries.{reg}"))
                continue
            getters[field] = (owner, ident, wreg)
    renamed = {}
    if tags and "--tag-data" in sys.argv:
        vdata = sys.argv[sys.argv.index("--tag-data") + 1]
        dirs = {"BLOCK": "block", "ITEM": "item", "ENCHANTMENT": "enchantment", "ENTITY_TYPE": "entity_type",
                "FLUID": "fluid", "GAME_EVENT": "game_event", "DAMAGE_TYPE": "damage_type"}
        members, consts = {}, {}
        for field, (owner, ident, wreg) in sorted(getters.items()):
            d = dirs.get(wreg)
            if not d:
                continue
            if d not in members:
                members[d] = (tag_members(vdata, d), tag_members(wjar, d))
            vtags, wtags = members[d]
            old = vtags.get(ident)
            same = [t for t, m in wtags.items() if old and m == old]
            if not same:
                continue
            wo = cmap.get(owner, owner)
            if wo not in consts:
                text = javap(wjar, wo)
                clinit = text[text.find("static {};"):]
                consts[wo] = dict(re.findall(r"ldc(?:_w)?\s+#\d+\s+// String (\S+)\n[^\n]*invokestatic[^\n]*\n[^\n]*putstatic\s+#\d+\s+// Field (\w+):Lnet/minecraft/tags/TagKey;", clinit))
            vread = {cmap.get(c, c) for c in readers(vjar, owner, field)}
            vread = {c for c in vread if not c.startswith("net/minecraft/data/")}
            hits = []
            for t in same:
                name = consts[wo].get(t)
                if not name:
                    continue
                wread = {c for c in readers(wjar, wo, name) if not c.startswith("net/minecraft/data/")}
                if wread == vread and wread:
                    hits.append((t, name))
            if len(hits) == 1:
                renamed[field] = (wo, hits[0][0], hits[0][1], sorted(vread))
            else:
                rep.append((owner, field, f"given back under its id: 26.1 tags with its members {same}, same readers {hits}"))
    pkg, simple = rtcls.rsplit("/", 1)
    lines = ["package " + pkg.replace("/", ".") + ";", "",
             "import net.minecraft.core.registries.Registries;",
             "import net.minecraft.resources.Identifier;",
             "import " + wtype + ";", "",
             "/**",
             " * Generated by core/tools/gen_wall_keys.py -- do not edit; regenerate with the command in its header.",
             " *",
             " * " + kind + " constants 26.1 dropped, given back as the keys 1.21.11 held (registry and id).",
             " */",
             "@SuppressWarnings({\"rawtypes\", \"unused\"})",
             "public final class " + simple + " {", "",
             "    private " + simple + "() {", "    }"]
    for field in sorted(getters):
        owner, ident, wreg = getters[field]
        if field in renamed:
            wo, wtag, wname, rd = renamed[field]
            lines += ["", f"    /** {owner}.{field}: minecraft:{ident}, renamed minecraft:{wtag} in 26.1 (same members; read by "
                      + ", ".join(c.rsplit("/", 1)[1] for c in rd) + " in both) */",
                      f"    public static {kind} {field}() {{",
                      f"        return {wo.replace('/', '.').replace('$', '.')}.{wname};",
                      "    }"]
            continue
        lines += ["", f"    /** {owner}.{field}: minecraft:{ident} */",
                  f"    public static {kind} {field}() {{",
                  f"        return {kind}.create(Registries.{wreg}, Identifier.withDefaultNamespace(\"{ident}\"));",
                  "    }"]
    lines += ["}", ""]
    path = os.path.join(src, rtcls + ".java")
    with open(path, "w", encoding="utf-8", newline="\n") as fh:
        fh.write("\n".join(lines))
    out = {"fieldGetters": {f"{o}.{f}:{KEY}": rtcls for f, (o, _, _) in sorted(getters.items())}}
    with open(frag, "w", encoding="utf-8", newline="\n") as fh:
        json.dump(out, fh, indent=1)
        fh.write("\n")
    with open(report, "w", encoding="utf-8", newline="\n") as fh:
        for row in rep:
            fh.write("\t".join(row) + "\n")
    print(f"{len(getters)} keys ({len(renamed)} renamed in 26.1) -> {frag}; {len(rep)} left (report {report})")


if __name__ == "__main__":
    main()
