"""Measure the vanilla-asset references of a mod corpus against a target client.

A 1.20.1 mod names vanilla resources by path: texture identifiers in code
("textures/gui/widgets.png"), the source release's own static texture fields
(ClickableWidget.WIDGETS_TEXTURE), sprite and parent references in its model JSON
("minecraft:block/grass", "block/cube_all"), model references in its blockstates.
The target's vanilla resources were renamed, split and redrawn on the way; a path
the target no longer has renders as the missing texture or model, with nothing in
the log but a warning. This counts, per reference kind, which of those paths the
target lacks, and how each missing one relates to what the target has instead
(the same pixels under another name, or nothing found) -- and which still exist
but with other pixels, so the region a mod blits out of them may be gone.

  python asset_refs.py <corpusDir> <oldClient.jar> <newClient.jar> <out.tsv> [--code-jar <old intermediary code jar>]

With --code-jar, references through the source release's own static texture fields
count too ("vanilla-field").
"""
import hashlib
import json
import os
import re
import struct
import subprocess
import sys
import zipfile
from collections import Counter, defaultdict

CLASS_MAGIC = bytes([0xCA, 0xFE, 0xBA, 0xBE])
PNG_MAGIC = bytes([0x89]) + b"PNG\r\n" + bytes([0x1A]) + b"\n"


def class_consts(b):
    """(string constants, field refs as (owner, name, desc)) of one class file"""
    if b[:4] != CLASS_MAGIC:
        return set(), set()
    n = struct.unpack(">H", b[8:10])[0]
    i, idx = 10, 1
    utf8, strs, cls, nat, frefs = {}, [], {}, {}, []
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
            strs.append(struct.unpack(">H", b[i + 1:i + 3])[0])
            i += 3
        elif tag in (16, 19, 20):
            i += 3
        elif tag == 12:
            nat[idx] = struct.unpack(">HH", b[i + 1:i + 5])
            i += 5
        elif tag == 9:
            frefs.append(struct.unpack(">HH", b[i + 1:i + 5]))
            i += 5
        elif tag in (10, 11, 17, 18):
            i += 5
        elif tag == 15:
            i += 4
        else:
            return set(), set()
        idx += 1
    fields = set()
    for c, nt in frefs:
        if c in cls and nt in nat:
            fields.add((utf8.get(cls[c]), utf8.get(nat[nt][0]), utf8.get(nat[nt][1])))
    return {utf8[x] for x in strs if x in utf8}, fields


CLASS_LINE = re.compile(r"^(?:\S+ )*(?:class|interface|enum) (\S+)")
LDC_TEXTURE = re.compile(r"// String (?:minecraft:)?(textures/\S+\.png)$")
PUT_IDENTIFIER = re.compile(r"putstatic\s+#\d+\s+// Field (\S+):Lnet/minecraft/class_2960;")


def vanilla_texture_fields(code_jar):
    """static Identifier fields of the source release whose <clinit> stores a texture path
    (WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png")): field name -> path. A mod
    names the field through whichever class it wrote (ButtonWidget.WIDGETS_TEXTURE, or its
    own subclass for an inherited one), so references match by the -- unique -- name."""
    with zipfile.ZipFile(code_jar) as z:
        owners = [n[:-6] for n in z.namelist() if n.endswith(".class")
                  and any(x.startswith(("textures/", "minecraft:textures/")) for x in class_consts(z.read(n))[0])]
    fields = {}
    for k in range(0, len(owners), 60):
        chunk = [o.replace("/", ".") for o in owners[k:k + 60]]
        out = subprocess.run(["javap", "-c", "-p", "-cp", code_jar] + chunk, capture_output=True,
                             text=True, encoding="utf-8", errors="replace").stdout
        cur, in_clinit, last = None, False, None
        for line in out.splitlines():
            m = CLASS_LINE.match(line)
            if m and not line.startswith(" "):
                cur, in_clinit = m.group(1).split("<")[0].replace(".", "/"), False
                continue
            if line.strip() == "static {};":
                in_clinit, last = True, None
                continue
            if not in_clinit:
                continue
            m = LDC_TEXTURE.search(line)
            if m:
                last = m.group(1)
                continue
            m = PUT_IDENTIFIER.search(line)
            if m and last:
                f = m.group(1)
                owner, name = f.rsplit(".", 1) if "." in f else (cur, f)
                fields[name] = last  # intermediary field names are unique across classes
                last = None
            elif "putstatic" in line:
                last = None
    return fields


def assets_of(jar):
    """path -> bytes for every assets/minecraft/ entry of a client jar"""
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.startswith("assets/minecraft/") and not n.endswith("/"):
                out[n[len("assets/minecraft/"):]] = z.read(n)
    return out


def pixels(b):
    """decoded RGBA pixels (transparent pixels zeroed), or the raw bytes when PIL is absent"""
    try:
        import io
        import numpy as np
        from PIL import Image
        a = np.asarray(Image.open(io.BytesIO(b)).convert("RGBA")).copy()
        a[a[:, :, 3] == 0] = 0
        return a.tobytes() + str(a.shape).encode()
    except Exception:
        return b


def png_size(b):
    if b[:8] == PNG_MAGIC:
        return struct.unpack(">II", b[16:24])
    return None


TEX_IN_CODE = re.compile(r"^(minecraft:)?(textures/[a-z0-9_./-]+\.png)$")


def split_id(ref):
    if ":" in ref:
        ns, p = ref.split(":", 1)
        return ns, p
    return "minecraft", ref


