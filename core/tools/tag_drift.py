"""Vanilla data-tag drift per climb step: tags whose membership changed between V and W.

A tag changing members links fine (BlockTags.DIRT is still a TagKey, "#minecraft:dirt" still parses),
so neither the API probe nor generic_drift can see it; a V-era mod reading the tag simply gets W's
meaning. 26.1 narrowed minecraft:dirt to dirt/coarse_dirt/rooted_dirt, and 1.21.11's DIRT set is
26.1's minecraft:substrate_overworld.

For each step V -> W of Release.ALL (read from core/src/top/dext/centurybridge/res/Release.java):
  1. reads every data/minecraft/tags/**.json of V and W from the game jars (client-<v>-obf.jar for the
     intermediary era, client-<v>.jar for 26.x). Built-in feature packs (data/minecraft/datapacks/<pack>,
     off by default) are not compared; V's are read only to recognise content V had behind an
     experimental flag that W released (step 4). Pre-1.21 plural directories (blocks, items,
     entity_types, fluids, game_events) are normalized to their registry ids.
  2. resolves each tag fully (#references, optional entries) to a set of registry ids.
  3. for tags in both, lists removed and added ids; lists tags W removed; for removed and narrowed
     tags names the W tag(s) whose set equals V's set, or V's set minus ids W's registry dropped.
  4. marks ids that changed only because the registry itself added or removed them (new content,
     not drift of V's meaning): those ids carry a leading '*'; an added id V's tag already held with
     V's feature packs enabled (experimental in V, released in W: 1.21's copper/tuff in 1.20.x's
     update_1_21 pack, the pale garden in 1.21.3's winter_drop) carries '~'. A row whose every
     changed id is '*' or '~' is kind registry-only.
  5. names the V field that holds the TagKey (BlockTags, ItemTags, ... in net/minecraft/tags, read
     from the <clinit> bytecode of V's code jar: intermediary owner.field plus the 1.21.11 Mojang
     name where it has one; Mojang names for 26.x V), or "-" when the tag is reachable from json only.

Registry contents (for step 4 and optional entries): block = assets blockstates; item = assets items/
(1.21.4+) or models/item (earlier); entity_type = en_us "entity.minecraft.<id>" keys; a data-driven
registry = data/minecraft/<registry>/**.json. A registry without such a source in a version (fluid,
game_event, point_of_interest_type, potion, pre-data-driven banner_pattern/cat_variant/instrument/
painting_variant, ...) is taken on that side to hold every id, so a registry change is claimed only
where the jar shows it, never guessed. The header of each output lists those registries and sides.

  python3 core/tools/tag_drift.py [--data data] [--out core/segments/damage-tags] [--step V W]
  python3 core/tools/tag_drift.py --calibrate      (V against V for every release: must be 0 rows)

Outputs core/segments/damage-tags/<V>__<W>.tsv and core/segments/damage-tags/summary.tsv.
"""
import argparse
import json
import os
import re
import struct
import sys
import zipfile
from collections import defaultdict

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.normpath(os.path.join(HERE, "..", ".."))

PLURAL = {"blocks": "block", "items": "item", "entity_types": "entity_type", "fluids": "fluid",
          "game_events": "game_event", "functions": "function"}
KINDS = ("narrowed", "widened", "changed", "removed", "registry-only")


def releases():
    src = open(os.path.join(ROOT, "core", "src", "top", "dext", "centurybridge", "res", "Release.java"),
               encoding="utf-8").read()
    body = src[src.index("ALL = List.of("):]
    body = body[:body.index(");")]
    body = re.sub(r"//[^\n]*", "", body)
    return re.findall(r'"([^"]+)"', body)


def year_era(v):
    return not v.startswith("1.")


def data_jar(data, v):
    p = os.path.join(data, "jars", f"client-{v}.jar" if year_era(v) else f"client-{v}-obf.jar")
    if not os.path.exists(p):
        raise SystemExit(f"no data jar for {v}: {p}")
    return p


def code_jar(data, v):
    p = os.path.join(data, "jars", f"client-{v}.jar" if year_era(v) else f"client-{v}-intermediary-code.jar")
    if not os.path.exists(p):
        raise SystemExit(f"no code jar for {v}: {p}")
    return p


def strip_ns(s):
    return s[len("minecraft:"):] if s.startswith("minecraft:") else s


# ---------------------------------------------------------------- tags

TAG_PREFIX = "data/minecraft/tags/"


