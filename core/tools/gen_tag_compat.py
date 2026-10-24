"""Layer compatibility tags for vanilla data-tag drift (core/segments/damage-tags, tag_drift.py).

A V-era mod reading a vanilla tag W changed (BlockTags.MINEABLE_WITH_AXE lost every plant at 1.21.5;
26.1 narrowed minecraft:dirt) links fine and silently gets W's members. For every step V -> W and every
drift row that is not registry-only, this writes into that step's layer:

  1. a data tag holding V's exact resolved membership, ids renamed the way the game renamed them between
     V and W (core/segments/id-renames.json), every entry {"id": ..., "required": false}: the layer climbs
     on to later releases, where an id can disappear, and one required entry missing fails the whole tag.
     The registry directory is plural before 1.21, as W reads it.
     - A tag W removed is restored under its own id, data/minecraft/tags/<registry>/<tag>.json with
       "replace": false: nothing in W uses the id, so it cannot collide, and both the mod's TagKey and mod
       json naming #minecraft:<tag> resolve with no translation; data packs still add to it. When a later
       release ships the id again with its own meaning (1.21.4 removed item flowers, 1.21.5 has one), the
       climbing layer would merge V's members into that vanilla tag, so such a tag goes to the compat
       namespace instead (below), naming the successor W introduced holding exactly V's set when there
       is one (tag_drift's hint, only when the successor is new in W -- an older tag that happens to hold
       the same ids is not a successor).
     - A tag whose id still means something else on W goes to data/centurybridge/tags/<registry>/compat/
       <V_>/<tag>.json. A narrowed tag also names {"id": "#minecraft:<tag>", "required": false}, so what
       mods and data packs add to the vanilla tag still counts. A widened (or changed) tag names no
       vanilla tag: #minecraft:<tag> would bring W's new vanilla members back. What that loses is mod and
       data pack additions to the vanilla tag; carrying those over is the resource translator's (mod tag
       files mirrored into the compat tag on conversion).
  2. for each such row with a V TagKey constant, a fieldGetter in <V>__<W>.json.d/tags-gen.json
     (shims-1.20.2.json.d/tags-gen.json for step 1) answering the constant with a TagKey of the compat
     tag (or the restored vanilla id), a static of rt/CbTagCompat in the layer (generated, one class per layer). A key some other
     fragment of the step already wires is left to it (listed on stdout).

Compat tags live in the layer's own namespace, so drift of the vanilla tag at later steps never changes
them; their ids are renamed on the way up by the resource translator like any mod's tag file.

The generator owns exactly the files listed in core/segments/damage-tags/compat-gen.tsv (its manifest):
a tag file at a path it does not own (a hand-written pilot, another agent's restored tag) is left alone
and listed.

  python3 core/tools/gen_tag_compat.py [--data data]
"""
import argparse
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import tag_drift  # noqa: E402

ROOT = tag_drift.ROOT
MANIFEST = os.path.join(ROOT, "core", "segments", "damage-tags", "compat-gen.tsv")
LEGACY_DIRS = {"block": "blocks", "item": "items", "entity_type": "entity_types", "fluid": "fluids",
               "game_event": "game_events"}
RENAME_FIX_REGISTRY = {"BlockRenameFix": "block", "ItemRenameFix": "item", "RenameEnchantmentsFix": "enchantment"}


def us(v):
    return v.replace(".", "_")


def layer_paths(v, w):
    """(layer source root, layer resources, rt package, tags-gen fragment, wiring files of the step)"""
    if v == "1.20.1" and w == "1.20.2":
        base = os.path.join(ROOT, "core", "shims", "1.20.2")
        pkg = "top.dext.centurybridge.rt.v1_20_2"
        wiring = os.path.join(ROOT, "core", "segments", "shims-1.20.2.json")
    else:
        base = os.path.join(ROOT, "core", "shims", "steps", w)
        pkg = f"top.dext.centurybridge.step.v{us(w)}.rt"
        wiring = os.path.join(ROOT, "core", "segments", "steps", f"{v}__{w}.json")
    return base, os.path.join(base, "resources"), pkg, os.path.join(wiring + ".d", "tags-gen.json"), wiring


def renames(v, w):
    """{registry: {old id: new id}} for the game's id renames between V and W"""
    lo, hi = tag_drift_dv(v), tag_drift_dv(w)
    out = {}
    doc = json.load(open(os.path.join(ROOT, "core", "segments", "id-renames.json"), encoding="utf-8"))
    for f in sorted(doc["fixes"], key=lambda f: f["dataVersion"]):
        reg = RENAME_FIX_REGISTRY.get(f["fix"])
        if reg and lo < f["dataVersion"] <= hi:
            m = out.setdefault(reg, {})
            for a, b in f["renames"].items():
                a, b = tag_drift.strip_ns(a), tag_drift.strip_ns(b)
                for k, val in list(m.items()):
                    if val == a:
                        m[k] = b
                m[a] = b
    return out


