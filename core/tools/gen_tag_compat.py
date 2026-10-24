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
  2. for each compat tag, a tagMap entry in the fragment below ("<registry> minecraft:<tag>" -> {"to": compat id,
     "mirror": widened/changed/removed}): the resource translator (engine/res/LegacyTags) rewrites a converted
     mod's data naming the vanilla tag to the compat tag, and with mirror copies the mod's own file for the vanilla
     tag into it (the compat tag names no #minecraft:<tag> there, so mod additions would be lost otherwise).
  3. for each such row with a V TagKey constant, a fieldGetter in <V>__<W>.json.d/tags-gen.json
     (shims-1.20.2.json.d/tags-gen.json for step 1) answering the constant with a TagKey of the compat
     tag (or the restored vanilla id), a static of rt/CbTagCompat in the layer (generated, one class per layer). A key some other
     fragment of the step already wires is left to it (listed on stdout).

  4. for a V TagKey constant whose field W dropped while W keeps the tag under another holder (26.2 moved
     BlockTags.SAPLINGS to BlockItemTags.SAPLINGS.block()), a fieldGetter answering with W's holder, found and
     checked by evaluating W's tag classes' <clinit> bytecode (same registry, same id).

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
        if "expr" in c:
            L[-1] = f"    /** {c['owner']}.{c['field']}: {v} minecraft:{c['tag']} ({c['kind']} at {w}: {c['where']}) */"
            L.append(f"    public static final {tk}<?> {c['name']} = {c['expr']};")
        else:
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



def step_names(v, w, wiring, data):
    """(V class -> W class, renamed fields) of the step: the wall table at the namespace wall (fields as
    "owner.field" -> (W owner, W name)), else the step wiring's classRenames and fieldRenames
    ("owner.field:desc" -> new name)"""
    cmap, fields = {}, {}
    if wall(v, w):
        import wall_tables
        for line in open(wall_tables.table_for(w, data), encoding="utf-8"):
            f = line.rstrip("\n").split("\t")
            if len(f) < 3:
                continue
            if f[0] == "C":
                cmap[f[1]] = f[2]
            elif f[0] == "F":
                o, n = f[2].rsplit(".", 1)
                fields[f[1]] = (o, n)
        return cmap, fields
    files = [wiring] if os.path.exists(wiring) else []
    if os.path.isdir(wiring + ".d"):
        files += [os.path.join(wiring + ".d", n) for n in sorted(os.listdir(wiring + ".d")) if n.endswith(".json")]
    for p in files:
        doc = json.load(open(p, encoding="utf-8"))
        cmap.update(doc.get("classRenames", {}))
        fields.update(doc.get("fieldRenames", {}))
    return cmap, fields


def field_refs(b):
    """(owner, name) of every Fieldref in a class file's constant pool"""
    import struct
    n = struct.unpack_from(">H", b, 8)[0]
    cp, i, p = [None] * n, 1, 10
    while i < n:
        t = b[p]
        if t == 1:
            ln = struct.unpack_from(">H", b, p + 1)[0]
            cp[i] = b[p + 3:p + 3 + ln].decode("utf-8", "replace")
            p += 3 + ln
        elif t in (3, 4):
            p += 5
        elif t in (5, 6):
            p += 9
            i += 1
        elif t in (7, 8, 16, 19, 20):
            cp[i] = (t, struct.unpack_from(">H", b, p + 1)[0])
            p += 3
        elif t in (9, 10, 11, 12, 17, 18):
            cp[i] = (t,) + struct.unpack_from(">HH", b, p + 1)
            p += 5
        elif t == 15:
            p += 4
        else:
            raise ValueError(f"constant tag {t}")
        i += 1
    out = set()
    for e in cp:
        if isinstance(e, tuple) and e[0] == 9:
            out.add((cp[cp[e[1]][1]], cp[cp[e[2]][1]]))
    return out


def readers(data, version, owner, field, cache):
    """classes of version's code (the owner and data generators aside) naming owner.field"""
    import zipfile
    if version not in cache:
        idx = {}
        with zipfile.ZipFile(tag_drift.code_jar(data, version)) as zf:
            for n in zf.namelist():
                if not n.endswith(".class") or n.startswith("net/minecraft/data/"):
                    continue
                b = zf.read(n)
                for ref in field_refs(b):
                    idx.setdefault(ref, set()).add(n[:-6])
        cache[version] = idx
    return {c for c in cache[version].get((owner, field), set()) if c != owner}


def successors(V, W, d, cmap, cache):
    """(rename, partial successors) of a tag W removed. The rename: the one W tag holding exactly V's members
    whose readers in W's code are V's readers of the dropped constant (through the step's class names; data
    generators aside) -- vanilla acts on it where it acted on the old tag, so mod data must reach it. Partial
    successors: W tags holding exactly V's members that W introduced or that a non-empty part of those readers
    reads (26.1 cauldron_can_remove_dye, read only by the cauldron, of 1.21.11 dyeable): mod additions are mirrored into
    them so that part keeps acting on them. A tag with no V constant cannot be judged and is neither."""
    reg, _ = d["key"]
    vread = set()
    for owner, field in d["fields"]:
        vread |= {cmap.get(c, c) for c in readers(V.data, V.v, owner, field, cache)}
    vread = {c for c in vread if not c.startswith("net/minecraft/data/")}
    if not vread:
        return None, []
    equal, part = [], []
    for c in d["exact"]:
        wread = set()
        for owner, field in W.fields().raw.get((reg, c), []):
            wread |= readers(W.data, W.v, owner, field, cache)
        wread = {x for x in wread if not x.startswith("net/minecraft/data/")}
        if wread and wread == vread:
            equal.append(c)
        elif wread and (wread < vread or (reg, c) not in V.tags):
            # read by part of the old readers, or a tag W introduced with exactly the old members (the reading
            # code itself may have moved: 26.1 CauldronInteractions reads cauldron_can_remove_dye)
            part.append(c)
    if len(d["exact"]) == 1 and len(equal) == 1:
        return equal[0], []
    return None, sorted(part + equal)


def carrier(W, d):
    """the W tag that carries a narrowed tag's V meaning: the one W tag holding exactly V's members that names
    #minecraft:<tag> itself (26.1 substrate_overworld for 1.21.11 dirt), so what mods add to the vanilla tag
    reaches it too; None when there is not exactly one"""
    reg, tag = d["key"]
    hits = [c for c in d["exact"] if any(ref == tag and is_tag for ref, is_tag, _ in W.tags.get((reg, c), []))]
    return hits[0] if len(hits) == 1 else None


def renamed_field_handled(v, w, owner, field, W, cmap, field_renames, key):
    """whether a V tag constant already reaches a W constant: through the wall table (when the name
    it gives exists in W) or the step's fieldRenames"""
    if wall(v, w):
        t = field_renames.get(f"{owner}.{field}")
        if not t:
            return False
        cf = W.fields()._class(t[0])
        return cf is not None and any(fn == t[1] for _, fn, _, _ in cf.fields)
    return key in field_renames


def wall(v, w):
    """the namespace wall: V's intermediary names are not W's, the death tables and swall own it"""
    return tag_drift.year_era(w) and not tag_drift.year_era(v)


def moved_fields(V, W, drifted):
    """V TagKey constants whose field W no longer declares while W still has the tag under another holder:
    [(registry, tag, owner, field, java expression, holder)]. The holder is found by evaluating W's tag
    classes (tag_drift.TagFields: the <clinit> bytecode), so it answers the same registry and id -- a static
    TagKey, or a component of a record of TagKeys (26.2 BlockItemTags.SAPLINGS.block())."""
    vf, wf = V.fields(), W.fields()
    found = {}
    for (owner, field), val in wf.values.items():
        if owner == "<done>" or not val:
            continue
        java_owner = owner.replace("/", ".").replace("$", ".")
        if val[0] == "tag" and val[1]:
            found.setdefault((val[1], val[2]), (f"{java_owner}.{field}", f"{owner.rsplit('/', 1)[1]}.{field}"))
        elif val[0] == "obj":
            rec = wf._class(val[1])
            for comp, cv in val[2].items():
                if not (cv and cv[0] == "tag" and cv[1]):
                    continue
                if rec and any(mn == comp and md == f"()L{wf.tagkey};" for _, mn, md, _ in rec.methods):
                    found.setdefault((cv[1], cv[2]), (f"{java_owner}.{field}.{comp}()",
                                                     f"{owner.rsplit('/', 1)[1]}.{field}.{comp}()"))
    out = []
    for key, pairs in sorted(vf.raw.items()):
        if key in drifted:
            continue
        for owner, field in pairs:
            cf = wf._class(owner)
            if cf is None or any(fn == field for _, fn, _, _ in cf.fields):
                continue
            if key in found:
                expr, where = found[key]
                out.append((key[0], key[1], owner, field, expr, where))
            else:
                print(f"warning: {owner}.{field} (#{key[1]}) has no holder in {W.v}", file=sys.stderr)
    return out


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
    skipped_keys, skipped_files, summary, reused, decisions, handled = [], [], [], [], [], []
    readers_cache = {}
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
        moved = moved_fields(ver(v), ver(w), {d["key"] for d in rows}) if not wall(v, w) else []
        if not rows and not moved:
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
        consts, getters, ntags, tagmap = [], {}, 0, {}
        cmap, field_renames = step_names(v, w, wiring, a.data)
        for d in rows:
            reg, tag = d["key"]
            m = ren.get(reg, {})
            ids = sorted({m.get(i, i) for i in d["vset"]})
            values = [{"id": ns(i), "required": False} for i in ids]
            later = [t for t in rel[rel.index(w) + 1:] if d["key"] in present(t)]
            regdir = LEGACY_DIRS.get(reg, reg) if Release_before(w, "1.21") else reg
            mapkey = f"{reg} minecraft:{tag}"
            # what carries V's meaning on W: a vanilla W tag (rename / carrier), the restored id, or the compat tag
            successor, partial, why = None, [], ""
            if d["kind"] == "removed":
                successor, partial = successors(ver(v), ver(w), d, cmap, readers_cache)
                why = "renamed" if successor else ""
            elif d["kind"] == "narrowed":
                successor = carrier(ver(w), d)
                why = "carried" if successor else ""
            restore = d["kind"] == "removed" and not successor and not later
            written, relpath = False, "-"
            if successor:
                key_ns, key_path = "minecraft", successor
                tagmap[mapkey] = {"to": f"minecraft:{successor}",
                                  "mirror": [f"minecraft:{successor}"] if why == "renamed" else []}
                decisions.append(f"{v}__{w}\t{mapkey}\t{why} -> minecraft:{successor}")
            else:
                refs = []
                if d["kind"] == "narrowed":
                    refs = [f"#minecraft:{tag}"]
                elif d["kind"] == "removed" and not restore:
                    # a later release ships the id again with its own meaning: #minecraft:<tag> would bring that in
                    refs = [f"#minecraft:{s}" for s in d["new_successors"]]
                    reused.append(f"{v}__{w}\t{reg}\tminecraft:{tag}\tvanilla again from {later[0]}")
                values = [{"id": r, "required": False} for r in refs] + values
                if restore:
                    key_ns, key_path = "minecraft", tag
                    rp = f"data/minecraft/tags/{regdir}/{tag}.json"
                    if partial:
                        tagmap[mapkey] = {"to": f"minecraft:{tag}", "mirror": [f"minecraft:{p}" for p in partial]}
                        decisions.append(f"{v}__{w}\t{mapkey}\trestored, mod additions also into "
                                         + ",".join(f"minecraft:{p}" for p in partial))
                else:
                    key_ns, key_path = "centurybridge", f"compat/{us(v)}/{tag}"
                    rp = f"data/centurybridge/tags/{regdir}/compat/{us(v)}/{tag}.json"
                    mirror = [f"centurybridge:{key_path}"] if d["kind"] in ("widened", "changed", "removed") else []
                    tagmap[mapkey] = {"to": f"centurybridge:{key_path}",
                                      "mirror": mirror + [f"minecraft:{p}" for p in partial]}
                path = os.path.join(res, *rp.split("/"))
                relpath = os.path.relpath(path, ROOT).replace(os.sep, "/")
                written = not os.path.exists(path)
                if not written:
                    skipped_files.append(f"{v}__{w}\t{relpath}")
                else:
                    os.makedirs(os.path.dirname(path), exist_ok=True)
                    with open(path, "w", encoding="utf-8", newline="\n") as f:
                        json.dump({"replace": False, "values": values} if restore else {"values": values}, f, indent=2)
                        f.write("\n")
                    ntags += 1
            getter_names = []
            for owner, field in d["fields"]:
                key = f"{owner}.{field}:L{tagkey};"
                if f'"{key}"' in other:
                    skipped_keys.append(f"{v}__{w}\t{key}\tminecraft:{tag}\t{d['kind']}")
                    continue
                if d["kind"] == "removed" and renamed_field_handled(v, w, owner, field, ver(w), cmap, field_renames, key):
                    # W dropped the constant and the wall table / the step's fieldRenames already sends it to one
                    # W declares (a rename's successor, 26.1 BlockTags.SUPPORTS_BAMBOO): left to it
                    handled.append(f"{v}__{w}	{key}	minecraft:{tag}	{d['kind']}")
                    continue
                if reg not in regfield:
                    raise SystemExit(f"{w} has no Registries key for {reg}")
                holder = holders.get(owner) or owner.rsplit("/", 1)[1]
                name = f"{holder}_{field}"
                consts.append({"owner": owner, "field": field, "tag": tag, "kind": d["kind"] + (f", {why}" if why else ""),
                               "registry": reg, "name": name, "ns": key_ns, "path": key_path})
                getters[key] = f"{pkg.replace('.', '/')}/CbTagCompat#{name}"
                getter_names.append(name)
            manifest.append((f"{v}__{w}", reg, tag, relpath if written else "-",
                             d["kind"] + (f" ({why} minecraft:{successor})" if successor else ""),
                             ",".join(getter_names) or "-"))
        for reg, tag, owner, field, expr, where in moved:
            key = f"{owner}.{field}:L{tagkey};"
            if f'"{key}"' in other:
                skipped_keys.append(f"{v}__{w}	{key}	minecraft:{tag}	moved")
                continue
            holder = holders.get(owner) or owner.rsplit("/", 1)[1]
            name = f"{holder}_{field}"
            consts.append({"owner": owner, "field": field, "tag": tag, "kind": "moved", "registry": reg,
                           "name": name, "expr": expr, "where": where})
            getters[key] = f"{pkg.replace('.', '/')}/CbTagCompat#{name}"
            manifest.append((f"{v}__{w}", reg, tag, "-", "moved", name))
        if consts:
            os.makedirs(os.path.dirname(java), exist_ok=True)
            with open(java, "w", encoding="utf-8", newline="\n") as f:
                f.write(java_class(v, w, pkg, consts, regfield))
        elif os.path.exists(java):
            os.remove(java)
        if getters or tagmap:
            doc = {}
            if getters:
                doc["fieldGetters"] = dict(sorted(getters.items()))
            if tagmap:
                doc["tagMap"] = dict(sorted(tagmap.items()))
            os.makedirs(os.path.dirname(frag), exist_ok=True)
            with open(frag, "w", encoding="utf-8", newline="\n") as f:
                json.dump(doc, f, indent=1)
                f.write("\n")
        elif os.path.exists(frag):
            os.remove(frag)
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
    for s in decisions:
        print("tag map:", s)
    for s in handled:
        print("field left to the wall table / fieldRenames:", s)


if __name__ == "__main__":
    main()