def split_registry(rel):
    """'worldgen/biome/has_structure/x' -> ('worldgen/biome', 'has_structure/x')"""
    parts = rel.split("/")
    n = 2 if parts[0] == "worldgen" else 1
    reg = "/".join(parts[:n])
    return PLURAL.get(reg, reg), "/".join(parts[n:])


def read_tags(zf, prefix=TAG_PREFIX):
    """{(registry, tag): [entries]}, an entry being (ref, is_tag, required); a feature pack's
    (prefix other than the base one) as {(registry, tag): (replace, [entries])}"""
    tags = {}
    for name in zf.namelist():
        if not name.startswith(prefix) or not name.endswith(".json"):
            continue
        reg, tag = split_registry(name[len(prefix):-len(".json")])
        doc = json.loads(zf.read(name).decode("utf-8"))
        entries = []
        for e in doc.get("values", []):
            if isinstance(e, dict):
                ref, req = e["id"], e.get("required", True)
            else:
                ref, req = e, True
            is_tag = ref.startswith("#")
            entries.append((strip_ns(ref[1:] if is_tag else ref), is_tag, req))
        tags[(reg, tag)] = (doc.get("replace", False), entries) if prefix != TAG_PREFIX else entries
    return tags


PACK_RE = re.compile(r"^data/minecraft/datapacks/([^/]+)/data/minecraft/tags/")


def with_feature_packs(zf, base):
    """base tags with every built-in feature pack (data/minecraft/datapacks/<pack>) applied on top,
    as enabling all of them would load them: a pack's file appends to the tag unless it replaces it"""
    packs = sorted({m.group(1) for m in map(PACK_RE.match, zf.namelist()) if m})
    merged = {k: list(v) for k, v in base.items()}
    for pack in packs:
        for key, (replace, entries) in read_tags(zf, f"data/minecraft/datapacks/{pack}/data/minecraft/tags/").items():
            merged[key] = list(entries) if replace or key not in merged else merged[key] + entries
    return packs, merged


def resolve_all(tags, contents):
    """{(registry, tag): frozenset(ids)}; an optional id counts only when the registry has it"""
    out = {}

    def resolve(key, stack):
        if key in out:
            return out[key]
        if key in stack:
            raise SystemExit(f"tag cycle through {key}")
        reg = key[0]
        known = contents.get(reg)
        s = set()
        for ref, is_tag, req in tags[key]:
            if is_tag:
                sub = (reg, ref)
                if sub in tags:
                    s |= resolve(sub, stack | {key})
                elif req:
                    raise SystemExit(f"{key} names missing required tag #{ref}")
            elif req or known is None or ref in known:
                s.add(ref)
        out[key] = frozenset(s)
        return out[key]

    for k in tags:
        resolve(k, frozenset())
    return out


# ---------------------------------------------------------------- registry contents

# registries defined in code: the class whose constants name every id (Mojang names; intermediary
# through data/mappings/inter-to-mojang-classes.tsv). Read only when the jar has no data directory
# for the registry; its string constants are a superset of the ids, enough to tell present from gone.
CODE_HOLDERS = {
    "game_event": ["net/minecraft/world/level/gameevent/GameEvent"],
    "fluid": ["net/minecraft/world/level/material/Fluids", "net/minecraft/world/level/material/FluidIds"],
    "enchantment": ["net/minecraft/world/item/enchantment/Enchantments"],
    "point_of_interest_type": ["net/minecraft/world/entity/ai/village/poi/PoiTypes"],
    "potion": ["net/minecraft/world/item/alchemy/Potions", "net/minecraft/world/item/alchemy/PotionIds"],
    "cat_variant": ["net/minecraft/world/entity/animal/feline/CatVariant",
                    "net/minecraft/world/entity/animal/feline/CatVariants"],
    "painting_variant": ["net/minecraft/world/entity/decoration/painting/PaintingVariants"],
    "instrument": ["net/minecraft/world/item/Instruments"],
    "banner_pattern": ["net/minecraft/world/level/block/entity/BannerPatterns"],
}
ID_RE = re.compile(r"^(?:minecraft:)?[a-z0-9_./-]+$")
_MOJANG_TO_INTER = {}


def mojang_to_inter(data):
    if not _MOJANG_TO_INTER:
        for line in open(os.path.join(data, "mappings", "inter-to-mojang-classes.tsv"), encoding="utf-8"):
            f = line.rstrip("\n").split("\t")
            if len(f) >= 3 and f[0] == "C":
                _MOJANG_TO_INTER[f[2]] = f[1]
    return _MOJANG_TO_INTER


