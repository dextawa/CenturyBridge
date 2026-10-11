"""Measure the vanilla-asset references of a mod corpus against a target client.

A 1.20.1 mod names vanilla resources by path: texture identifiers in code
("textures/gui/widgets.png"), sprite and parent references in its model JSON
("minecraft:block/grass", "block/cube_all"), model references in its blockstates.
The target's vanilla resources were renamed, split and redrawn on the way; a path
the target no longer has renders as the missing texture or model, with nothing in
the log but a warning. This counts, per reference kind, which of those paths the
target lacks, and how each missing one relates to what the target has instead
(the same bytes under another name, or nothing found).

  python asset_refs.py <corpusDir> <oldClient.jar> <newClient.jar> <out.tsv>
"""
import hashlib
import json
import os
import re
import struct
import sys
import zipfile
from collections import Counter, defaultdict


def class_strings(b):
    """string constants (CONSTANT_String) of one class file"""
    if b[:4] != b"\xca\xfe\xba\xbe":
        return set()
    n = struct.unpack(">H", b[8:10])[0]
    i, idx = 10, 1
    utf8, strs = {}, []
    while idx < n:
        tag = b[i]
        if tag == 1:
            ln = struct.unpack(">H", b[i + 1:i + 3])[0]
            utf8[idx] = b[i + 3:i + 3 + ln]
            i += 3 + ln
        elif tag in (3, 4):
            i += 5
        elif tag in (5, 6):
            i += 9
            idx += 1
        elif tag in (7, 8, 16, 19, 20):
            if tag == 8:
                strs.append(struct.unpack(">H", b[i + 1:i + 3])[0])
            i += 3
        elif tag in (9, 10, 11, 12, 17, 18):
            i += 5
        elif tag == 15:
            i += 4
        else:
            return set()
        idx += 1
    return {utf8[s].decode("utf-8", "replace") for s in strs if s in utf8}


def assets_of(jar):
    """path -> bytes for every assets/minecraft/ entry of a client jar"""
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.startswith("assets/minecraft/") and not n.endswith("/"):
                out[n[len("assets/minecraft/"):]] = z.read(n)
    return out


def png_size(b):
    if b[:8] == b"\x89PNG\r\n\x1a\n":
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
    old, new = assets_of(old_jar), assets_of(new_jar)
    new_by_md5 = defaultdict(list)
    for p, b in new.items():
        new_by_md5[hashlib.md5(b).hexdigest()].append(p)

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
                for s in class_strings(z.read(n)):
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
    missing_jars = defaultdict(set)
    kinds = Counter()
    kinds_missing = Counter()
    for (kind, p), by_jar in sorted(refs.items()):
        kinds[kind] += 1
        if p in new:
            continue
        kinds_missing[kind] += 1
        b = old[p]
        same = new_by_md5.get(hashlib.md5(b).hexdigest(), [])
        size = png_size(b)
        rel = "rename:" + ",".join(sorted(same)) if same else "none"
        rows.append((kind, p, len(by_jar), rel, "%dx%d" % size if size else "", ",".join(sorted(by_jar))[:300]))
        for j in by_jar:
            missing_jars[j].add(p)
    rows.sort(key=lambda r: -r[2])
    with open(out, "w", encoding="utf-8") as f:
        f.write("kind\tpath\tjars\trelation\tsize\twho\n")
        for r in rows:
            f.write("\t".join(str(x) for x in r) + "\n")
    print(f"corpus jars: {len(jars)}")
    print(f"distinct vanilla references: {sum(kinds.values())}  missing in target: {sum(kinds_missing.values())}")
    for k in sorted(kinds):
        print(f"  {k:18} {kinds[k]:5} referenced, {kinds_missing[k]:4} missing")
    print(f"jars with a missing vanilla asset: {len(missing_jars)}")
    print(f"missing with an identical file elsewhere: {sum(1 for r in rows if r[3] != 'none')}")
    print("top missing:")
    for r in rows[:25]:
        print(f"  {r[2]:3} {r[0]:16} {r[1]}  {r[3]}  {r[4]}")


if __name__ == "__main__":
    main()