def json_refs(name, data):
    """(kind, minecraft-relative file path) for every vanilla reference in one mod asset JSON"""
    try:
        j = json.loads(data.decode("utf-8-sig"))
    except Exception:
        return
    parts = name.split("/")
    if len(parts) < 4:
        return
    kind = parts[2]
    if kind == "models" and isinstance(j, dict):
        tex = j.get("textures")
        if isinstance(tex, dict):
            for v in tex.values():
                if isinstance(v, str) and not v.startswith("#"):
                    ns, p = split_id(v)
                    if ns == "minecraft":
                        yield "model-texture", "textures/" + p + ".png"
        par = j.get("parent")
        if isinstance(par, str) and not par.startswith("builtin/"):
            ns, p = split_id(par)
            if ns == "minecraft":
                yield "model-parent", "models/" + p + ".json"
    elif kind == "blockstates" and isinstance(j, dict):
        def walk(o):
            if isinstance(o, dict):
                m = o.get("model")
                if isinstance(m, str):
                    ns, p = split_id(m)
                    if ns == "minecraft":
                        yield "blockstate-model", "models/" + p + ".json"
                for v in o.values():
                    yield from walk(v)
            elif isinstance(o, list):
                for v in o:
                    yield from walk(v)
        yield from walk(j)
    elif kind == "particles" and isinstance(j, dict):
        for v in j.get("textures", []):
            if isinstance(v, str):
                ns, p = split_id(v)
                if ns == "minecraft":
                    yield "particle-texture", "textures/particle/" + p.split("/")[-1] + ".png"


def main():
    corpus, old_jar, new_jar, out = sys.argv[1:5]
    vfields = {}
    if "--code-jar" in sys.argv:
        vfields = vanilla_texture_fields(sys.argv[sys.argv.index("--code-jar") + 1])
        print(f"source-release static texture fields: {len(vfields)}")
    old, new = assets_of(old_jar), assets_of(new_jar)

    def key(p, b):
        return hashlib.md5(pixels(b) if p.endswith(".png") else b).hexdigest()

    new_by_md5 = defaultdict(list)
    for p, b in new.items():
        new_by_md5[key(p, b)].append(p)

    refs = defaultdict(lambda: defaultdict(set))  # (kind, path) -> jar -> {where}
    jars = sorted(f for f in os.listdir(corpus) if f.endswith(".jar"))
    for jar in jars:
        try:
            z = zipfile.ZipFile(os.path.join(corpus, jar))
        except zipfile.BadZipFile:
            continue
        names = z.namelist()
        own = set()
        for n in names:
            if n.startswith("assets/") and n.count("/") >= 2:
                ns = n.split("/")[1]
                own.add(n[len("assets/" + ns + "/"):])
        for n in names:
            if n.endswith(".class"):
                strs, frefs = class_consts(z.read(n))
                for owner, name, desc in frefs:
                    p = vfields.get(name) if desc == "Lnet/minecraft/class_2960;" else None
                    if p and p in old:
                        refs[("vanilla-field", p)][jar].add(n)
                for s in strs:
                    m = TEX_IN_CODE.match(s)
                    if not m:
                        continue
                    p = m.group(2)
                    # unprefixed: the mod's own when it ships the file under any namespace
                    if not m.group(1) and p in own:
                        continue
                    if p in old:
                        refs[("code-texture", p)][jar].add(n)
            elif n.startswith("assets/") and n.endswith(".json"):
                for kind, p in json_refs(n, z.read(n)):
                    if p in old:
                        refs[(kind, p)][jar].add(n)
        z.close()

    rows = []
    changed_rows = []
    missing_jars = defaultdict(set)
    kinds = Counter()
    kinds_missing = Counter()
    kinds_changed = Counter()
    for (kind, p), by_jar in sorted(refs.items()):
        kinds[kind] += 1
        b = old[p]
        size = png_size(b)
        if p in new:
            if key(p, b) == key(p, new[p]):
                continue
            # still there, other pixels: what a mod blits out of it may be gone
            kinds_changed[kind] += 1
            changed_rows.append((kind, p, len(by_jar), "changed-in-place", "%dx%d" % size if size else "",
                                 ",".join(sorted(by_jar))[:300]))
            continue
        kinds_missing[kind] += 1
        same = new_by_md5.get(key(p, b), [])
        rel = "rename:" + ",".join(sorted(same)) if same else "none"
        rows.append((kind, p, len(by_jar), rel, "%dx%d" % size if size else "", ",".join(sorted(by_jar))[:300]))
        for j in by_jar:
            missing_jars[j].add(p)
    rows.sort(key=lambda r: -r[2])
    changed_rows.sort(key=lambda r: -r[2])
    with open(out, "w", encoding="utf-8") as f:
        f.write("kind\tpath\tjars\trelation\tsize\twho\n")
        for r in rows + changed_rows:
            f.write("\t".join(str(x) for x in r) + "\n")
    print(f"corpus jars: {len(jars)}")
    print(f"distinct vanilla references: {sum(kinds.values())}  missing in target: {sum(kinds_missing.values())}")
    for k in sorted(kinds):
        print(f"  {k:18} {kinds[k]:5} referenced, {kinds_missing[k]:4} missing, {kinds_changed[k]:4} changed in place")
    print(f"jars with a missing vanilla asset: {len(missing_jars)}")
    print(f"missing with an identical file elsewhere: {sum(1 for r in rows if r[3] != 'none')}")
    print("changed in place:")
    for r in changed_rows[:20]:
        print(f"  {r[2]:3} {r[0]:16} {r[1]}  {r[4]}")
    print("top missing:")
    for r in rows[:25]:
        print(f"  {r[2]:3} {r[0]:16} {r[1]}  {r[3]}  {r[4]}")


if __name__ == "__main__":
    main()