def code_registry_ids(data, v, reg):
    """the string constants of reg's holder classes in v's code jar, or None without a holder"""
    holders = CODE_HOLDERS.get(reg)
    if not holders:
        return None
    ids = set()
    found = False
    with zipfile.ZipFile(code_jar(data, v)) as zf:
        for h in holders:
            name = h if year_era(v) else mojang_to_inter(data).get(h)
            if not name or f"{name}.class" not in zf.namelist():
                continue
            found = True
            cf = ClassFile(zf.read(f"{name}.class"))
            for e in cf.cp:
                if e and e[0] == 8:
                    sv = cf.utf(e[1])
                    if ID_RE.match(sv):
                        ids.add(strip_ns(sv))
    return ids if found else None


def registry_contents(zf, registries):
    """{registry: set(ids)} for registries with a real source in this jar; others absent"""
    names = zf.namelist()
    out = {}
    blocks = {n[len("assets/minecraft/blockstates/"):-5] for n in names
              if n.startswith("assets/minecraft/blockstates/") and n.endswith(".json")}
    if blocks:
        out["block"] = blocks
    items = {n[len("assets/minecraft/items/"):-5] for n in names
             if n.startswith("assets/minecraft/items/") and n.endswith(".json")}
    if not items:
        items = {n[len("assets/minecraft/models/item/"):-5] for n in names
                 if n.startswith("assets/minecraft/models/item/") and n.endswith(".json")}
    if items:
        out["item"] = items
    if "assets/minecraft/lang/en_us.json" in names:
        lang = json.loads(zf.read("assets/minecraft/lang/en_us.json").decode("utf-8"))
        ents = {k[len("entity.minecraft."):] for k in lang
                if k.startswith("entity.minecraft.") and "." not in k[len("entity.minecraft."):]}
        if ents:
            out["entity_type"] = ents
    for reg in registries:
        if reg in out:
            continue
        pre = f"data/minecraft/{reg}/"
        ids = {n[len(pre):-5] for n in names if n.startswith(pre) and n.endswith(".json")}
        if ids:
            out[reg] = ids
    return out


class Contents:
    """a registry's ids in one version; None = no source in the jar, taken to hold every id"""

    def __init__(self, ids):
        self.ids = ids

    def __contains__(self, i):
        return self.ids is None or i in self.ids


# ---------------------------------------------------------------- class files

class ClassFile:
    def __init__(self, b):
        self.b = b
        if struct.unpack_from(">I", b, 0)[0] != 0xCAFEBABE:
            raise ValueError("not a class file")
        n = struct.unpack_from(">H", b, 8)[0]
        cp = [None] * n
        i, p = 1, 10
        while i < n:
            t = b[p]
            if t == 1:
                ln = struct.unpack_from(">H", b, p + 1)[0]
                cp[i] = (1, b[p + 3:p + 3 + ln].decode("utf-8", "replace"))
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
                raise ValueError(f"bad constant tag {t}")
            i += 1
        self.cp = cp
        self.name = self.cls(struct.unpack_from(">H", b, p + 2)[0])
        p += 6
        ni = struct.unpack_from(">H", b, p)[0]
        p += 2 + 2 * ni
        self.fields = []
        p = self._members(p, self.fields)
        self.methods = []
        self._members(p, self.methods)

    def utf(self, i):
        return self.cp[i][1]

    def cls(self, i):
        return self.utf(self.cp[i][1])

    def ref(self, i):
        """(owner, name, desc) of a Field/Method/InterfaceMethod ref"""
        _, c, nt = self.cp[i]
        _, n, d = self.cp[nt]
        return self.cls(c), self.utf(n), self.utf(d)

    def _members(self, p, out):
        n = struct.unpack_from(">H", self.b, p)[0]
        p += 2
        for _ in range(n):
            acc, ni, di, na = struct.unpack_from(">HHHH", self.b, p)
            p += 8
            code = None
            for _ in range(na):
                an, al = struct.unpack_from(">HI", self.b, p)
                if self.utf(an) == "Code":
                    cl = struct.unpack_from(">I", self.b, p + 10)[0]
                    code = self.b[p + 14:p + 14 + cl]
                p += 6 + al
            out.append((acc, self.utf(ni), self.utf(di), code))
        return p