def tag_drift_dv(v):
    """v's data version (Release.DATA_VERSIONS), the clock id-renames.json counts by"""
    src = open(os.path.join(ROOT, "core", "src", "top", "dext", "centurybridge", "res", "Release.java"),
               encoding="utf-8").read()
    body = src[src.index("DATA_VERSIONS = List.of("):]
    nums = [int(x) for x in body[:body.index(");")].replace("\n", " ").split("(", 1)[1].replace(" ", "").split(",") if x]
    return nums[tag_drift.releases().index(v)]


def wired_elsewhere(wiring, own):
    """the text of every wiring file of the step except our own fragment"""
    texts = []
    if os.path.exists(wiring):
        texts.append(open(wiring, encoding="utf-8").read())
    d = wiring + ".d"
    if os.path.isdir(d):
        for n in sorted(os.listdir(d)):
            p = os.path.join(d, n)
            if n.endswith(".json") and os.path.abspath(p) != os.path.abspath(own):
                texts.append(open(p, encoding="utf-8").read())
    return "\n".join(texts)


def ns(i):
    return i if ":" in i else "minecraft:" + i


def java_class(v, w, pkg, consts, regfield):
    year = tag_drift.year_era(w)
    if year:
        tk, reg_owner = "net.minecraft.tags.TagKey", "net.minecraft.core.registries.Registries"
        rk, regcls = "net.minecraft.resources.ResourceKey", "net.minecraft.core.Registry"
        ident = 'net.minecraft.resources.Identifier.fromNamespaceAndPath(namespace, path)'
        create = "net.minecraft.tags.TagKey.create"
    else:
        tk, reg_owner = "net.minecraft.class_6862", "net.minecraft.class_7924"
        rk, regcls = "net.minecraft.class_5321", "net.minecraft.class_2378"
        ident = ('new net.minecraft.class_2960(namespace, path)' if Release_before(w, "1.21")
                 else 'net.minecraft.class_2960.method_60655(namespace, path)')
        create = "net.minecraft.class_6862.method_40092"
    L = [f"package {pkg};", "",
         "/**",
         " * Generated by core/tools/gen_tag_compat.py -- do not edit; regenerate with the command in its header.",
         " *",
         f" * {v} TagKey constants whose vanilla tag {w} changed (core/segments/damage-tags/{v}__{w}.tsv): each answers",
         f" * with a tag of this layer holding {v}'s members -- centurybridge:compat/{us(v)}/&lt;tag&gt; where the id still",
         f" * means something else, the vanilla id itself where {w} removed the tag and no later release ships it again.",
         " */",
         "@SuppressWarnings({\"rawtypes\", \"unchecked\"})",
         "public final class CbTagCompat {", "",
         "    private CbTagCompat() {",
         "    }", "",
         f"    private static <T> {tk}<T> key({rk}<? extends {regcls}<T>> registry, String namespace, String path) {{",
         f"        return {create}(registry, {ident});",
         "    }", ""]
    for c in consts:
        L.append(f"    /** {c['owner']}.{c['field']}: {v} minecraft:{c['tag']} ({c['kind']} at {w}) */")
        L.append(f"    public static final {tk}<?> {c['name']} = key({reg_owner}.{regfield[c['registry']]}, "
                 f"\"{c['ns']}\", \"{c['path']}\");")
        L.append("")
        L.append(f"    public static {tk}<?> {c['name']}() {{")
        L.append(f"        return {c['name']};")
        L.append("    }")
        L.append("")
    L[-1] = "}"
    return "\n".join(L) + "\n"


def Release_before(a, b):
    rel = tag_drift.releases()
    return rel.index(a) < rel.index(b)