OPLEN = [1] * 256
for _op in (0x10, 0x12, 0xa9, 0xbc) + tuple(range(0x15, 0x1a)) + tuple(range(0x36, 0x3b)):
    OPLEN[_op] = 2
for _op in (0x11, 0x13, 0x14, 0x84, 0xbb, 0xbd, 0xc0, 0xc1, 0xc6, 0xc7) + tuple(range(0x99, 0xa9)) \
        + tuple(range(0xb2, 0xb9)):
    OPLEN[_op] = 3
OPLEN[0xc5] = 4
for _op in (0xb9, 0xba, 0xc8, 0xc9):
    OPLEN[_op] = 5


def instructions(code):
    p = 0
    while p < len(code):
        op = code[p]
        if op in (0xaa, 0xab):
            q = (p + 4) & ~3
            if op == 0xaa:
                lo, hi = struct.unpack_from(">ii", code, q + 4)
                ln = q + 12 + 4 * (hi - lo + 1) - p
            else:
                np_ = struct.unpack_from(">i", code, q + 4)[0]
                ln = q + 8 + 8 * np_ - p
        elif op == 0xc4:
            ln = 6 if code[p + 1] == 0x84 else 4
        else:
            ln = OPLEN[op]
        yield p, op, code[p:p + ln]
        p += ln


def arg_types(desc):
    return re.findall(r"\[*(?:L[^;]+;|[ZBCSIJFD])", desc[1:desc.index(")")])


class TagFields:
    """V's TagKey constants: {(registry, tag): [field label]} from net/minecraft/tags <clinit>s"""

    def __init__(self, data, v):
        self.zf = zipfile.ZipFile(code_jar(data, v))
        self.year = year_era(v)
        if self.year:
            self.tagkey = "net/minecraft/tags/TagKey"
            self.registries = "net/minecraft/core/registries/Registries"
            self.identifier = "net/minecraft/resources/Identifier"
            self.mojang = {}
            holders = [n[:-6] for n in self.zf.namelist()
                       if n.startswith("net/minecraft/tags/") and n.endswith(".class")]
        else:
            self.tagkey = "net/minecraft/class_6862"
            self.registries = "net/minecraft/class_7924"
            self.identifier = "net/minecraft/class_2960"
            self.mojang = {}
            maps = os.path.join(data, "mappings")
            holders = []
            for line in open(os.path.join(maps, "inter-to-mojang-classes.tsv"), encoding="utf-8"):
                f = line.rstrip("\n").split("\t")
                if len(f) >= 3 and f[0] == "C" and f[2].startswith("net/minecraft/tags/"):
                    holders.append(f[1])
                    self.mojang[f[1]] = f[2].rsplit("/", 1)[1]
            for line in open(os.path.join(maps, "inter-to-mojang-1.21.11.tsv"), encoding="utf-8"):
                if line.startswith("F\tnet/minecraft/class_"):
                    f = line.rstrip("\n").split("\t")
                    self.mojang[f[1]] = f[2].rsplit("/", 1)[1]
        self.classes = {}
        self.values = {}       # (owner, field) -> value
        self.reg_ids = self._registry_ids()
        self.by_tag = defaultdict(list)
        for h in holders:
            if f"{h}.class" in self.zf.namelist():
                self._eval(h)
        for (owner, field), val in self.values.items():
            if val and val[0] == "tag" and val[1]:
                self.by_tag[(val[1], val[2])].append(self._label(owner, field))

    def _label(self, owner, field):
        if self.year:
            return f"{owner.rsplit('/', 1)[1]}.{field}"
        lab = f"{owner.rsplit('/', 1)[1]}.{field}"
        mj = self.mojang.get(f"{owner}.{field}")
        return f"{lab}({mj})" if mj else lab

    def _class(self, name):
        if name not in self.classes:
            try:
                self.classes[name] = ClassFile(self.zf.read(f"{name}.class"))
            except KeyError:
                self.classes[name] = None
        return self.classes[name]

    def _registry_ids(self):
        cf = self._class(self.registries)
        out = {}
        for _, name, _, code in cf.methods:
            if name == "<clinit>" and code:
                last = None
                for _, op, ins in instructions(code):
                    if op in (0x12, 0x13):
                        i = ins[1] if op == 0x12 else struct.unpack_from(">H", ins, 1)[0]
                        last = self.cf_string(cf, i)
                    elif op == 0xb3:
                        owner, f, _ = cf.ref(struct.unpack_from(">H", ins, 1)[0])
                        if owner == self.registries and last:
                            out[f] = strip_ns(last)
                        last = None
        return out

    @staticmethod
    def cf_string(cf, i):
        e = cf.cp[i]
        return cf.utf(e[1]) if e and e[0] == 8 else None

    def _method(self, owner, name, desc):
        cf = self._class(owner)
        if cf:
            for acc, mn, md, code in cf.methods:
                if mn == name and md == desc and code:
                    return cf, acc, code
        return None, 0, None

    def _eval(self, owner):
        """symbolic run of owner.<clinit>, recording what each static field holds"""
        if ("<done>", owner) in self.values:
            return
        self.values[("<done>", owner)] = None
        cf, _, code = self._method(owner, "<clinit>", "()V")
        if code:
            self._run(cf, code, [], 0, clinit=owner)

    def _run(self, cf, code, local, depth, clinit=None):
        """Interprets straight-line code over symbolic values: ("str", s), ("reg", registry),
        ("tag", registry, name), ("obj", class, {field: value}); None = unknown. Calls into
        net/minecraft methods are followed (TagKey factories, BlockItemTagId records and their
        accessors); TagKey.create(registryKey, id) makes the tag; Identifier factories pass their path
        through. Outside <clinit> an instruction it does not model gives up (None); inside <clinit> it
        clears the stack, the next constant starting afresh."""
        local = list(local) + [None] * 8
        stack = []
        for _, op, ins in instructions(code):
            u2 = struct.unpack_from(">H", ins, 1)[0] if len(ins) >= 3 else None
            if op == 0x01:
                stack.append(None)
            elif op in (0x12, 0x13):
                s = self.cf_string(cf, ins[1] if op == 0x12 else u2)
                stack.append(("str", s) if s is not None else None)
            elif 0x2a <= op <= 0x2d or op == 0x19:
                i = op - 0x2a if op != 0x19 else ins[1]
                stack.append(local[i] if i < len(local) else None)
            elif 0x4b <= op <= 0x4e or op == 0x3a:
                i = op - 0x4b if op != 0x3a else ins[1]
                v = stack.pop() if stack else None
                if i < len(local):
                    local[i] = v
            elif op == 0x59 and stack:
                stack.append(stack[-1])
            elif op == 0x57 and stack:
                stack.pop()
            elif op == 0xbb:
                stack.append(("obj", cf.cls(u2), {}))
            elif op == 0xb2:
                o, f, _ = cf.ref(u2)
                if o == self.registries:
                    stack.append(("reg", self.reg_ids.get(f)))
                else:
                    self._eval(o)
                    stack.append(self.values.get((o, f)))
            elif op == 0xb3:
                o, f, _ = cf.ref(u2)
                v = stack.pop() if stack else None
                if clinit and o == clinit:
                    self.values[(o, f)] = v
                if clinit:
                    stack = []
            elif op == 0xb4:
                _, f, _ = cf.ref(u2)
                obj = stack.pop() if stack else None
                stack.append(obj[2].get(f) if obj and obj[0] == "obj" else None)
            elif op == 0xb5:
                _, f, _ = cf.ref(u2)
                v = stack.pop() if stack else None
                obj = stack.pop() if stack else None
                if obj and obj[0] == "obj":
                    obj[2][f] = v
            elif op in (0xb6, 0xb7, 0xb8):
                o, n, d = cf.ref(u2)
                types = arg_types(d)
                k = len(types) + (0 if op == 0xb8 else 1)
                if len(stack) < k:
                    if clinit:
                        stack = []
                        continue
                    return None
                args = stack[len(stack) - k:]
                del stack[len(stack) - k:]
                if op == 0xb7 and n == "<init>" and o == self.identifier:
                    # new Identifier(path) / new Identifier(namespace, path): the object is its path
                    strs = [a[1] for a in args[1:] if a and a[0] == "str"]
                    path = ("str", strs[-1]) if strs and len(strs) == len(args) - 1 else None
                    stack = [path if s is args[0] else s for s in stack]
                    continue
                r = self._invoke(o, n, d, args, types, op == 0xb8, depth)
                if not d.endswith(")V"):
                    stack.append(r)
            elif op == 0xb0:
                return stack.pop() if stack else None
            elif op == 0xb1:
                return None
            elif clinit:
                stack = []
            else:
                return None
        return None

    def _invoke(self, o, n, d, args, types, static, depth):
        if o == self.tagkey and static and len(args) == 2:
            reg, ident = args
            if reg and reg[0] == "reg" and ident and ident[0] == "str":
                return ("tag", reg[1], strip_ns(ident[1]))
            return None
        if o == self.identifier:
            strs = [a[1] for a in args if a and a[0] == "str"]
            return ("str", strs[-1]) if static and strs and len(strs) == len(args) else None
        if not o.startswith("net/minecraft/") or depth > 8:
            return None
        cf, _, code = self._method(o, n, d)
        if not code:
            return None
        local = [] if static else [args[0]]
        for t, a in zip(types, args if static else args[1:]):
            local.append(a)
            if t in ("J", "D"):
                local.append(None)
        return self._run(cf, code, local, depth + 1)