def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", default=os.path.join(ROOT, "data"))
    a = ap.parse_args()
    rel = tag_drift.releases()
    owned = set()
    if os.path.exists(MANIFEST):
        for line in open(MANIFEST, encoding="utf-8"):
            if line.startswith("#") or line.startswith("step\t"):
                continue
            f = line.rstrip("\n").split("\t")
            if len(f) >= 4 and f[3] != "-":
                owned.add(f[3])
    # remove what the previous run wrote, so a row that stopped drifting leaves no file behind
    for rp in owned:
        p = os.path.join(ROOT, rp)
        if os.path.exists(p):
            os.remove(p)
    manifest = []
    skipped_keys, skipped_files, summary, reused = [], [], [], []
    cache = {}
    tagsets = {}

    def present(t):
        """the vanilla tags release t ships"""
        if t not in tagsets:
            import zipfile
            with zipfile.ZipFile(tag_drift.data_jar(a.data, t)) as zf:
                tagsets[t] = set(tag_drift.read_tags(zf))
        return tagsets[t]

    def ver(v):
        if v not in cache:
            cache[v] = tag_drift.Version(a.data, v)
        return cache[v]

    for v, w in zip(rel, rel[1:]):
        details = []
        tag_drift.compare(ver(v), ver(w), details)
        rows = [d for d in details if d["kind"] != "registry-only"]
        base, res, pkg, frag, wiring = layer_paths(v, w)
        java = os.path.join(base, "src", *pkg.split("."), "CbTagCompat.java")
        if not rows:
            for p in (frag, java):
                if os.path.exists(p):
                    os.remove(p)
            cache.pop(v, None)
            continue
        ren = renames(v, w)
        other = wired_elsewhere(wiring, frag)
        tagkey = ver(v).fields().tagkey
        holders = ver(v).fields().mojang
        regfield = {r: f for f, r in ver(w).fields().reg_ids.items()}
        consts, getters, ntags = [], {}, 0
        for d in rows:
            reg, tag = d["key"]
            m = ren.get(reg, {})
            ids = sorted({m.get(i, i) for i in d["vset"]})
            values = [{"id": ns(i), "required": False} for i in ids]
            later = [t for t in rel[rel.index(w) + 1:] if d["key"] in present(t)]
            restore = d["kind"] == "removed" and not later
            refs = []
            if d["kind"] == "narrowed":
                refs = [f"#minecraft:{tag}"]
            elif d["kind"] == "removed" and not restore:
                # a later release ships the id again with its own meaning: #minecraft:<tag> would bring that in
                refs = [f"#minecraft:{s}" for s in d["new_successors"]]
                reused.append(f"{v}__{w}	{reg}	minecraft:{tag}	vanilla again from {later[0]}")
            values = [{"id": r, "required": False} for r in refs] + values
            regdir = LEGACY_DIRS.get(reg, reg) if Release_before(w, "1.21") else reg
            if restore:
                key_ns, key_path = "minecraft", tag
                rp = f"data/minecraft/tags/{regdir}/{tag}.json"
            else:
                key_ns, key_path = "centurybridge", f"compat/{us(v)}/{tag}"
                rp = f"data/centurybridge/tags/{regdir}/compat/{us(v)}/{tag}.json"
            path = os.path.join(res, *rp.split("/"))
            relpath = os.path.relpath(path, ROOT).replace(os.sep, "/")
            getter_names = []
            written = not os.path.exists(path)
            if not written:
                skipped_files.append(f"{v}__{w}\t{relpath}")
            else:
                os.makedirs(os.path.dirname(path), exist_ok=True)
                with open(path, "w", encoding="utf-8", newline="\n") as f:
                    json.dump({"replace": False, "values": values} if restore else {"values": values}, f, indent=2)
                    f.write("\n")
                ntags += 1
            for owner, field in d["fields"]:
                key = f"{owner}.{field}:L{tagkey};"
                if f'"{key}"' in other:
                    skipped_keys.append(f"{v}__{w}\t{key}\tminecraft:{tag}\t{d['kind']}")
                    continue
                if reg not in regfield:
                    raise SystemExit(f"{w} has no Registries key for {reg}")
                holder = holders.get(owner) or owner.rsplit("/", 1)[1]
                name = f"{holder}_{field}"
                consts.append({"owner": owner, "field": field, "tag": tag, "kind": d["kind"],
                               "registry": reg, "name": name, "ns": key_ns, "path": key_path})
                getters[key] = f"{pkg.replace('.', '/')}/CbTagCompat#{name}"
                getter_names.append(name)
            manifest.append((f"{v}__{w}", reg, tag, relpath if written else "-",
                             d["kind"], ",".join(getter_names) or "-"))
        if consts:
            os.makedirs(os.path.dirname(java), exist_ok=True)
            with open(java, "w", encoding="utf-8", newline="\n") as f:
                f.write(java_class(v, w, pkg, consts, regfield))
            os.makedirs(os.path.dirname(frag), exist_ok=True)
            with open(frag, "w", encoding="utf-8", newline="\n") as f:
                json.dump({"fieldGetters": dict(sorted(getters.items()))}, f, indent=1)
                f.write("\n")
        else:
            for p in (frag, java):
                if os.path.exists(p):
                    os.remove(p)
        summary.append((f"{v}__{w}", len(rows), ntags, len(getters)))
        print(f"{v}__{w}: drift rows {len(rows)}, compat tags {ntags}, getters {len(getters)}")
        cache.pop(v, None)
    with open(MANIFEST, "w", encoding="utf-8", newline="\n") as f:
        f.write("# compat tags and getters written by core/tools/gen_tag_compat.py (its manifest: the files it owns)\n")
        f.write("step\tregistry\ttag\tcompat tag file\tkind\tCbTagCompat getters\n")
        for r in manifest:
            f.write("\t".join(r) + "\n")
    for s in skipped_keys:
        print("left to another fragment:", s)
    for s in skipped_files:
        print("tag file not owned, left alone:", s)
    for s in reused:
        print("removed tag in the compat namespace (id vanilla again later):", s)


if __name__ == "__main__":
    main()