# ---------------------------------------------------------------- one version

class Version:
    def __init__(self, data, v):
        self.v = v
        self.jar = data_jar(data, v)
        with zipfile.ZipFile(self.jar) as zf:
            self.tags = read_tags(zf)
            self.packs, self.pack_tags = with_feature_packs(zf, self.tags)
            regs = {r for r, _ in self.tags}
            self.real = registry_contents(zf, regs)
            self.names = zf.namelist()
        self.registries = regs
        self._fields = None
        self.data = data

    def cover(self, regs):
        """contents for registries the other version tags (a data directory, else code constants)"""
        for r in regs - set(self.real):
            pre = f"data/minecraft/{r}/"
            ids = {n[len(pre):-5] for n in self.names if n.startswith(pre) and n.endswith(".json")}
            if not ids:
                ids = code_registry_ids(self.data, self.v, r)
                named = {ref for (rr, _), es in self.tags.items() if rr == r for ref, is_tag, _ in es if not is_tag}
                if ids is not None and not named <= ids:
                    # the holder no longer spells every id (moved to another class): unknown, not guessed
                    print(f"warning: {self.v} {r}: code holder misses {sorted(named - ids)[:5]}; contents unknown",
                          file=sys.stderr)
                    ids = None
            if ids:
                self.real[r] = ids

    def fields(self):
        if self._fields is None:
            self._fields = TagFields(self.data, self.v)
        return self._fields


def compare(V, W):
    """rows, proxied registries"""
    regs = V.registries | W.registries
    V.cover(regs)
    W.cover(regs)
    # a side without a real source for a registry is taken to hold every id: a registry change is
    # claimed only where it is read off the jar, never guessed
    vc = {r: Contents(V.real.get(r)) for r in regs}
    wc = {r: Contents(W.real.get(r)) for r in regs}
    unknown = sorted(f"{r}({'V' if r not in V.real else ''}{'W' if r not in W.real else ''})"
                     for r in regs if r not in V.real or r not in W.real)
    vs = resolve_all(V.tags, V.real)
    ws = resolve_all(W.tags, W.real)
    # ids V had only behind a built-in feature pack (experimental content W released): V's tag with
    # every pack enabled already holds them
    vp = resolve_all(V.pack_tags, V.real) if V.packs else vs
    # ... and ids that only V's feature packs name at all (the experimental block itself: 1.20.x's
    # copper_door is in update_1_21's tags and in no base tag)
    base_named, pack_named = defaultdict(set), defaultdict(set)
    for (r, _), s in vs.items():
        base_named[r] |= s
    for (r, _), s in vp.items():
        pack_named[r] |= s
    pack_only = {r: pack_named[r] - base_named[r] for r in pack_named}
    fields = V.fields().by_tag
    w_by_set = defaultdict(list)
    for k, s in ws.items():
        if s:
            w_by_set[(k[0], s)].append(k[1])

    def mark(ids, other_registry, flagged=frozenset()):
        return ",".join("*" + i if i not in other_registry else "~" + i if i in flagged else i
                        for i in sorted(ids)) or "-"

    rows = []
    for key in sorted(vs):
        reg, tag = key
        a = vs[key]
        gone = {i for i in a if i not in wc[reg]}
        flagged = frozenset()
        if key not in ws:
            kind, rem, add = "removed", a, frozenset()
        else:
            b = ws[key]
            rem, add = a - b, b - a
            if not rem and not add:
                continue
            flagged = add & (vp.get(key, frozenset()) | pack_only.get(reg, set()))
            real_rem = {i for i in rem if i in wc[reg]}
            real_add = {i for i in add if i in vc[reg] and i not in flagged}
            if real_rem and real_add:
                kind = "changed"
            elif real_rem:
                kind = "narrowed"
            elif real_add:
                kind = "widened"
            else:
                kind = "registry-only"
        succ = "-"
        if kind in ("removed", "narrowed") and a:
            cands = set(w_by_set.get((reg, a), [])) | set(w_by_set.get((reg, a - gone), []) if a - gone else [])
            cands.discard(tag)
            succ = ",".join(sorted(cands)) or "-"
        rows.append((reg, tag, kind, mark(rem, wc[reg]), mark(add, vc[reg], flagged), succ, ";".join(sorted(fields.get(key, []))) or "-"))
    return rows, unknown, len(set(vs) & set(ws)), len(set(vs) - set(ws))


HEADER = ("# vanilla data-tag drift {V} -> {W}, generated by core/tools/tag_drift.py from {vj} and {wj}\n"
          "# (data/minecraft/tags/**). removed/added ids: '*' = the id is absent from the other version's\n"
          "# registry (new or removed content, not drift of V's meaning); '~' = an added id V's tag already\n"
          "# held behind a built-in feature pack (experimental content W released; V packs: {packs});\n"
          "# registry-only = every changed id is '*' or '~'. successor: W tag(s) of the same registry whose\n"
          "# set equals V's set or V's set minus ids W's registry dropped (removed and narrowed rows only).\n"
          "# V field: the V TagKey constant (intermediary owner.field(1.21.11 Mojang name); Mojang names for\n"
          "# 26.x), '-' = reachable from json only. tags compared: {n}; removed in W: {nr}.\n"
          "# registries without a contents source in V/W (that side taken to hold every id, so no '*' from\n"
          "# it): {proxied}\n"
          "registry\ttag\tkind\tremoved ids\tadded ids\tsuccessor hint\tV field\n")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", default=os.path.join(ROOT, "data"))
    ap.add_argument("--out", default=os.path.join(ROOT, "core", "segments", "damage-tags"))
    ap.add_argument("--step", nargs=2)
    ap.add_argument("--calibrate", action="store_true")
    a = ap.parse_args()
    rel = releases()
    cache = {}

    def ver(v):
        if v not in cache:
            cache[v] = Version(a.data, v)
        return cache[v]

    if a.calibrate:
        bad = 0
        for v in rel:
            rows, _, n, _ = compare(ver(v), ver(v))
            print(f"{v} vs {v}: {n} tags compared, {len(rows)} rows")
            bad += len(rows)
        sys.exit(1 if bad else 0)

    steps = [tuple(a.step)] if a.step else list(zip(rel, rel[1:]))
    os.makedirs(a.out, exist_ok=True)
    summary = []
    for v, w in steps:
        V, W = ver(v), ver(w)
        rows, proxied, n, nr = compare(V, W)
        path = os.path.join(a.out, f"{v}__{w}.tsv")
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            f.write(HEADER.format(V=v, W=w, vj="data/jars/" + os.path.basename(V.jar), wj="data/jars/" + os.path.basename(W.jar),
                                   n=n, nr=nr,
                                  proxied=",".join(proxied) or "none", packs=",".join(V.packs) or "none"))
            for r in rows:
                f.write("\t".join(r) + "\n")
        counts = {k: sum(1 for r in rows if r[2] == k) for k in KINDS}
        withf = sum(1 for r in rows if r[2] != "registry-only" and r[6] != "-")
        summary.append((v, w, n + nr, counts, withf))
        print(f"{v}__{w}: compared {n + nr} " + " ".join(f"{k}={counts[k]}" for k in KINDS)
              + f" with-V-field(non-registry-only)={withf}")
        cache.pop(v, None)
    if not a.step:
        with open(os.path.join(a.out, "summary.tsv"), "w", encoding="utf-8", newline="\n") as f:
            f.write("# per-step vanilla data-tag drift, generated by core/tools/tag_drift.py; tags compared = "
                    "V tags (present in both or removed in W); with V field = drift rows (not registry-only) "
                    "whose tag V code reads through a TagKey constant\n")
            f.write("step\ttags compared\t" + "\t".join(KINDS) + "\twith V field\n")
            for v, w, n, c, wf in summary:
                f.write(f"{v}__{w}\t{n}\t" + "\t".join(str(c[k]) for k in KINDS) + f"\t{wf}\n")


if __name__ == "__main__":
    main()
